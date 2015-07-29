package org.openrdf.repository.object.codegen;

import java.io.File;

import junit.framework.TestCase;

import org.openrdf.repository.object.compiler.Compiler;

public class CompilerTest extends TestCase {

	public void testHelp() throws Exception {
		Compiler.main(new String[] { "--help" });
	}

	public void testOntology() throws Exception {
		String rdfs = getResource("/ontologies/rdfs-schema.rdf");
		String owl = getResource("/ontologies/owl-schema.rdf");
		String foaf = getResource("/ontologies/foaf-ontology.owl");
		File jar = File.createTempFile("ont", "jar");
		jar.delete();
		try {
			Compiler.main(new String[] { "-j", jar.getAbsolutePath(), "-f", "false", rdfs, owl, foaf });
		} finally {
			jar.delete();
		}
	}

	private String getResource(String name) {
		return CompilerTest.class.getResource(name).toExternalForm();
	}
}
