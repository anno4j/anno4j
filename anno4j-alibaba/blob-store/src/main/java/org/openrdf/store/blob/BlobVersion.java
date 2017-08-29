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

/**
 * Set of {@link BlobObject} modifications that were or will be saved together
 * to this {@link BlobStore}.
 * 
 * @author James Leigh
 * 
 */
public interface BlobVersion {

	/**
	 * Blobs that have been modified in this version.
	 */
	String[] getModifications() throws IOException;

	/**
	 * Opens a {@link BlobObject} for reading or writing if this
	 * {@link BlobVersion} has not be committed. Opens a read-only
	 * {@link BlobObject} using this version if this {@link BlobVersion} is
	 * closed.
	 * 
	 * @throws IllegalStateException
	 *             if this {@link BlobVersion} has been committed and the blob
	 *             is not listed in {@link #getModifications()}.
	 */
	BlobObject open(String uri) throws IOException, IllegalStateException;

	/**
	 * Prevents any further changes to the store from other threads until
	 * {@link #commit()} or {@link #rollback()} is called from this thread.
	 * Checks that the blobs read or written in this {@link BlobVersion} were
	 * not changed in another {@link BlobVersion} since they were opened.
	 * 
	 * @throws IOException
	 *             if a blob opened in this {@link BlobVersion} had since
	 *             changed.
	 */
	void prepare() throws IOException;

	/**
	 * Makes the changes to the open blobs in this {@link BlobVersion} available
	 * to others. This method can only be called at most once for a given
	 * {@link BlobVersion}; once this method is called this state is closed and
	 * only the modified blobs can be opened.
	 * 
	 * @throws IOException
	 *             if a blob opened in this {@link BlobVersion} had since
	 *             changed.
	 */
	void commit() throws IOException;

	/**
	 * Aborts all uncommitted changes to blobs, restoring the initial state of
	 * this {@link BlobVersion}.
	 */
	void rollback() throws IOException;

	/**
	 * Reverts all committed changes to blobs from this {@link BlobVersion}.
	 * 
	 * @return <code>true</code> if all blobs of this version were removed
	 *         successfully.
	 */
	boolean erase() throws IOException;
}
