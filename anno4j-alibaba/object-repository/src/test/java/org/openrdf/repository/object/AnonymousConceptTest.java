package org.openrdf.repository.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Matching;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class AnonymousConceptTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(AnonymousConceptTest.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface oneOf {
		@Iri(OWL.NAMESPACE + "oneOf")
		String[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface unionOf {
		@Iri(OWL.NAMESPACE + "unionOf")
		Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface intersectionOf {
		@Iri(OWL.NAMESPACE + "intersectionOf")
		Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface complementOf {
		@Iri(OWL.NAMESPACE + "complementOf")
		Class<?> value();
	}

	@Iri("urn:test:Nothing")
	public interface Nothing {
	}

	@Iri("urn:test:MyThing")
	public interface MyThing {
	}

	@Iri("urn:test:AConcept")
	public interface AConcept {
	}

	@Iri("urn:test:MyOtherConcept")
	public interface MyOtherConcept extends MyConcept {
	}

	@Iri("urn:test:AnotherConcept")
	public interface AnotherConcept extends MyConcept {
	}

	@Iri("urn:test:MyConcept")
	public interface MyConcept {
		String hello();
	}

	public static class MyClass implements MyConcept {

		public String hello() {
			return "world";
		}
	}

	@Matching("/main_resource")
	public interface AnonyoumsMatchesConcept extends MyConcept {

	}

	@oneOf("http://localhost/one")
	public interface AnonyoumsOneOfConcept extends MyConcept {

	}

	@unionOf( { MyOtherConcept.class, AnotherConcept.class })
	public interface AnonyoumsUnionConcept extends MyConcept {

	}

	@intersectionOf( { MyThing.class, AConcept.class })
	public interface AnonyoumsIntersectionConcept extends MyConcept {

	}

	@complementOf(Nothing.class)
	public interface AnonyoumsComplementConcept extends MyConcept {

	}

	public void testMatches() throws Exception {
		tearDown();
		config.addConcept(MyConcept.class);
		config.addBehaviour(MyClass.class);
		config.addConcept(AnonyoumsMatchesConcept.class);
		setUp();
		URIImpl id = new URIImpl("http://localhost/main_resource");
		Object main = con.getObject(id);
		assertTrue(main instanceof AnonyoumsMatchesConcept);
		assertTrue(main instanceof MyConcept);
		assertEquals(((MyConcept) main).hello(), "world");
	}

	public void testOneOf() throws Exception {
		tearDown();
		config.addConcept(MyConcept.class);
		config.addBehaviour(MyClass.class);
		config.addConcept(AnonyoumsOneOfConcept.class);
		setUp();
		URIImpl id = new URIImpl("http://localhost/one");
		Object main = con.getObject(id);
		assertTrue(main instanceof AnonyoumsOneOfConcept);
		assertTrue(main instanceof MyConcept);
		assertEquals(((MyConcept) main).hello(), "world");
	}

	public void testUnionOf() throws Exception {
		tearDown();
		config.addConcept(MyOtherConcept.class);
		config.addConcept(AnotherConcept.class);
		config.addConcept(MyConcept.class);
		config.addBehaviour(MyClass.class);
		config.addConcept(AnonyoumsUnionConcept.class);
		setUp();
		URIImpl id = new URIImpl("urn:test:my-concept");
		Object main = con.getObject(id);
		main = con.addDesignation(main, MyOtherConcept.class);
		assertTrue(main instanceof AnonyoumsUnionConcept);
		assertTrue(main instanceof MyConcept);
		assertFalse(main instanceof AnotherConcept);
		assertEquals(((MyConcept) main).hello(), "world");
	}

	public void testIntersectionOf() throws Exception {
		tearDown();
		config.addConcept(MyThing.class);
		config.addConcept(AConcept.class);
		config.addConcept(MyConcept.class);
		config.addBehaviour(MyClass.class);
		config.addConcept(AnonyoumsIntersectionConcept.class);
		setUp();
		URIImpl id = new URIImpl("urn:test:my-concept");
		Object main = con.getObject(id);
		main = con.addDesignation(main, MyThing.class);
		main = con.addDesignation(main, AConcept.class);
		assertTrue(main instanceof AnonyoumsIntersectionConcept);
		assertTrue(main instanceof MyConcept);
		assertEquals(((MyConcept) main).hello(), "world");
	}

	public void testComplementOf() throws Exception {
		tearDown();
		config.addConcept(Nothing.class);
		config.addConcept(MyConcept.class);
		config.addBehaviour(MyClass.class);
		config.addConcept(AnonyoumsComplementConcept.class);
		setUp();
		URIImpl id = new URIImpl("urn:test:something");
		Object main = con.getObject(id);
		assertTrue(main instanceof AnonyoumsComplementConcept);
		assertTrue(main instanceof MyConcept);
		assertEquals(((MyConcept) main).hello(), "world");
	}

}
