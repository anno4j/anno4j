package org.openrdf.repository.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.base.RepositoryTestCase;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class MergeTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(MergeTest.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface complementOf {
		@Iri(OWL.NAMESPACE + "complementOf")
		Class<?> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface intersectionOf {
		@Iri(OWL.NAMESPACE+"intersectionOf")
		Class<?>[] value();
	}

	@Iri("urn:test:Company")
	public interface Company {}
	@Iri("urn:test:BigCompany")
	public interface BigCompany extends Company {}
	@complementOf(BigCompany.class)
	public interface NotBigCompany {}
	@intersectionOf({Company.class, NotBigCompany.class})
	public interface SmallCompany extends Company, NotBigCompany {}

	public class SmallCompanyImpl implements SmallCompany {
		private URI name;

		public SmallCompanyImpl(URI name) {
			this.name = name;
		}

		public URI getURI() {
			return name;
		}
	}

	@Iri("urn:test:Node")
	public interface Node {
		@Iri("urn:test:sibling")
		Node getSibling();
		@Iri("urn:test:sibling")
		void setSibling(Node sibling);
	}

	public void testComplexMerge() throws Exception {
		URI name = ValueFactoryImpl.getInstance().createURI("urn:test:", "comp");
		con.addDesignation(con.getObject(name), BigCompany.class);
		con.addObject(name, new SmallCompanyImpl(name));
		Company company = (Company) con.getObject(name);
		assertTrue(company instanceof BigCompany);
	}

	public void testMergeOtherRepository() throws Exception {
		SailRepository repo = new SailRepository(new MemoryStore());
		repo.initialize();
		ObjectRepositoryFactory orf = new ObjectRepositoryFactory();
		ObjectRepository or = orf.createRepository(config, repo);
		ObjectConnection oc = or.getConnection();
		Node n1 = oc.addDesignation(oc.getObject("urn:test:n1"), Node.class);
		Node n2 = oc.addDesignation(oc.getObject("urn:test:n2"), Node.class);
		n1.setSibling(n2);
		n2.setSibling(n1);
		Node m1 = (Node) con.getObject(con.addObject(n1));
		assertNotNull(m1.getSibling());
	}

	public void testMergeBlankNodeFromOtherRepository() throws Exception {
		SailRepository repo = new SailRepository(new MemoryStore());
		repo.initialize();
		ObjectRepositoryFactory orf = new ObjectRepositoryFactory();
		ObjectRepository or = orf.createRepository(config, repo);
		ObjectConnection oc = or.getConnection();
		ValueFactory vf = oc.getValueFactory();
		Node n1 = oc.addDesignation(oc.getObject(vf.createBNode()), Node.class);
		Node n2 = oc.addDesignation(oc.getObject(vf.createBNode()), Node.class);
		n1.setSibling(n2);
		n2.setSibling(n1);
		Node m1 = (Node) con.getObject(con.addObject(n1));
		assertNotNull(m1.getSibling());
	}

	public void setUp() throws Exception {
		config.addAnnotation(complementOf.class);
		config.addAnnotation(intersectionOf.class);
		config.addConcept(Company.class);
		config.addConcept(BigCompany.class);
		config.addConcept(Node.class);
		super.setUp();
	}

}
