package org.openrdf.repository.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advice.AdviceFactory;
import org.openrdf.repository.object.advice.AdviceProvider;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.base.RepositoryTestCase;
import org.openrdf.repository.object.traits.ObjectMessage;

public class AdviceTest extends ObjectRepositoryTestCase {
	private static final String FOAF = "urn:test:foa:";

	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(AdviceTest.class);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ProtectedBy {
		String value();
	}

	public static class ProtectedException extends RuntimeException {
		private static final long serialVersionUID = -8112067826766110429L;

		public ProtectedException(String message) {
			super(message);
		}
	}

	@Iri(FOAF + "Person")
	public interface Person {
		@ProtectedBy("doSomethingImportantPermission")
		void doSomethingImportant();
	}

	public static abstract class PersonSupport implements Person {
		public void doSomethingImportant() {
			// something
		}
	}

	public static class ProtectedAdvice implements Advice {
		private String directive;

		public ProtectedAdvice(String directive) {
			this.directive = directive;
		}

		public Object intercept(ObjectMessage msg) throws Exception {
			throw new ProtectedException(directive);
		}
	}

	public static class ProtectedAdviceFactory implements AdviceFactory,
			AdviceProvider {
		public AdviceFactory getAdviserFactory(Class<?> annotationType) {
			if (ProtectedBy.class.equals(annotationType))
				return this;
			return null;
		}

		public Advice createAdvice(Method method) {
			ProtectedBy ann = method.getAnnotation(ProtectedBy.class);
			return new ProtectedAdvice(ann.value());
		}
	}

	public void setUp() throws Exception {
		config.addConcept(Person.class);
		config.addBehaviour(PersonSupport.class);
		super.setUp();
	}

	public void test() throws Exception {
		URI name = ValueFactoryImpl.getInstance().createURI("urn:test:",
				"instance");
		Person person = con.addDesignation(con.getObject(name), Person.class);
		try {
			person.doSomethingImportant();
			fail();
		} catch (ProtectedException e) {
			// success
		}
	}
}
