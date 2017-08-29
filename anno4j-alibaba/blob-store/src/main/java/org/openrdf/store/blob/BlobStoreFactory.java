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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates new {@link BlobStore}s, storing blobs to the given directories.
 * 
 * @author James Leigh
 * 
 */
public class BlobStoreFactory {
	private static final String URL_KEY = "_url";
	private static BlobStoreFactory instance = new BlobStoreFactory();

	public static BlobStoreFactory newInstance() {
		return instance;
	}

	private final Logger logger = LoggerFactory
			.getLogger(BlobStoreFactory.class);
	private final Map<Map<String, String>, WeakReference<BlobStore>> stores = new HashMap<Map<String, String>, WeakReference<BlobStore>>();

	private BlobStoreFactory() {
		super();
	}

	/**
	 * Create or retrieve a BlobStore at this location.
	 * 
	 * @throws IllegalArgumentException
	 *             if no blob store provider for this <code>url</code> could be
	 *             found.
	 */
	public BlobStore openBlobStore(String url, Map<String, String> parameters)
			throws IOException {
		Map<String, String> key = new HashMap<String, String>();
		if (parameters != null) {
			key.putAll(parameters);
		}
		key.put(URL_KEY, url);
		synchronized (stores) {
			Iterator<WeakReference<BlobStore>> iter = stores.values().iterator();
			while (iter.hasNext()) {
				if (iter.next().get() == null) {
					iter.remove();
				}
			}
			WeakReference<BlobStore> ref = stores.get(key);
			if (ref != null) {
				BlobStore store = ref.get();
				if (store != null)
					return store;
			}
		}
		BlobStore store = getBlobStoreProvider(url, parameters);
		if (store == null)
			throw new IllegalArgumentException(
					"No blob store provider is available for: " + url);
		synchronized (stores) {
			stores.put(key, new WeakReference<BlobStore>(store));
		}
		return store;
	}

	/**
	 * Create or retrieve a BlobStore at this location.
	 */
	public BlobStore openBlobStore(String url) throws IOException {
		return openBlobStore(url, null);
	}

	/**
	 * Create or retrieve a BlobStore at this directory.
	 */
	public BlobStore openBlobStore(File dir) throws IOException {
		return openBlobStore(dir.toURI().toString());
	}

	private BlobStore getBlobStoreProvider(String url,
			Map<String, String> parameters) {
		ClassLoader ccl = getClass().getClassLoader();
		BlobStore store = getBlobStoreProvider(url, parameters, ccl);
		if (store != null)
			return store;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return getBlobStoreProvider(url, parameters, cl);
	}

	private BlobStore getBlobStoreProvider(String url, Map<String, String> parameters,
			ClassLoader cl) {
		ServiceLoader<BlobStoreProvider> load = ServiceLoader.load(
				BlobStoreProvider.class, cl);
		Iterator<BlobStoreProvider> providers = load.iterator();
		while (providers.hasNext()) {
			try {
				BlobStoreProvider provider = providers.next();
				BlobStore store = provider.createBlobStore(url, parameters);
				if (store != null)
					return store;
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		return null;
	}
}
