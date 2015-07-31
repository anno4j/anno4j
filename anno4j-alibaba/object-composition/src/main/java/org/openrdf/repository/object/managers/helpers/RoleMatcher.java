/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.managers.helpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Matches concepts with @matches annotation to URIs as their are loaded from
 * the repository.
 */
public class RoleMatcher implements Cloneable {
	private Comparator<String> reverse = new Comparator<String>() {
		public int compare(String o1, String o2) {
			int i = o1.length();
			int j = o2.length();
            while (i > 0 && j > 0) {
                char c1 = o1.charAt(--i);
                char c2 = o2.charAt(--j);
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
			return i - j;
		}
	};
	private ConcurrentNavigableMap<String, ConcurrentNavigableMap<String, Collection<Class<?>>>> hostsufPathpre = new ConcurrentSkipListMap(reverse);
	private ConcurrentNavigableMap<String, ConcurrentMap<String, Collection<Class<?>>>> hostsufPath = new ConcurrentSkipListMap(reverse);
	private ConcurrentNavigableMap<String, Collection<Class<?>>> uriprefix = new ConcurrentSkipListMap();
	private ConcurrentMap<String, Collection<Class<?>>> uris = new ConcurrentHashMap();
	private boolean empty = true;

	public RoleMatcher clone() {
		RoleMatcher cloned = new RoleMatcher();
		for (String host : hostsufPathpre.keySet()) {
			for (String path : hostsufPathpre.get(host).keySet()) {
				for (Class<?> role : hostsufPathpre.get(host).get(path)) {
					cloned.addRoles('*' + host + path + '*', role);
				}
			}
		}
		for (String host : hostsufPath.keySet()) {
			for (String path : hostsufPath.get(host).keySet()) {
				for (Class<?> role : hostsufPath.get(host).get(path)) {
					cloned.addRoles('*' + host + path, role);
				}
			}
		}
		for (String key : uriprefix.keySet()) {
			for (Class<?> role : uriprefix.get(key)) {
				cloned.addRoles(key + '*', role);
			}
		}
		for (String key : uris.keySet()) {
			for (Class<?> role : uris.get(key)) {
				cloned.addRoles(key, role);
			}
		}
		return cloned;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void addRoles(String pattern, Class<?> role) {
		if (pattern.endsWith("*")) {
			String prefix = pattern.substring(0, pattern.length() - 1);
			if (prefix.startsWith("/")) {
				addPathPrefix(hostsufPathpre, "", prefix, role);
			} else if (prefix.startsWith("*") && prefix.contains("/")) {
				int idx = prefix.indexOf('/');
				String suffix = prefix.substring(1, idx);
				prefix = prefix.substring(idx);
				addPathPrefix(hostsufPathpre, suffix, prefix, role);
			} else if (prefix.startsWith("*")) {
				String suffix = prefix.substring(1);
				addPathPrefix(hostsufPathpre, suffix, "", role);
			} else {
				add(uriprefix, prefix, role);
			}
		} else {
			if (pattern.startsWith("/")) {
				addPath(hostsufPath, "", pattern, role);
			} else if (pattern.startsWith("*") && pattern.contains("/")) {
				int idx = pattern.indexOf('/');
				String suffix = pattern.substring(1, idx);
				pattern = pattern.substring(idx);
				addPath(hostsufPath, suffix, pattern, role);
			} else if (pattern.startsWith("*")) {
				String suffix = pattern.substring(1);
				addPath(hostsufPath, suffix, "", role);
			} else {
				add(uris, pattern, role);
			}
		}
		empty = false;
	}

	public void findRoles(String uri, Collection<Class<?>> roles) {
		findExactRoles(uris, uri, roles);
		findRoles(uriprefix, uri, roles);
		int idx = uri.indexOf("://") + 3;
		if (idx > 3 && idx < uri.length()) {
			int sidx = uri.indexOf('/', idx);
			if (sidx > 0) {
				String auth = uri.substring(idx, sidx);
				String path = uri.substring(sidx);
				findPathPrefixRoles(auth, path, roles);
				findPathRoles(auth, path, roles);
			} else {
				String auth = uri.substring(idx);
				findPathPrefixRoles(auth, "", roles);
				findPathRoles(auth, "", roles);
			}
		}
	}

	private void addPathPrefix(
			ConcurrentNavigableMap<String, ConcurrentNavigableMap<String, Collection<Class<?>>>> map,
			String suffix, String prefix, Class<?> role) {
		ConcurrentNavigableMap<String, Collection<Class<?>>> m, o;
		m = map.get(suffix);
		if (m == null) {
			m = new ConcurrentSkipListMap<String, Collection<Class<?>>>();
			o = map.putIfAbsent(suffix, m);
			if (o != null) {
				m = o;
			}
		}
		add(m, prefix, role);
	}

	private void addPath(
			ConcurrentNavigableMap<String, ConcurrentMap<String, Collection<Class<?>>>> map,
			String suffix, String prefix, Class<?> role) {
		ConcurrentMap<String, Collection<Class<?>>> m, o;
		m = map.get(suffix);
		if (m == null) {
			m = new ConcurrentHashMap<String, Collection<Class<?>>>();
			o = map.putIfAbsent(suffix, m);
			if (o != null) {
				m = o;
			}
		}
		add(m, prefix, role);
	}

	private void add(ConcurrentMap<String, Collection<Class<?>>> map,
			String pattern, Class<?> role) {
		Collection<Class<?>> list = map.get(pattern);
		if (list == null) {
			list = new CopyOnWriteArrayList<Class<?>>();
			Collection<Class<?>> o = map.putIfAbsent(pattern, list);
			if (o != null) {
				list = o;
			}
		}
		if (!list.contains(role)) {
			list.add(role);
		}
	}

	private void findPathRoles(String auth, String path, Collection<Class<?>> roles) {
		Map<String, Collection<Class<?>>> map = hostsufPath.get(auth);
		if (map != null) {
			findExactRoles(map, path, roles);
		}
		String key = hostsufPath.lowerKey(auth);
		if (key == null) {
			return;
		} else if (auth.endsWith(key)) {
			findPathRoles(key, path, roles);
		} else if (auth.length() > 0) {
			int i = auth.length() - 1;
			int j = key.length() - 1;
			while (i >= 0 && j >=0 && auth.charAt(i) == key.charAt(j)) {
				i--;
				j--;
			}
			String suffix = auth.substring(i + 1);
			findPathRoles(suffix, path, roles);
		}
	}

	private void findPathPrefixRoles(String auth, String path, Collection<Class<?>> roles) {
		NavigableMap<String, Collection<Class<?>>> map = hostsufPathpre.get(auth);
		if (map != null) {
			findRoles(map, path, roles);
		}
		String key = hostsufPathpre.lowerKey(auth);
		if (key == null) {
			return;
		} else if (auth.endsWith(key)) {
			findPathPrefixRoles(key, path, roles);
		} else if (auth.length() > 0) {
			int i = auth.length() - 1;
			int j = key.length() - 1;
			while (i >= 0 && j >= 0 && auth.charAt(i) == key.charAt(j)) {
				i--;
				j--;
			}
			String suffix = auth.substring(i + 1);
			findPathPrefixRoles(suffix, path, roles);
		}
	}

	private void findRoles(NavigableMap<String, Collection<Class<?>>> map,
			String full, Collection<Class<?>> roles) {
		findExactRoles(map, full, roles);
		String key = map.lowerKey(full);
		if (key == null) {
			return;
		} else if (full.startsWith(key)) {
			findRoles(map, key, roles);
		} else if (full.length() > 0) {
			int idx = 0;
			while (idx < full.length() && idx < key.length()
					&& full.charAt(idx) == key.charAt(idx)) {
				idx++;
			}
			String prefix = full.substring(0, idx);
			findRoles(map, prefix, roles);
		}
	}

	private void findExactRoles(Map<String, Collection<Class<?>>> map,
			String uri, Collection<Class<?>> roles) {
		Collection<Class<?>> list = map.get(uri);
		if (list != null) {
			roles.addAll(list);
		}
	}
}
