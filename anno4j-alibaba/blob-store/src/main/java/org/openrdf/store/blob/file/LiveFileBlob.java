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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openrdf.store.blob.BlobObject;
import org.openrdf.store.blob.BlobVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commits changes to a blob as soon as OutputStream is closed.
 */
public class LiveFileBlob extends BlobObject {
	private final Logger logger = LoggerFactory.getLogger(LiveFileBlob.class);
	private final FileBlobStore store;
	private final String uri;

	protected LiveFileBlob(FileBlobStore store, String uri) throws IOException {
		super(uri);
		assert store != null;
		assert uri != null;
		this.store = store;
		this.uri = uri;
	}

	public OutputStream openOutputStream() throws IOException {
		final BlobVersion version = store.newVersion();
		final BlobObject delegate = version.open(uri);
		OutputStream out = delegate.openOutputStream();
		return new FilterOutputStream(out) {
			private boolean closed;

			public void write(int b) throws IOException {
				try {
					out.write(b);
				} catch (IOException e) {
					rollback();
					throw e;
				}
			}

			public void write(byte[] b, int off, int len) throws IOException {
				try {
					out.write(b, off, len);
				} catch (IOException e) {
					rollback();
					throw e;
				}
			}

			public void close() throws IOException {
				out.close();
				if (!closed) {
					try {
						version.commit();
					} finally {
						rollback();
					}
				}
			}

			private synchronized void rollback() {
				try {
					if (!closed) {
						closed = true;
						version.rollback();
					}
				} catch (IOException e) {
					logger.error(e.toString(), e);
				}
			}
		};
	}

	public boolean delete() {
		store.lock();
		try {
			BlobVersion version = store.newVersion();
			boolean ret = version.open(uri).delete();
			version.prepare();
			try {
				version.commit();
				version = null;
				return ret;
			} finally {
				if (version != null) {
					version.rollback();
				}
			}
		} catch (IOException exc) {
			logger.error(exc.toString(), exc);
			return false;
		} finally {
			store.unlock();
		}
	}

	public long getLength() throws IOException {
		BlobVersion version = store.newVersion();
		return version.open(uri).getLastModified();
	}

	public long getLastModified() {
		try {
			BlobVersion version = store.newVersion();
			return version.open(uri).getLastModified();
		} catch (IOException exc) {
			logger.error(exc.toString(), exc);
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LiveFileBlob other = (LiveFileBlob) obj;
		if (!uri.equals(other.uri))
			return false;
		if (!store.equals(other.store))
			return false;
		return true;
	}

	public String[] getRecentVersions() throws IOException {
		return store.newVersion().open(uri).getRecentVersions();
	}

	public String getCommittedVersion() throws IOException {
		return store.newVersion().open(uri).getCommittedVersion();
	}

	public InputStream openInputStream() throws IOException {
		return store.newVersion().open(uri).openInputStream();
	}
}
