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
package org.openrdf.store.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;

import javax.tools.FileObject;

/**
 * A blob is a stream of binary data stored as a single value with a unique key
 * within any single transaction of the store. Blobs are typically documents,
 * images, audio or other files.
 * 
 * @author James Leigh
 * 
 */
public abstract class BlobObject implements FileObject {
	private final String uri;

	public BlobObject(String uri) {
		assert uri != null;
		this.uri = uri;
	}

	/**
	 * Most recent version identifiers that have committed modifications of this
	 * blob. The first identifier in the response is the most recent.
	 */
	public abstract String[] getRecentVersions() throws IOException;

	/**
	 * Identifier of the most recent committed blob version that is read by this
	 * {@link BlobObject}. This method can be used after committing a
	 * {@link BlobVersion} to retrieve the committed version identifier, which
	 * can later be passed to {@link BlobStore#openVersion(String)} to reread
	 * this version of this blob.
	 * 
	 * @return version identifier or null if this blob has no version
	 */
	public abstract String getCommittedVersion() throws IOException;

	/**
	 * Returns the number of bytes in the BLOB value designated by this Blob
	 * object.
	 * 
	 * @return length of the BLOB in bytes
	 * @throws IOException
	 */
	public abstract long getLength() throws IOException;

	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		Reader reader = openReader(ignoreEncodingErrors);
		if (reader == null)
			return null;
		try {
			StringWriter writer = new StringWriter();
			int read;
			char[] cbuf = new char[1024];
			while ((read = reader.read(cbuf)) >= 0) {
				writer.write(cbuf, 0, read);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	public String getName() {
		String uri = toUri().getPath();
		int last = uri.length() - 1;
		int idx = uri.lastIndexOf('/', last - 1) + 1;
		if (idx > 0 && uri.charAt(last) != '/')
			return uri.substring(idx);
		if (idx > 0 && idx != last)
			return uri.substring(idx, last);
		return uri;
	}

	public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
		InputStream in = openInputStream();
		if (in == null)
			return null;
		return new InputStreamReader(in);
	}

	public Writer openWriter() throws IOException {
		OutputStream out = openOutputStream();
		if (out == null)
			return null;
		return new OutputStreamWriter(out);
	}

	public URI toUri() {
		return URI.create(uri);
	}

	public final String toString() {
		return uri.toString();
	}

	public abstract boolean equals(Object obj);

}
