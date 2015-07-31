/*
 * Copyright (c) 2012 3 Round Stones Inc., Some rights reserved.
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
package org.openrdf.repository.object.compiler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up a default prefix for a namespace using the prefix.cc service.
 */
public class NamespacePrefixService {
	private static NamespacePrefixService instance = new NamespacePrefixService();
	private static final String PREFIX_LOOKUP = "http://prefix.cc/reverse?format=ttl&uri=";

	public static synchronized NamespacePrefixService getInstance() {
		return instance;
	}

	public static synchronized void setInstance(NamespacePrefixService service) {
		instance = service;
	}

	private final Logger logger = LoggerFactory
			.getLogger(NamespacePrefixService.class);
	private final Map<String, String> prefixes = new HashMap<String, String>();

	private NamespacePrefixService() {
		super();
	}

	public synchronized String prefix(final String ns) {
		try {
			if (prefixes.containsKey(ns))
				return prefixes.get(ns);
			URL url = new URL(PREFIX_LOOKUP + URLEncoder.encode(ns, "UTF-8"));
			logger.info("Requesting {}", url);
			URLConnection con = url.openConnection();
			con.addRequestProperty("Accept", "text/turtle");
			InputStream in = con.getInputStream();
			try {
				RDFParser parser = RDFParserRegistry.getInstance().get(RDFFormat.TURTLE).getParser();
				final List<String> match = new ArrayList<String>();
				parser.setRDFHandler(new RDFHandlerBase() {
					public void handleNamespace(String prefix, String uri)
							throws RDFHandlerException {
						if (uri.equals(ns)) {
							match.add(prefix);
						}
					}
				});
				parser.parse(in, PREFIX_LOOKUP + URLEncoder.encode(ns, "UTF-8"));
				if (match.size() > 0) {
					String prefix = match.get(0);
					prefixes.put(ns, prefix);
					return prefix;
				}
				prefixes.put(ns, null);
			} finally {
				in.close();
			}
		} catch (FileNotFoundException e) {
			prefixes.put(ns, null);
			logger.trace("Unknown namespace {}", ns);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return null;
	}
}
