/*
 * Copyright (c) 2014, 3 Round Stones Inc. Some rights reserved.
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This Map will remove entries when the value in the map has been cleaned from
 * garbage collection.
 * 
 * @author James Leigh
 * 
 * @param <K>
 * @param <V>
 */
public class WeakValueMap<K, V> extends AbstractMap<K, V> {
	final Map<K, WeakEntry> hash;
	final ReferenceQueue<? super V> queue = new ReferenceQueue<V>();
	private Set<java.util.Map.Entry<K, V>> entrySet;
	private Collection<V> values;

	private class WeakEntry extends WeakReference<V> {
		private K key;

		public WeakEntry(K key, V value) {
			super(value, (ReferenceQueue<? super V>) queue);
			this.key = key;
	        if (key == null || value == null) {
	            throw new NullPointerException();
	        }
		}

		public K getKey() {
			return key;
		}

	}

	/**
	 * Constructs an empty <tt>SoftObjectMap</tt> with the specified initial
	 * capacity and load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             nonpositive
	 */
	public WeakValueMap(int initialCapacity, float loadFactor) {
		hash = new HashMap<K, WeakEntry>(initialCapacity, loadFactor);
	}

	/**
	 * Constructs an empty <tt>SoftObjectMap</tt> with the specified initial
	 * capacity and the default load factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public WeakValueMap(int initialCapacity) {
		hash = new HashMap<K, WeakEntry>(initialCapacity);
	}

	/**
	 * Constructs an empty <tt>SoftObjectMap</tt> with the default initial
	 * capacity (16) and the default load factor (0.75).
	 */
	public WeakValueMap() {
		hash = new HashMap<K, WeakEntry>();
	}

	/**
	 * Constructs a new <tt>SoftObjectMap</tt> with the same mappings as the
	 * specified <tt>Map</tt>. The <tt>SoftObjectMap</tt> is created with
	 * default load factor (0.75) and an initial capacity sufficient to hold the
	 * mappings in the specified <tt>Map</tt>.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	public WeakValueMap(Map<? extends K, ? extends V> m) {
		hash = new HashMap<K, WeakEntry>(m.size());
		putAll(m);
	}

	public int size() {
		compact();
		return hash.size();
	}

	public boolean isEmpty() {
		compact();
		return hash.isEmpty();
	}

	public boolean containsKey(Object key) {
		compact();
		WeakEntry ref = hash.get(key);
		return ref != null && ref.get() != null;
	}

	public boolean containsValue(Object value) {
		compact();
		return values().contains(value);
	}

	public V get(Object key) {
		return refGet(hash.get(key));
	}

	public V put(K key, V value) {
		compact();
		return refGet(hash.put(key, new WeakEntry(key, value)));
	}

	public V remove(Object key) {
		compact();
		return refGet(hash.remove(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		compact();
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
			hash.put(e.getKey(), new WeakEntry(e.getKey(), e.getValue()));
		}
	}

	public void clear() {
		hash.clear();
	}

	public Set<K> keySet() {
		compact();
		return hash.keySet();
	}

	public synchronized Collection<V> values() {
		compact();
		if (values != null)
			return values;
		return values = new AbstractCollection<V>() {
			public Iterator<V> iterator() {
				return new Iterator<V>() {
					private Iterator<WeakEntry> i = hash.values().iterator();

					public boolean hasNext() {
						return i.hasNext();
					}

					public V next() {
						return refGet(i.next());
					}

					public void remove() {
						i.remove();
					}
				};
			}

			public int size() {
				return hash.size();
			}

			public boolean isEmpty() {
				return hash.isEmpty();
			}

			public void clear() {
				hash.clear();
			}
		};
	}

	public synchronized Set<java.util.Map.Entry<K, V>> entrySet() {
		compact();
		if (entrySet != null)
			return entrySet;
		return entrySet = new AbstractSet<Map.Entry<K, V>>() {
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				return new Iterator<java.util.Map.Entry<K, V>>() {
					Iterator<Entry<K, WeakEntry>> i = hash.entrySet()
							.iterator();

					public boolean hasNext() {
						return i.hasNext();
					}

					public java.util.Map.Entry<K, V> next() {
						return new Entry<K, V>() {
							private Entry<K, WeakEntry> entry = i.next();

							public K getKey() {
								return entry.getKey();
							}

							public V getValue() {
								return refGet(entry.getValue());
							}

							public V setValue(V value) {
								return refGet(entry.setValue(new WeakEntry(
										entry.getKey(), value)));
							}
						};
					}

					public void remove() {
						i.remove();
					}
				};
			}

			public int size() {
				return hash.size();
			}

			public boolean isEmpty() {
				return hash.isEmpty();
			}

			public void clear() {
				hash.clear();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private synchronized void compact() {
		WeakEntry ref;
		while ((ref = (WeakEntry) queue.poll()) != null) {
			if (ref == hash.get(ref.getKey())) {
				hash.remove(ref.getKey());
			}
		}
	}

	V refGet(WeakEntry ref) {
		if (ref == null)
			return null;
		return ref.get();
	}

}
