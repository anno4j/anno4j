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
import java.io.OutputStream;
import java.io.Writer;

/**
 * Managements {@link BlobVersion}.
 * 
 * @see BlobStoreFactory
 * 
 * @author James Leigh
 * 
 */
public interface BlobStore {

	/**
	 * Opens a {@link BlobObject} for reading or writing. Any changes to this
	 * blob are visible to other blobs of the same <code>uri</code> when a call
	 * to the {@link BlobObject#delete()} returns or a call to the
	 * {@link BlobObject#openWriter()}'s {@link Writer#close()} returns or a
	 * call to the {@link BlobObject#openOutputStream()}'s
	 * {@link OutputStream#close()} returns.
	 */
	BlobObject open(String uri) throws IOException;

	/**
	 * Create a new {@link BlobVersion} using a generated version ID.
	 * 
	 * @return a new BlobTransaction
	 */
	BlobVersion newVersion() throws IOException;

	/**
	 * Create a new {@link BlobVersion} maybe with the given unique ID.
	 * {@link BlobVersion} returned from this method using a previously assigned
	 * version have undefined consequences.
	 * 
	 * @param version
	 *            to uniquely identify new blob versions, this may or may not
	 *            actually be used
	 * @return a new BlobTransaction
	 */
	BlobVersion newVersion(String version) throws IOException;

	/**
	 * Open a read-only {@link BlobVersion} of the blob(s) with this version.
	 * Only blobs with the given version are accessible using the returned
	 * {@link BlobVersion}.
	 * 
	 * @param version
	 *            version of blob to read
	 * @return a closed {@link BlobVersion}
	 * @throws IllegalArgumentException
	 *             if the version is not a valid version of at least one blob.
	 */
	BlobVersion openVersion(String version) throws IOException;

	/**
	 * Most recent blobs that have committed modifications. The first identifier
	 * in the response is the most recent blob to have been modified. Blobs
	 * appear for every recent modification.
	 */
	String[] getRecentModifications() throws IOException;

	/**
	 * Remove all blobs from all transactions and all the history.
	 * 
	 * @return <code>true</code> if the store was successfully deleted.
	 */
	boolean erase() throws IOException;
}
