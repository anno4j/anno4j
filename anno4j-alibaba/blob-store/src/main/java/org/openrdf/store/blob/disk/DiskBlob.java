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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openrdf.store.blob.BlobObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskBlob extends BlobObject implements DiskListener {
	private static final int MAX_HISTORY = 1000;
	private static final byte[] EMPTY_SHA1;
	static {
		try {
			EMPTY_SHA1 = MessageDigest.getInstance("SHA1").digest();
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}
	}

	private interface Closure<V> {
		V call(String name, long length, byte[] sha1, String iri);
	};

	private final Logger logger = LoggerFactory.getLogger(DiskBlob.class);
	private final DiskBlobVersion disk;
	private final String uri;
	final File dir;

	/** listening for changes by other transactions */
	private boolean open;
	/** Blob was changed and committed by another transaction */
	private volatile boolean changed;
	/** uncommitted delete of readFile */
	private boolean deleted;

	String readVersion;
	File readFile;
	boolean readCompressed;
	long readLength;
	byte[] readDigest;

	private File writeFile;
	private boolean writeCompressed;
	private long writeLength;
	private byte[] writeDigest;
	private OutputStream writeStream;

	protected DiskBlob(DiskBlobVersion disk, String uri) {
		super(uri);
		assert disk != null;
		assert uri != null;
		this.disk = disk;
		this.uri = uri;
		this.dir = new File(disk.getDirectory(), safe(uri));
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiskBlob other = (DiskBlob) obj;
		if (!uri.equals(other.uri))
			return false;
		if (!disk.equals(other.disk))
			return false;
		return true;
	}

	public synchronized String getCommittedVersion() throws IOException {
		init(false);
		return readVersion;
	}

	public synchronized String[] getRecentVersions() throws IOException {
		init(false);
		final LinkedList<String> history = new LinkedList<String>();
		eachVersion(new Closure<Void>() {
			public Void call(String name, long length, byte[] sha1, String iri) {
				history.addFirst(iri);
				if (history.size() > MAX_HISTORY) {
					history.removeLast();
				}
				return null;
			}
		});
		return history.toArray(new String[history.size()]);
	}

	public synchronized boolean delete() {
		try {
			init(true);
		} catch (IOException e) {
			logger.error(e.toString(), e);
			return false;
		}
		Lock read = disk.readLock();
		try {
			read.lock();
			deleted = readFile != null && readFile.exists()
					|| writeFile != null && writeFile.exists()
					&& writeFile.getParentFile().canWrite();
			if (writeFile != null) {
				return deleteWriteFile();
			} else {
				return deleted;
			}
		} catch (IOException e) {
			logger.error(e.toString(), e);
			return false;
		} finally {
			read.unlock();
		}
	}

	public synchronized long getLength() throws IOException {
		init(false);
		if (deleted)
			return 0;
		if (writeFile != null)
			return writeLength;
		return readLength;
	}

	public synchronized long getLastModified() {
		try {
			init(false);
		} catch (IOException e) {
			logger.error(e.toString(), e);
			return 0;
		}
		if (deleted)
			return 0;
		if (writeFile != null)
			return writeFile.lastModified();
		if (readFile == null)
			return 0;
		Lock read = disk.readLock();
		try {
			read.lock();
			return readFile.lastModified();
		} finally {
			read.unlock();
		}
	}

	public synchronized InputStream openInputStream() throws IOException {
		init(false);
		if (deleted)
			return null;
		if (writeFile != null && writeCompressed)
			return new GZIPInputStream(new FileInputStream(writeFile));
		if (writeFile != null)
			return new FileInputStream(writeFile);
		if (readFile == null)
			return null;
		Lock read = disk.readLock();
		try {
			read.lock();
			FileInputStream fin = new FileInputStream(readFile);
			if (readCompressed)
				return new GZIPInputStream(fin);
			return fin;
		} finally {
			read.unlock();
		}
	}

	public synchronized OutputStream openOutputStream() throws IOException {
		init(true);
		if (writeFile == null) {
			writeFile = new File(dir, newWriteFileName());
			writeCompressed = readCompressed || readFile.length() <= 512;
			writeLength = 0;
			writeDigest = EMPTY_SHA1;
		}
		OutputStream out = disk.openOutputStream(writeFile);
		if (writeCompressed) {
			out = new GZIPOutputStream(out);
		}
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException exc) {
			throw new AssertionError(exc);
		}
		return writeStream = new FilterOutputStream(out) {
			private long size = 0;
			private IOException fatal;
			private boolean closed;

			public void write(int b) throws IOException {
				try {
					out.write(b);
					size++;
					md.update((byte) b);
				} catch (IOException e) {
					fatal = e;
					throw e;
				}
			}

			public void write(byte[] b, int off, int len) throws IOException {
				try {
					out.write(b, off, len);
					size += len;
					md.update(b, off, len);
				} catch (IOException e) {
					fatal = e;
					throw e;
				}
			}

			public void close() throws IOException {
				if (!closed) {
					closed = true;
					super.close();
					written(fatal == null, size, md.digest(), this);
				}
			}
		};
	}

	public void changed(String uri) {
		changed = true;
	}

	protected synchronized boolean hasConflict() {
		return changed;
	}

	protected synchronized boolean isChangePending() {
		return deleted || writeFile != null;
	}

	protected synchronized boolean resync() throws IOException {
		final String erasing = disk.getVersion();
		filterVersion(new Closure<Boolean>() {
			public Boolean call(String name, long length, byte[] sha1,
					String iri) {
				return !iri.equals(erasing);
			}
		});
		return sync();
	}

	protected synchronized boolean sync() throws IOException {
		if (!open)
			return false;
		if (writeStream != null) {
			// write stream was aborted
			deleteWriteFile();
		}
		try {
			String iri = disk.getVersion();
			if (deleted) {
				appendIndexFile(null, 0, EMPTY_SHA1, iri);
				readVersion = iri;
				return true;
			} else if (writeFile != null) {
				if (writeCompressed && writeFile.length() >= writeLength / 2) {
					uncompress(writeFile);
					writeCompressed = false;
				}
				appendIndexFile(writeFile, writeLength, writeDigest, iri);
				readVersion = iri;
				readFile = writeFile;
				readCompressed = writeCompressed;
				readLength = writeLength;
				readDigest = writeDigest;
				return true;
			}
			return false;
		} finally {
			if (open) {
				disk.unwatch(uri, this);
				open = false;
				changed = false;
				writeFile = null;
				deleted = false;
			}
		}
	}

	protected synchronized void abort() {
		if (open) {
			disk.unwatch(uri, this);
			open = false;
			changed = false;
			deleted = false;
			try {
				deleteWriteFile();
			} catch (IOException e) {
				logger.error(e.toString(), e);
			}
		}
	}

	protected synchronized boolean erase() throws IOException {
		final String erasing = disk.getVersion();
		return filterVersion(new Closure<Boolean>() {
			public Boolean call(String name, long length, byte[] sha1,
					String iri) {
				if (iri.equals(erasing) && name.length() > 0) {
					File file = new File(dir, name);
					file.delete();
					File d = file.getParentFile();
					String[] dlist = d.list();
					if (dlist != null && dlist.length == 0) {
						d.delete();
					}
					String[] plist = d.getParentFile().list();
					if (plist != null && plist.length == 0) {
						d.getParentFile().delete();
					}
					return false;
				}
				return !iri.equals(erasing);
			}
		});
	}

	synchronized void written(boolean success, long size,
			byte[] digest, OutputStream stream) throws IOException {
		if (success) {
			if (readFile != null && !readFile.equals(writeFile)
					&& readLength == size
					&& MessageDigest.isEqual(readDigest, digest)) {
				// no change to file
				deleteWriteFile();
			}
			deleted = false;
			writeLength = size;
			writeDigest = digest;
		} else {
			deleteWriteFile();
		}
		if (stream == writeStream) {
			writeStream = null;
		}
	}

	private synchronized boolean filterVersion(final Closure<Boolean> closure) throws IOException {
		final AtomicBoolean erased = new AtomicBoolean(false);
		final File rest = new File(dir, getIndexFileName(disk.getVersion()
				.hashCode()));
		final PrintWriter writer = new PrintWriter(disk.openWriter(rest, false));
		try {
			eachVersion(new Closure<Void>() {
				public Void call(String name, long length, byte[] sha1, String iri) {
					if (closure.call(name, length, sha1, iri)) {
						writer.print(name);
						writer.print(' ');
						writer.print(Long.toString(length));
						writer.print(' ');
						writer.print(Hex.encodeHex(sha1));
						writer.print(' ');
						writer.println(iri);
					} else {
						erased.set(true);
					}
					return null;
				}
			});
		} finally {
			writer.close();
		}
		if (erased.get()) {
			File index = new File(dir, getIndexFileName(null));
			index.delete();
			if (rest.length() > 0) {
				rest.renameTo(index);
			} else {
				rest.delete();
				File parent = dir;
				while (!parent.equals(disk.getDirectory()) && parent.delete()) {
					parent = parent.getParentFile();
				}
			}
			return true;
		} else {
			rest.delete();
		}
		return false;
	}

	private void uncompress(File file) throws IOException {
		File dir = file.getParentFile();
		File gz = new File(dir, file.getName() + ".gz");
		if (!file.renameTo(gz))
			throw new IOException("Cannot rename " + file);
		try {
			InputStream in = new GZIPInputStream(new FileInputStream(gz));
			try {
				OutputStream out = disk.openOutputStream(file);
				try {
					int read;
					byte[] buf = new byte[512];
					while ((read = in.read(buf)) >= 0) {
						out.write(buf, 0, read);
					}
				} finally {
					out.close();
				}
			} finally {
				in.close();
			}
		} finally {
			gz.delete();
		}
	}

	private boolean deleteWriteFile() throws IOException {
		if (writeStream != null) {
			writeStream.close();
			writeStream = null;
		}
		if (writeFile != null && writeFile.delete()) {
			File d = writeFile.getParentFile();
			while (!d.equals(disk.getDirectory()) && d.delete()) {
				d = d.getParentFile();
			}
			writeFile = null;
			return true;
		}
		return false;
	}

	private void init(boolean write) throws IOException {
		if (!open) {
			open = true;
			disk.watch(uri, this);
		}
		if (readDigest == null) {
			Lock readLock = disk.readLock();
			try {
				readLock.lock();
				initReadWriteFile();
			} finally {
				readLock.unlock();
			}
		}
	}

	private void initReadWriteFile() throws IOException {
		final String current = disk.getVersion();
		readVersion = null;
		readFile = null;
		readLength = 0;
		readDigest = EMPTY_SHA1;
		eachVersion(new Closure<String>() {
			public String call(String name, long l, byte[] d, String iri) {
				readVersion = iri;
				if (name.length() == 0) {
					readFile = null;
				} else {
					readFile = new File(dir, name);
				}
				readLength = l;
				readDigest = d;
				if (iri.equals(current)) {
					return name; // break;
				}
				return null;
			}
		});
		readCompressed = readFile == null || readLength > readFile.length();
	}

	private String newWriteFileName() throws IOException {
		final String current = disk.getVersion();
		int code = current.hashCode();
		String name;
		Boolean conflict;
		do {
			final String cname = name = getLocalName(code++);
			conflict = eachVersion(new Closure<Boolean>() {
				public Boolean call(String name, long length, byte[] sha1, String iri) {
					if (name.equals(cname) && !iri.equals(current))
						return Boolean.TRUE; // continue;
					return null;
				}
			});
		} while (conflict != null && conflict);
		return name;
	}

	private <V> V eachVersion(Closure<V> closure) throws IOException {
		Lock read = disk.readLock();
		try {
			read.lock();
			File index = new File(dir, getIndexFileName(null));
			if (!index.exists())
				return null;
			BufferedReader reader = new BufferedReader(new FileReader(index));
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					try {
						String[] split = line.split("\\s+", 4);
						String name = split[0];
						Long length = Long.valueOf(split[1]);
						byte[] sha1 = Hex.decodeHex(split[2].toCharArray());
						String iri = split[3];
						V ret = closure.call(name, length, sha1, iri);
						if (ret != null)
							return ret;
					} catch (DecoderException e) {
						logger.error(line, e);
					} catch (ArrayIndexOutOfBoundsException e) {
						logger.error(line, e);
					}
				}
			} finally {
				reader.close();
			}
		} catch (FileNotFoundException e) {
			// same as empty file
		} finally {
			read.unlock();
		}
		return null;
	}

	private void appendIndexFile(File file, long length, byte[] sha1, String iri)
			throws IOException {
		assert sha1 != null && sha1.length > 0;
		File index = new File(dir, getIndexFileName(null));
		PrintWriter writer = new PrintWriter(disk.openWriter(index, true));
		try {
			if (file != null) {
				String jpath = dir.getAbsolutePath();
				String path = file.getAbsolutePath();
				if (path.startsWith(jpath)
						&& path.charAt(jpath.length()) == File.separatorChar) {
					path = path.substring(jpath.length() + 1);
				} else {
					throw new AssertionError("Invalid blob entry path: " + path);
				}
				writer.print(path.replace(File.separatorChar, '/'));
			}
			writer.print(' ');
			writer.print(Long.toString(length));
			writer.print(' ');
			writer.print(Hex.encodeHex(sha1));
			writer.print(' ');
			writer.println(iri);
		} finally {
			writer.close();
		}
	}

	private String safe(String path) {
		if (path == null)
			return "";
		path = path.replace('/', File.separatorChar);
		path = path.replace('\\', File.separatorChar);
		path = path.replace(':', File.separatorChar);
		path = path.replaceAll("[^a-zA-Z0-9\\-./\\\\]", "_");
		return path.toLowerCase();
	}

	private String getIndexFileName(Integer code) {
		String name = Integer.toHexString(uri.hashCode());
		if (code == null)
			return "index$" + name;
		return "index$" + name + '-' + Integer.toHexString(code);
	}

	private String getLocalName(int code) {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(code));
		while (sb.length() < 8) {
			sb.insert(0, '0');
		}
		sb.insert(4, File.separatorChar);
		sb.append(File.separatorChar);
		sb.append('$');
		sb.append(Integer.toHexString(uri.hashCode()));
		return sb.toString();
	}

}
