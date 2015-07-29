package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class LiteralTest extends CodeGenTestCase {

	public void testLiterals() throws Exception {
		addRdfSource("/ontologies/literals-ontology.owl");
		File jar = createJar("literals.jar");
		assertTrue(jar.isFile());
		assertEquals(1, countClasses(jar, "trip", ".java"));
		assertEquals(1, countClasses(jar, "trip", ".class"));
	}
}
