package org.openrdf.repository.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.base.RepositoryTestCase;

public class UnionOfTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(UnionOfTest.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface unionOf {
		@Iri(OWL.NAMESPACE + "unionOf")
		Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.TYPE })
	public @interface unionStringOf {
		@Iri(OWL.NAMESPACE + "unionOf")
		String[] value();
	}

	@unionOf( { Car.class, Truck.class })
	public interface CarOrTruck {
		String start();
	}

	@Iri("urn:test:Car")
	public interface Car extends CarOrTruck, CarOrLorry {
	}

	@Iri("urn:test:Truck")
	public interface Truck extends CarOrTruck {
	};

	@unionStringOf( { "urn:test:Car", "urn:test:Truck" })
	public interface CarOrLorry {
		String start();
	}

	@Iri("urn:test:Truck")
	public interface Lorry extends CarOrLorry {
	};

	public static class Engine implements CarOrTruck {

		public String start() {
			return "vroom";
		}
	}

	@Override
	public void setUp() throws Exception {
		config.addAnnotation(unionOf.class);
		config.addConcept(Car.class);
		config.addConcept(Truck.class);
		config.addConcept(Lorry.class);
		config.addConcept(CarOrLorry.class);
		config.addBehaviour(Engine.class);
		super.setUp();
	}

	public void testUnioOfBehaviour() throws Exception {
		Car car = con.addDesignation(of.createObject(), Car.class);
		assertEquals("vroom", car.start());
		assertFalse(car instanceof Truck);
		assertFalse(car instanceof Lorry);
		assertTrue(car instanceof CarOrLorry);
	}

}
