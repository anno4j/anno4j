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
package org.openrdf.repository.object.composition.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.openrdf.repository.object.composition.BehaviourFactory;
import org.openrdf.repository.object.composition.BehaviourProvider;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.object.managers.PropertyMapper;

/**
 * Looks up BehaviourFactory for a set of classes.
 */
public class BehaviourProviderService {
	public static BehaviourProviderService newInstance(ClassFactory cl) {
		return new BehaviourProviderService(cl);
	}

	private final ServiceLoader<BehaviourProvider> loader;
	private final ClassFactory cl;

	public BehaviourProviderService(ClassFactory cl) {
		this.cl = cl;
		this.loader = ServiceLoader.load(BehaviourProvider.class, cl);
	}

	public Collection<BehaviourFactory> findImplementations(PropertyMapper mapper,
			Collection<Class<?>> classes, Set<Class<?>> bases)
			throws IOException {
		List<BehaviourFactory> implementations = new ArrayList<BehaviourFactory>();
		synchronized (loader) {
			for (BehaviourProvider bf : loader) {
				bf.setClassDefiner(cl);
				bf.setPropertyMapper(mapper);
				bf.setBaseClasses(bases);
				implementations.addAll(bf.getBehaviourFactories(classes));
			}
		}
		return implementations;
	}

}
