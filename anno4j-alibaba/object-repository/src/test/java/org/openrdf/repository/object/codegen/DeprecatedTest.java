package org.openrdf.repository.object.codegen;

import org.openrdf.repository.object.base.CodeGenTestCase;


public class DeprecatedTest extends CodeGenTestCase {

	public void testRoundTrip() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/roundtrip-ontology.owl");
		createJar("deprecated.jar");
	}
}
