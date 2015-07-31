package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class MultipleInstanceTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(MultipleInstanceTest.class);
	}

	@Iri("u:geonode")
	public interface TestNode extends RDFObject {
		@Iri("u:lat")
		Double getLatitude();

		@Iri("u:lat")
		void setLatitude(Double latitude);

		@Iri("u:lon")
		Double getLongitude();

		@Iri("u:lon")
		void setLongitude(Double longitude);
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(TestNode.class);
		super.setUp();
	}

	public void testThenGet1() throws Exception {
		URI uri = con.getValueFactory().createURI("u:1");
		TestNode node1 = con.addDesignation(con.getObjectFactory()
				.createObject(uri), TestNode.class);
		node1.setLatitude(14.0);
		node1.setLongitude(15.0);
		TestNode node2 = con.getObject(TestNode.class, uri);
		node2.setLatitude(12.0);
		node2.setLongitude(13.0);
		assertEquals(node1, node2);
		assertEquals(12.0, node2.getLatitude(), 0.0);
	}

	public void testThenGet2() throws Exception {
		URI uri = con.getValueFactory().createURI("u:1");
		TestNode node1 = con.addDesignation(con.getObjectFactory()
				.createObject(uri), TestNode.class);
		node1.setLatitude(14.0);
		TestNode node2 = con.getObject(TestNode.class, uri);
		node2.setLatitude(12.0);
		assertEquals(node1, node2);
		assertEquals(12.0, node2.getLatitude(), 0.0);
	}
}
