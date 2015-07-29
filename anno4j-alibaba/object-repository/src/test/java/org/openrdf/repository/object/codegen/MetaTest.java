package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class MetaTest extends CodeGenTestCase {

	public void testSpecies() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/species-ontology.owl");
		File jar = createJar("species.jar");
		assertTrue(jar.isFile());
		assertEquals(2, countClasses(jar, "spc", ".java"));
		assertEquals(2, countClasses(jar, "spc", ".class"));
	}
}
