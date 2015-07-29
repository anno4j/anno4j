package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.base.CodeGenTestCase;


public class OneOfTest extends CodeGenTestCase {

	public void testOneOf() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/oneof-ontology.owl");
		File jar = createJar("oneOf.jar");
		assertTrue(jar.isFile());
		assertEquals(2, countClasses(jar, "one", ".java"));
		assertEquals(2, countClasses(jar, "one", ".class"));
		assertEquals(4, countClasses(jar, "ns", ".java"));
		assertEquals(4, countClasses(jar, "ns", ".class"));
	}
}
