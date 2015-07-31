package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class BridgeMethodTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(BridgeMethodTest.class);
	}

	@Iri("urn:test:Concept")
	public interface Concept {
		Concept getThis();
		Concept getThat();
	}

	@Iri("urn:test:Concept")
	public interface Sub1 extends Concept {
		Sub1 getThis();
		Sub1 getThat();
	}

	@Iri("urn:test:Concept")
	public interface Sub2 extends Concept {
		Sub2 getThis();
		Sub2 getThat();
	}

	public static abstract class Stub1 implements Sub1 {
		public static int count;
		public Stub1 getThis() {
			count++;
			return this;
		}
		public Stub1 getThat() {
			count++;
			return null;
		}
	}
	public static abstract class Stub2 implements Sub2 {
		public static int count;
		public Stub2 getThis() {
			count++;
			return this;
		}
		public Stub2 getThat() {
			count++;
			return null;
		}
	}

	public void setUp() throws Exception {
		Stub1.count = 0;
		Stub2.count = 0;
		config.addConcept(Concept.class);
		config.addConcept(Sub1.class);
		config.addConcept(Sub2.class);
		config.addBehaviour(Stub1.class);
		config.addBehaviour(Stub2.class);
		super.setUp();
	}

	public void testConflict() throws Exception {
		Concept c = con.addDesignation(con.getObject("urn:test:concept"), Concept.class);
		assertEquals(c, c.getThis());
	}

	public void testBridgeSub1() throws Exception {
		Sub1 c = con.addDesignation(con.getObject("urn:test:concept"), Sub1.class);
		assertNull(c.getThat());
		assertEquals(1, Stub1.count);
		assertEquals(1, Stub2.count);
	}

	public void testBridgeSub2() throws Exception {
		Sub2 c = con.addDesignation(con.getObject("urn:test:concept"), Sub2.class);
		assertNull(c.getThat());
		assertEquals(1, Stub1.count);
		assertEquals(1, Stub2.count);
	}

}
