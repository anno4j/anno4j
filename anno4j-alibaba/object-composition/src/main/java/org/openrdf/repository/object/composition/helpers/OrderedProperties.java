/*
 * Copyright (c) 2011 Talis Inc., Some rights reserved.
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
package org.openrdf.repository.object.composition.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * Reads a properties file and preserves the order of the keys.
 *
 * @author James Leigh
 **/
public class OrderedProperties extends Properties {
	private static final long serialVersionUID = -5067493864922247542L;
	private Vector<Object> propertyNames = new Vector<Object>();
	private boolean loaded;

	public OrderedProperties(InputStream inStream) throws IOException {
		super.load(inStream);
		propertyNames.retainAll(super.keySet());
		loaded = true;
	}

	public Enumeration<?> propertyNames() {
		return keys();
	}

	@Override
	public synchronized Enumeration<Object> keys() {
		return propertyNames.elements();
	}

	@Override
	public Set<Object> keySet() {
		return new LinkedHashSet<Object>(propertyNames);
	}

	public synchronized Object put(Object key, Object value) {
		if (loaded)
			throw new UnsupportedOperationException();
		propertyNames.add(key);
		return super.put(key, value);
	}

	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void load(Reader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void loadFromXML(InputStream in) throws IOException,
			InvalidPropertiesFormatException {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object setProperty(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Object remove(Object key) {
		throw new UnsupportedOperationException();
	}
}
