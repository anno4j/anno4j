package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class ICalTest extends CodeGenTestCase {

	public void testLiterals() throws Exception {
		addRdfSource("/ontologies/ical-ontology.owl");
		File jar = createJar("ical.jar");
		assertTrue(jar.isFile());
		assertEquals(28, countClasses(jar, "cal", ".java"));
		assertEquals(28, countClasses(jar, "cal", ".class"));
	}
}
