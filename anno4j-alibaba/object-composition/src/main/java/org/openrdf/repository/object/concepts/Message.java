/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.concepts;

import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.repository.object.vocabulary.MSG;

/**
 * Invocation context for behaviour methods. Can be used in conjunction with
 * 
 * {@link ParameterTypes} to intersect method invocations.
 * 
 * @author James Leigh
 * 
 */
@Iri(MSG.NAMESPACE + "Message")
public interface Message {

	/** Single return value of this message. */
	@Iri(MSG.NAMESPACE + "literal")
	Object getFunctionalLiteralResponse();

	/** Single return value of this message. */
	@Iri(MSG.NAMESPACE + "literal")
	void setFunctionalLiteralResponse(Object functionalLiteralResponse);

	/** Single return value of this message. */
	@Iri(MSG.NAMESPACE + "object")
	Object getFunctionalObjectResponse();

	/** Single return value of this message. */
	@Iri(MSG.NAMESPACE + "object")
	void setFunctionalObjectResponse(Object functionalObjectResponse);

	/** The return value of this message. */
	@Iri(MSG.NAMESPACE + "literalSet")
	Set<Object> getLiteralResponse();

	/** The return value of this message. */
	@Iri(MSG.NAMESPACE + "literalSet")
	void setLiteralResponse(Set<?> literalResponse);

	/** The return value of this message. */
	@Iri(MSG.NAMESPACE + "objectSet")
	Set<Object> getObjectResponse();

	/** The return value of this message. */
	@Iri(MSG.NAMESPACE + "objectSet")
	void setObjectResponse(Set<?> objectResponse);

	/** The receiver of this message. */
	@Iri(MSG.NAMESPACE + "target")
	Object getMsgTarget();

	/** The receiver of this message. */
	@Iri(MSG.NAMESPACE + "target")
	void setMsgTarget(Object msgTarget);
}
