/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
 * Copyright (c) 2011, Talis Inc. Some rights reserved.
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
package org.openrdf.repository.object;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareRepository;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.store.blob.BlobStore;
import org.openrdf.store.blob.BlobStoreFactory;

/**
 * Creates the {@link ObjectConnection} used to interact with the repository.
 * 
 * @author James Leigh
 * @author Steve Battle
 * 
 */
public class ObjectRepository extends ContextAwareRepository {
	private ObjectService service;
	private File dataDir;
	private String blobStoreUrl;
	private Map<String, String> blobStoreParameters;
	private BlobStore blobs;

	public ObjectRepository() throws ObjectStoreConfigException {
		this.service = new ObjectServiceImpl();
	}

	public ObjectRepository(ObjectService objectService) {
		this.service = objectService;
	}

	public ObjectService getObjectService() {
		return service;
	}

	public void setObjectService(ObjectService service) {
		this.service = service;
	}

	public synchronized String getBlobStoreUrl() {
		return blobStoreUrl;
	}

	public synchronized void setBlobStoreUrl(String blobStoreUrl) {
		this.blobStoreUrl = blobStoreUrl;
		blobs = null;
	}

	public synchronized Map<String, String> getBlobStoreParameters() {
		return blobStoreParameters;
	}

	public synchronized void setBlobStoreParameters(Map<String, String> blobStoreParameters) {
		this.blobStoreParameters = blobStoreParameters;
		blobs = null;
	}

	@Override
	public File getDataDir() {
		File dataDir = super.getDataDir();
		if (dataDir == null)
			return this.dataDir;
		return dataDir;
	}

	@Override
	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;
		super.setDataDir(dataDir);
	}

	@Override
	public synchronized void initialize() throws RepositoryException {
		super.initialize();
		try {
			// check setup
			getBlobStore();
		} catch (ObjectStoreConfigException e) {
			throw new RepositoryException(e);
		}
	}

	public ValueFactory getURIFactory() {
		return super.getValueFactory();
	}

	public ValueFactory getLiteralFactory() {
		return super.getValueFactory();
	}

	public synchronized BlobStore getBlobStore() throws ObjectStoreConfigException {
		if (blobStoreUrl != null && blobs == null) {
			try {
				File dataDir = getDataDir();
				if (dataDir == null) {
					dataDir = new File(".");
				}
				java.net.URI base = dataDir.toURI();
				String url = base.resolve(blobStoreUrl).toString();
				BlobStoreFactory bsf = BlobStoreFactory.newInstance();
				blobs = bsf.openBlobStore(url, blobStoreParameters);
			} catch (IOException e) {
				throw new ObjectStoreConfigException(e);
			} catch (IllegalArgumentException e) {
				throw new ObjectStoreConfigException(e);
			}
		}
		return blobs;
	}

	public synchronized void setBlobStore(BlobStore store) {
		this.blobs = store;
	}

	/**
	 * Creates a new ObjectConnection that will need to be closed by the caller.
	 */
	@Override
	public ObjectConnection getConnection() throws RepositoryException {
		BlobStore blobs;
		try {
			blobs = getBlobStore();
		} catch (ObjectStoreConfigException e) {
			throw new RepositoryException(e);
		}
		ObjectFactory factory = service.createObjectFactory();
		RepositoryConnection conn = getDelegate().getConnection();
		ObjectConnection con = new ObjectConnection(this, conn, factory,
				createTypeManager(), blobs);
		con.setIncludeInferred(isIncludeInferred());
		con.setMaxQueryTime(getMaxQueryTime());
		// con.setQueryResultLimit(getQueryResultLimit());
		con.setQueryLanguage(getQueryLanguage());
		con.setReadContexts(getReadContexts());
		con.setAddContexts(getAddContexts());
		con.setRemoveContexts(getRemoveContexts());
		con.setArchiveContexts(getArchiveContexts());
		return con;
	}

	protected TypeManager createTypeManager() {
		return new TypeManager(true);
	}

}
