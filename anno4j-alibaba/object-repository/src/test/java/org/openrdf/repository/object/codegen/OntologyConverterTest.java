package org.openrdf.repository.object.codegen;

import java.io.File;
import java.net.URL;

import org.openrdf.repository.object.base.CodeGenTestCase;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;

/**
 * ObjectRepositoryConfig test case.
 * 
 * The default behaviour of <code>ObjectRepositoryConfig</code> is to import these
 * ontologies. This behaviour can be over-ridden by using the
 * {@link ObjectRepositoryConfig#setImportJarOntologies(boolean)} method. When set to
 * false, the ontologies are no longer automatically imported and therefore must
 * be explicitly imported using the {@link ObjectRepositoryConfig#addImports(URL)}
 * method (as demonstrated in {@link #testDc()} and
 * {@link #createFoafJar(String)}).
 * 
 */
public class OntologyConverterTest extends CodeGenTestCase {

	/**
	 * Dublin Core (DC) Elements jar creation test.
	 * 
	 * Automatic RDF source file import is disabled, and ontologies are manually
	 * added to converter.
	 * 
	 * @throws Exception
	 */
	public void testDc() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/dc-elements-schema.rdf");
		addRdfSource("/ontologies/dc-terms-schema.rdf");
		addRdfSource("/ontologies/dc-type-schema.rdf");
		File jar = createJar("dc.jar");
		assertTrue(jar.isFile());
		assertEquals(40, countClasses(jar, "dc", ".java"));
		assertEquals(40, countClasses(jar, "dc", ".class"));
	}

	/**
	 * Friend of a Friend (FOAF) ontology jar test.
	 * 
	 * Delegates to {@link #createFoafJar(String)} for the creation of the jar
	 * file.
	 * 
	 * @throws Exception
	 */
	public void testFoaf() throws Exception {
		addRdfSource("/ontologies/xsd-datatypes.rdf");
		addRdfSource("/ontologies/rdfs-schema.rdf");
		addRdfSource("/ontologies/owl-schema.rdf");
		addRdfSource("/ontologies/foaf-ontology.owl");
		File jar = createJar("foaf.jar");
		assertTrue(jar.isFile());
		assertEquals(12, countClasses(jar, "foaf", ".java"));
		assertEquals(12, countClasses(jar, "foaf", ".class"));
		assertTrue(jar.isFile());
	}

	/**
	 * Genealogical Data Communication (GEDCOM) jar creation test.
	 * 
	 * Automatic RDF source file import is left enabled, but gedcom-ontology.owl
	 * is manually added to converter.
	 * 
	 * @throws Exception
	 */
	public void testGedcom() throws Exception {
		addRdfSource("/ontologies/gedcom-ontology.owl");
		File jar = createJar("gedcom.jar");
		assertTrue(jar.isFile());
		assertEquals(9, countClasses(jar, "gedcom", ".java"));
		assertEquals(9, countClasses(jar, "gedcom", ".class"));
	}
}
