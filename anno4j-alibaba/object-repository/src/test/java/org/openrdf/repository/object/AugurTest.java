package org.openrdf.repository.object;

import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class AugurTest extends ObjectRepositoryTestCase {

	private static final String NS = "urn:test:";

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(AugurTest.class);
	}

	@Iri("urn:test:Bean")
	public static interface Bean extends RDFObject {
		@Iri("urn:test:name")
		String getName();

		void setName(String name);

		@Iri("urn:test:nick")
		Set<String> getNicks();

		void setNicks(Set<String> nicks);

		@Iri("urn:test:parent")
		Bean getParent();

		void setParent(Bean parent);

		@Iri("urn:test:friend")
		Set<Bean> getFriends();

		void setFriends(Set<Bean> friends);
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(Bean.class);
		super.setUp();
		con.setNamespace("test", NS);
		ValueFactory vf = con.getValueFactory();
		con.setAutoCommit(false);
		URI urn_root = vf.createURI(NS, "root");
		Bean root = con.addDesignation(con.getObjectFactory().createObject(urn_root), Bean.class);
		for (int i = 0; i < 100; i++) {
			URI uri = vf.createURI(NS, String.valueOf(i));
			Bean bean = con.addDesignation(con.getObjectFactory().createObject(uri), Bean.class);
			bean.setName("name" + i);
			bean.getNicks().add("nicka" + i);
			bean.getNicks().add("nickb" + i);
			bean.getNicks().add("nickc" + i);
			URI p = vf.createURI(NS, String.valueOf(i + 1000));
			Bean parent = con.addDesignation(con.getObjectFactory().createObject(p), Bean.class);
			parent.setName("name" + String.valueOf(i + 1000));
			bean.setParent(parent);
			for (int j = i - 10; j < i; j++) {
				if (j > 0) {
					URI f = vf.createURI(NS, String.valueOf(j + 1000));
					Bean friend = con.addDesignation(con.getObjectFactory().createObject(f), Bean.class);
					friend.setName("name" + String.valueOf(j + 1000));
					bean.getFriends().add(friend);
				}
			}
			root.getFriends().add(bean);
		}
		con.setAutoCommit(true);
	}

	public void test_concept() throws Exception {
		long start = System.currentTimeMillis();
		ObjectQuery query = con.prepareObjectQuery("SELECT ?o ?o_class ?o_name ?o_parent ?o_parent_class ?o_parent_name " +
				"WHERE {?o a ?type; a ?o_class; <urn:test:name> ?o_name; <urn:test:parent> ?o_parent ." +
				" ?o_parent a ?o_parent_class; <urn:test:name> ?o_parent_name }");
		query.setType("type", Bean.class);
		List<Bean> beans = query.evaluate(Bean.class).asList();
		for (Bean bean : beans) {
			bean.getName();
			if (bean.getParent() != null) {
				bean.getParent().getName();
			}
			for (Bean f : bean.getFriends()) {
				f.getName();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0);
	}

	public void test_object() throws Exception {
		long start = System.currentTimeMillis();
		List<Bean> beans = con.getObjects(Bean.class).asList();
		for (Bean bean : beans) {
			bean.getName();
			if (bean.getParent() != null) {
				bean.getParent().getName();
			}
			for (Bean f : bean.getFriends()) {
				f.getName();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0);
	}

	public void test_naive() throws Exception {
		ValueFactory vf = con.getValueFactory();
		final URI Bean = vf.createURI(NS, "Bean");
		final URI name = vf.createURI(NS, "name");
		final URI parent = vf.createURI(NS, "parent");
		final URI friend = vf.createURI(NS, "friend");
		long start = System.currentTimeMillis();
		RepositoryResult<Statement> beans = con.getStatements(null, RDF.TYPE, Bean);
		while (beans.hasNext()) {
			Statement st = beans.next();
			Resource bean = st.getSubject();
			QueryResults.asList(con.getStatements(bean, name, null));
			RepositoryResult<Statement> match;
			match = con.getStatements(bean, parent, null);
			while (match.hasNext()) {
				QueryResults.asList(con.getStatements((Resource)match.next().getObject(), name, null));
			}
			match = con.getStatements(bean, friend, null);
			while (match.hasNext()) {
				QueryResults.asList(con.getStatements((Resource)match.next().getObject(), name, null));
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000.0);
	}
}
