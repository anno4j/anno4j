package org.openrdf.repository.object;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class DurationTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(DurationTest.class);
	}

	@Iri("urn:test:Entity")
	public interface Entity extends RDFObject {
		@Iri("urn:test:duration")
		Duration getDuration();
		void setDuration(Duration duration);
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(Entity.class);
		super.setUp();
	}

	public void testDayTime() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Duration duration = DatatypeFactory.newInstance().newDuration("P0DT0H0M1S");
		entity.setDuration(duration);
		entity = (Entity) con.getObject(entity.getResource());
		assertEquals(duration, entity.getDuration());
	}

	public void testYearMonth() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Duration duration = DatatypeFactory.newInstance().newDuration("P0Y1M");
		entity.setDuration(duration);
		entity = (Entity) con.getObject(entity.getResource());
		assertEquals(duration, entity.getDuration());
	}

	public void testFullDuration() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Duration duration = DatatypeFactory.newInstance().newDuration("P0Y0M0DT0H0M1S");
		entity.setDuration(duration);
		entity = (Entity) con.getObject(entity.getResource());
		assertEquals(duration, entity.getDuration());
	}

}
