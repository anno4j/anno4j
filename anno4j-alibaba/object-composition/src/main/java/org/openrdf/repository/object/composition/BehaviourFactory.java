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
package org.openrdf.repository.object.composition;

import java.lang.reflect.Method;

/**
 * Behaviour mixin constructor.
 * 
 * @author James Leigh
 */
public interface BehaviourFactory {

	/**
	 * An short name to reasonably distinguish it from similar behaviours.
	 * 
	 * @return short name
	 */
	String getName();

	/**
	 * Type of behaviour that the {@link #newInstance(Object)} will implement.
	 * 
	 * @return the type of behaviour
	 */
	Class<?> getBehaviourType();

	/**
	 * Traits that these behaviours provides.
	 * 
	 * @return array of java interfaces
	 */
	Class<?>[] getInterfaces();

	/**
	 * Public methods these behaviours provide. This includes methods from
	 * {@link #getInterfaces()} that these behaviours provides.
	 * 
	 * @return array of public methods
	 */
	Method[] getMethods();

	/**
	 * The method implemented by {@link #getBehaviourType()} that is to be
	 * invoked when the given method is called. If these behaviours do not
	 * provide an implementation for the given method, return null.
	 * 
	 * @param method
	 * @return method implemented by the return value of
	 *         {@link #getBehaviourType()} or null
	 */
	Method getInvocation(Method method);

	/**
	 * If these behaviours should always be invoked before behaviours of the
	 * given factory.
	 * 
	 * @param invocation
	 *            the method returned from {@link #getInvocation(Method)}
	 * @param factory
	 *            an alternative set of behaviours
	 * @param to
	 *            the method returned from the given factory
	 * @return false if no preference
	 */
	boolean precedes(Method invocation, BehaviourFactory factory, Method to);

	/**
	 * If this factory always returns a single instance.
	 * @return <code>true</code> if {@link #getSingleton()} should be called
	 */
	boolean isSingleton();

	/**
	 * The single behaviour that this factory produces.
	 * 
	 * @return singleton instance of {@link #isSingleton()} returns <code>true</code>
	 */
	Object getSingleton();

	/**
	 * New behaviour implementation for the given proxy object.
	 * 
	 * @param proxy
	 * @throws Throwable
	 */
	Object newInstance(Object proxy) throws Throwable;

}
