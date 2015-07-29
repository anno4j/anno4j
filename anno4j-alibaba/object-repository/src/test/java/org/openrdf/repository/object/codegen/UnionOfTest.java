package org.openrdf.repository.object.codegen;

import org.openrdf.repository.object.base.CodeGenTestCase;


public class UnionOfTest extends CodeGenTestCase {

	public void testUnionOf() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/object-ontology.owl");
		addRdfSource("/ontologies/unionof-ontology.owl");
		createJar("unionOf.jar");
	}
}
