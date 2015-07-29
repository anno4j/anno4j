package org.openrdf.repository.object.codegen;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class NoPrefixTest extends CodeGenTestCase {

	public void testEmptyPrefix() throws Exception {
		addRdfSource("/ontologies/noprefix1-ontology.ttl");
		addRdfSource("/ontologies/noprefix2-ontology.ttl");
		createJar("noprefix.jar");
	}
}
