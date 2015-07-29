package org.openrdf.repository.object.codegen;

import java.io.File;

import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.base.CodeGenTestCase;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class DbpediaTest extends CodeGenTestCase {

	public void testCompile() throws Exception {
		addRdfSource("/ontologies/dbpedia_3.6.owl");
		File jar = createJar("dbpedia.jar");
		assertTrue(jar.isFile());
		assertEquals(272, countClasses(jar, "dbpedia_owl", ".java"));
		assertEquals(272, countClasses(jar, "dbpedia_owl", ".class"));
	}

	public void testCompose() throws Exception {
		addRdfSource("/ontologies/dbpedia_3.6.owl");
		ObjectRepositoryConfig converter = new ObjectRepositoryConfig();
		converter.addConceptJar(createJar("dbpedia.jar").toURI().toURL());
		ObjectRepositoryFactory ofm = new ObjectRepositoryFactory();
		ObjectRepository repo = ofm.getRepository(converter);
		repo.setDelegate(new SailRepository(new MemoryStore()));
		repo.setDataDir(targetDir);
		repo.initialize();
		ObjectConnection manager = repo.getConnection();
		manager.getObject("urn:test#test");
		manager.close();
		repo.shutDown();
	}
}
