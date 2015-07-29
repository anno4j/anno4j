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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.bytecode.ClassFile;

/**
 * Filter for detecting behaviour class files.
 * 
 * @author James Leigh
 *
 */
public class CheckForBehaviour extends CheckForConcept {

	public CheckForBehaviour(ClassLoader cl) {
		super(cl);
		assert cl != null;
	}

	public String getName() {
		return "behaviours";
	}

	public String getClassName(String name, InputStream stream) throws IOException {
		// NOTE package-info.class should be excluded
		if (!name.endsWith(".class") || name.contains("-"))
			return null;
		DataInputStream dstream = new DataInputStream(stream);
		try {
			ClassFile cf = new ClassFile(dstream);
			if (!cf.isInterface() && !isAnnotationPresent(cf)) {
				// behaviour that implements a concept
				for (String fname : cf.getInterfaces()) {
					String cn = fname.replace('.', '/') + ".class";
					InputStream in = cl.getResource(cn).openStream();
					try {
						if (super.getClassName(cn, in) != null)
							return cf.getName();
					} finally {
						in.close();
					}
				}
			}
		} finally {
			dstream.close();
			stream.close();
		}
		return null;
	}

}
