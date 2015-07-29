package org.openrdf.repository.object.codegen;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class Base64BinaryTest extends CodeGenTestCase {

	public void testOneOf() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/binary-ontology.owl");
		createJar("binary.jar");
	}

}
