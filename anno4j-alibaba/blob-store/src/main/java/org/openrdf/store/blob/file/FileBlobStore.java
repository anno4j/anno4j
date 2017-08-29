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
package org.openrdf.store.blob.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openrdf.store.blob.BlobObject;
import org.openrdf.store.blob.BlobStore;

/**
 * Set of blob objects that do not support version history.
 */
@Deprecated
public class FileBlobStore implements BlobStore {
	private final File dir;
	private final ReentrantReadWriteLock diskLock = new ReentrantReadWriteLock();
	private final Map<String, Set<FileListener>> listeners = new HashMap<String, Set<FileListener>>();

	public FileBlobStore(File dir) throws IOException {
		assert dir != null;
		this.dir = dir;
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
		FileBlobStore other = (FileBlobStore) obj;
		if (!dir.equals(other.dir))
			return false;
		return true;
	}

	public BlobObject open(String uri) throws IOException {
		return new LiveFileBlob(this, uri);
	}

	public String[] getRecentModifications() throws IOException {
		return new String[0];
	}

	public FileBlobVersion openVersion(String iri) throws IOException {
		throw new IllegalArgumentException("No history information is persisted in this store");
	}

	public FileBlobVersion newVersion() throws IOException {
		return new FileBlobVersion(this);
	}

	public FileBlobVersion newVersion(String iri) throws IOException {
		return new FileBlobVersion(this);
	}

	public boolean erase() throws IOException {
		lock();
		try {
			boolean result = true;
			File[] listFiles = dir.listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					if (!deltree(f)) {
						result = false;
					}
				}
			}
			return result;
		} finally {
			unlock();
		}
	}

	protected File getDirectory() {
		return dir;
	}

	protected void watch(String uri, FileListener listener) {
		synchronized (listeners) {
			Set<FileListener> set = listeners.get(uri);
			if (set == null) {
				listeners.put(uri, set = new HashSet<FileListener>());
			}
			set.add(listener);
		}
	}

	protected boolean unwatch(String uri, FileListener listener) {
		synchronized (listeners) {
			Set<FileListener> set = listeners.get(uri);
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

	protected void changed(Collection<String> blobs) throws IOException {
		for (String uri : blobs) {
			Set<FileListener> set = listeners.get(uri);
			if (set != null) {
				for (FileListener listener : set) {
					listener.changed(uri);
				}
			}
		}
	}

	private boolean deltree(File directory) {
		if (directory == null || !directory.exists()) {
			return true;
		}

		boolean result = true;
		if (directory.isFile()) {
			result = directory.delete();
		}
		else {
			for (File f : directory.listFiles()) {
				if (!deltree(f)) {
					result = false;
				}
			}
			if (!directory.delete()) {
				result = false;
			}
		}
		return result;
	}

}
