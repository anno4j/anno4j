package org.openrdf.repository.object;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.object.traits.VoidMessage;

public class InterceptTest extends ObjectRepositoryTestCase {
	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(InterceptTest.class);
	}

	@Iri("urn:test:IConcept")
	public interface IConcept extends RDFObject {
		@Iri("urn:test:date")
		public XMLGregorianCalendar getDate();
		public void setDate(XMLGregorianCalendar date);
		@Iri("urn:test:time")
		public XMLGregorianCalendar getTime();
		public void setTime(XMLGregorianCalendar time);
	}

	public abstract static class CatchBehaviour implements IConcept {
		@ParameterTypes({})
		public XMLGregorianCalendar getTime(ObjectMessage msg) throws Exception {
			try {
				msg.proceed();
			} catch (IllegalArgumentException e) {
				try {
					return DatatypeFactory.newInstance().newXMLGregorianCalendar();
				} catch (DatatypeConfigurationException e1) {
					return null;
				}
			}
			return (XMLGregorianCalendar) msg.proceed();
		}
	}

	public static class CConcept {
		public static int count;

		public void increment1() {
			count++;
		}

		public void increment2() {
			count++;
		}
	}

	public static class Behaviour {
		public static int count;

		@ParameterTypes( {})
		public void increment1(VoidMessage msg) throws Exception {
			count++;
			msg.proceed();
		}

		@ParameterTypes( {})
		public void increment2(VoidMessage msg) {
			count++;
		}
	}

	public void setUp() throws Exception {
		config.addConcept(CConcept.class, "urn:test:Concept");
		config.addBehaviour(Behaviour.class, "urn:test:Concept");
		config.addConcept(IConcept.class);
		config.addBehaviour(CatchBehaviour.class);
		super.setUp();
	}

	public void testInterceptBaseMethod() throws Exception {
		CConcept.count = 0;
		Behaviour.count = 0;
		CConcept concept = con.addDesignation(
				con.getObject("urn:test:concept"), CConcept.class);
		concept.increment1();
		assertEquals(1, CConcept.count);
		assertEquals(1, Behaviour.count);
	}

	public void testOverrideBaseMethod() throws Exception {
		CConcept.count = 0;
		Behaviour.count = 0;
		CConcept concept = con.addDesignation(
				con.getObject("urn:test:concept"), CConcept.class);
		concept.increment2();
		assertEquals(1, Behaviour.count);
		assertEquals(0, CConcept.count);
	}

	public void testIllegalArgument() throws Exception {
		IConcept concept = con.addDesignation(
				con.getObject("urn:test:concept"), IConcept.class);
		ValueFactory vf = con.getValueFactory();
		Resource subj = concept.getResource();
		URI pred = vf.createURI("urn:test:date");
		Literal lit = vf.createLiteral("noon", XMLSchema.DATETIME);
		con.add(subj, pred, lit);
		try {
			concept.getDate();
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("noon"));
		}
	}

	public void testCatchIllegalArgument() throws Exception {
		IConcept concept = con.addDesignation(
				con.getObject("urn:test:concept"), IConcept.class);
		ValueFactory vf = con.getValueFactory();
		Resource subj = concept.getResource();
		URI pred = vf.createURI("urn:test:time");
		Literal lit = vf.createLiteral("noon", XMLSchema.DATETIME);
		con.add(subj, pred, lit);
		XMLGregorianCalendar zero = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		assertEquals(zero, concept.getTime());
	}
}
