package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.base.CodeGenTestCase;


public class PropertyrestrictionTest extends CodeGenTestCase {

	public void testPropertyRestriction() throws Exception {
		addRdfSource("/ontologies/restriction-ontology.owl");
		File jar = createJar("restriction.jar");
		assertTrue(jar.isFile());
		assertEquals(7, countClasses(jar, "test", ".java"));
		assertEquals(7, countClasses(jar, "test", ".class"));
	}
}
