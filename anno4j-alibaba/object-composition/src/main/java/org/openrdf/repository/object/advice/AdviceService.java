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
package org.openrdf.repository.object.advice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up the AdviceFactory for a method annotation.
 */
public class AdviceService {
	private static final ServiceLoader<AdviceProvider> installed = ServiceLoader
			.load(AdviceProvider.class, AdviceService.class.getClassLoader());

	public static AdviceService newInstance() {
		return newInstance(Thread.currentThread().getContextClassLoader());
	}

	public static AdviceService newInstance(ClassLoader cl) {
		return new AdviceService(cl == null ? AdviceService.class.getClassLoader() : cl);
	}

	private final Logger logger = LoggerFactory.getLogger(AdviceService.class);
	private final ServiceLoader<AdviceProvider> loader;
	private final Map<Class<?>, AdviceFactory> factories = new HashMap<Class<?>, AdviceFactory>();

	public AdviceService(ClassLoader cl) {
		this.loader = ServiceLoader.load(AdviceProvider.class, cl);
	}

	public synchronized AdviceFactory getAdviserFactory(Class<?> annotationType) {
		if (factories.containsKey(annotationType)) {
			return factories.get(annotationType);
		} else {
			AdviceFactory factory;
			factory = getAdviceFactory(annotationType, loader);
			if (factory != null) {
				factories.put(annotationType, factory);
				return factory;
			}
			synchronized (installed) {
				factory = getAdviceFactory(annotationType, installed);
			}
			if (factory != null) {
				factories.put(annotationType, factory);
				return factory;
			}
			factories.put(annotationType, null);
			return null;
		}
	}

	private AdviceFactory getAdviceFactory(Class<?> type,
			ServiceLoader<AdviceProvider> loader) {
		Iterator<AdviceProvider> iter = loader.iterator();
		while (iter.hasNext()) {
			try {
				AdviceFactory f = iter.next().getAdviserFactory(type);
				if (f != null) {
					factories.put(type, f);
					return f;
				}
			} catch (ServiceConfigurationError e) {
				logger.warn(e.getMessage());
			}
		}
		return null;
	}
}
