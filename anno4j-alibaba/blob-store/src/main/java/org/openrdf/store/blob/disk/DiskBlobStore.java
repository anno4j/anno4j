/*
 * Copyright (c) 2011, 3 Round Stones Inc. Some rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution. 
 * - Neither the name of the openrdf.org nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.openrdf.store.blob.disk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openrdf.store.blob.BlobObject;
import org.openrdf.store.blob.BlobStore;

public class DiskBlobStore implements BlobStore {
	private static final int MAX_HISTORY = 1000;

	private interface Closure<V> {
		V call(String name, String iri) throws IOException;
	};

	private final File dir;
	final File journal;
	final String prefix;
	final AtomicLong seq = new AtomicLong(0);
	private final ReentrantReadWriteLock diskLock = new ReentrantReadWriteLock();
	private final Map<String, Set<DiskListener>> listeners = new HashMap<String, Set<DiskListener>>();
	/** version -> open DiskTransaction */
	private final Map<String, WeakReference<DiskBlobVersion>> transactions;

	public DiskBlobStore(File dir) throws IOException {
		assert dir != null;
		this.dir = dir;
		this.journal = new File(dir, "$versions");
		this.transactions = new WeakHashMap<String, WeakReference<DiskBlobVersion>>();
		this.prefix = new File(getDirectory(), "trx").toURI().toString();
		eachEntry(new Closure<Void>() {
			public Void call(String name, String iri) {
				if (iri.startsWith(prefix)) {
					try {
						String suffix = iri.substring(prefix.length());
						seq.set(Math.max(seq.get(), Long.parseLong(suffix)));
					} catch (NumberFormatException exc) {
						// ignore
					}
				}
				return null;
			}
		});
	}

	public String toString() {
		return dir.toString();
	}

	public int hashCode() {
		return dir.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiskBlobStore other = (DiskBlobStore) obj;
		if (!dir.equals(other.dir))
			return false;
		return true;
	}

	public BlobObject open(String uri) throws IOException {
		return new LiveDiskBlob(this, uri);
	}

	public DiskBlobVersion newVersion() throws IOException {
		return newVersion(prefix + seq.incrementAndGet());
	}

	public DiskBlobVersion newVersion(String version) throws IOException {
		synchronized (transactions) {
			WeakReference<DiskBlobVersion> ref = transactions.get(version);
			if (ref != null) {
				DiskBlobVersion result = ref.get();
				if (result != null)
					return result;
			}
			DiskBlobVersion result = new DiskBlobVersion(this, version, journal);
			ref = new WeakReference<DiskBlobVersion>(result);
			transactions.put(version, ref);
			return result;
		}
	}

	public DiskBlobVersion openVersion(final String version) throws IOException {
		File entry = eachEntry(new Closure<File>() {
			public File call(String name, String id) {
				if (id.equals(version))
					return new File(journal, name);
				return null;
			}
		});
		if (entry == null)
			throw new IllegalArgumentException("Unknown blob version: " + version);
		synchronized (transactions) {
			WeakReference<DiskBlobVersion> ref = transactions.get(version);
			if (ref != null) {
				DiskBlobVersion result = ref.get();
				if (result != null)
					return result;
			}
			DiskBlobVersion result = new DiskBlobVersion(this, version, entry);
			ref = new WeakReference<DiskBlobVersion>(result);
			transactions.put(version, ref);
			return result;
		}
	}

	public String[] getRecentModifications() throws IOException {
		Lock readLock = readLock();
		try {
			readLock.lock();
			final LinkedList<String> history = new LinkedList<String>();
			final Map<String, String> map = new HashMap<String, String>(MAX_HISTORY);
			eachEntry(new Closure<Void>() {
				public Void call(String name, String iri) {
					history.addFirst(name);
					if (history.size() > MAX_HISTORY) {
						history.removeLast();
						map.remove(name);
					}
					map.put(name, iri);
					return null;
				}
			});
			final LinkedList<String> blobs = new LinkedList<String>();
			for (String name : history) {
				String version = map.get(name);
				File entry = new File(journal, name);
				new DiskBlobVersion(this, version, entry).addOpenBlobs(blobs);
				if (blobs.size() >= MAX_HISTORY)
					break;
			}
			return blobs.toArray(new String[blobs.size()]);
		} finally {
			readLock.unlock();
		}
	}

	public boolean erase() throws IOException {
		File index = new File(journal, "index");
		File tmp = new File(journal, "index$");
		lock();
		try {
			new File(journal, "obsolete").delete();
			if (index.exists()) {
				copy(index, tmp, null);
			}
			eachEntry(tmp, new Closure<Void>() {
				public Void call(String name, String iri) throws IOException {
					openVersion(iri).erase();
					return null;
				}
			});
			return true;
		} finally {
			tmp.delete();
			String[] list = journal.list();
			if (list != null && list.length == 0) {
				journal.delete();
			}
			unlock();
		}
	}

	protected File getDirectory() {
		return dir;
	}

	protected boolean mkdirs(File dir) {
		if (dir.isDirectory())
			return false;
		mkdirs(dir.getParentFile());
		dir.mkdir();
		dir.setReadable(false, false);
		dir.setReadable(true);
		dir.setWritable(false, false);
		dir.setWritable(true);
		dir.setExecutable(false, false);
		dir.setExecutable(true);
		return dir.isDirectory();
	}

	protected OutputStream openOutputStream(File file) throws IOException {
		File dir = file.getParentFile();
		mkdirs(dir);
		if (!dir.canWrite() || file.exists() && !file.canWrite())
			throw new IOException("Cannot open blob file for writing");
		file.createNewFile();
		file.setReadable(false, false);
		file.setReadable(true);
		file.setWritable(false, false);
		file.setWritable(true);
		return new FileOutputStream(file);
	}

	protected Writer openWriter(File file, boolean append) throws IOException {
		File dir = file.getParentFile();
		mkdirs(dir);
		if (!dir.canWrite() || file.exists() && !file.canWrite())
			throw new IOException("Cannot open file for writing");
		file.createNewFile();
		file.setReadable(false, false);
		file.setReadable(true);
		file.setWritable(false, false);
		file.setWritable(true);
		return new FileWriter(file, append);
	}

	protected void watch(String uri, DiskListener listener) {
		synchronized (listeners) {
			Set<DiskListener> set = listeners.get(uri);
			if (set == null) {
				listeners.put(uri, set = new HashSet<DiskListener>());
			}
			set.add(listener);
		}
	}

	protected boolean unwatch(String uri, DiskListener listener) {
		synchronized (listeners) {
			Set<DiskListener> set = listeners.get(uri);
			if (set == null)
				return false;
			boolean ret = set.remove(listener);
			if (set.isEmpty()) {
				listeners.remove(uri);
			}
			return ret;
		}
	}

	protected Lock readLock() {
		return diskLock.readLock();
	}

	protected void lock() {
		diskLock.writeLock().lock();
	}

	protected void unlock() {
		diskLock.writeLock().unlock();
	}

	protected void changed(String version, Collection<String> blobs, File entry, Collection<String> previousVersions)
			throws IOException {
		Set<String> obsolete = new HashSet<String>();
		for (String previous : previousVersions) {
			if (previous != null && this.openVersion(previous).isObsolete()) {
				obsolete.add(previous);
			}
		}
		for (String uri : blobs) {
			Set<DiskListener> set = listeners.get(uri);
			if (set != null) {
				for (DiskListener listener : set) {
					listener.changed(uri);
				}
			}
		}
		if (!obsolete.isEmpty()) {
			appendObsolete(obsolete);
		}
	}

	protected void newBlobVersion(String version, File file) throws IOException {
		lock();
		try {
			File f = new File(journal, "index");
			PrintWriter index = new PrintWriter(openWriter(f, true));
			try {
				String jpath = journal.getAbsolutePath();
				String path = file.getAbsolutePath();
				if (path.startsWith(jpath) && path.charAt(jpath.length()) == File.separatorChar) {
					path = path.substring(jpath.length() + 1);
				} else {
					throw new AssertionError("Invalid version entry path: " + path);
				}
				index.print(path.replace(File.separatorChar, '/'));
				index.print(' ');
				index.println(version);
			} finally {
				index.close();
			}
		} finally {
			unlock();
		}
	}

	protected void removeFromIndex(String erasing) throws IOException {
		lock();
		try {
			File index = new File(journal, "index");
			File rest = new File(journal, "index$"
					+ Integer.toHexString(erasing.hashCode()));
			boolean empty = copy(index, rest, erasing);
			index.delete();
			if (empty) {
				rest.delete();
				String[] list = journal.list();
				if (list != null && list.length == 0) {
					journal.delete();
				}
			} else {
				rest.renameTo(index);
			}
		} finally {
			unlock();
		}
	}

	private boolean copy(File source, File destintation, String exclude)
			throws FileNotFoundException, IOException {
		boolean empty = true;
		BufferedReader reader = new BufferedReader(new FileReader(source));
		try {
			PrintWriter writer = new PrintWriter(openWriter(destintation, false));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String iri = line.substring(line.indexOf(' ') + 1);
					if (!iri.equals(exclude)) {
						writer.println(line);
						empty = false;
					}
				}
			} finally {
				writer.close();
			}
		} finally {
			reader.close();
		}
		return empty;
	}

	private void appendObsolete(Set<String> obsolete) throws IOException {
		lock();
		try {
			File f = new File(journal, "obsolete");
			PrintWriter index = new PrintWriter(openWriter(f, true));
			try {
				for (String o : obsolete) {
					index.println(o);
				}
			} finally {
				index.close();
			}
		} finally {
			unlock();
		}
	}

	private <V> V eachEntry(Closure<V> closure) throws IOException {
		return eachEntry(new File(journal, "index"), closure);
	}

	private <V> V eachEntry(File index, Closure<V> closure) throws IOException {
		Lock readLock = readLock();
		try {
			readLock.lock();
			if (!index.exists())
				return null;
			BufferedReader reader = new BufferedReader(new FileReader(index));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.split("\\s+", 2);
					V ret = closure.call(split[0], split[1]);
					if (ret != null)
						return ret;
				}
			} finally {
				reader.close();
			}
		} catch (FileNotFoundException e) {
			// same as empty file
		} finally {
			readLock.unlock();
		}
		return null;
	}

}
