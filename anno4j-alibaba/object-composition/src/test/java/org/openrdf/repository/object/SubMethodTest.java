package org.openrdf.repository.object;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advice.AdviceFactory;
import org.openrdf.repository.object.advice.AdviceProvider;
import org.openrdf.repository.object.composition.ClassResolver;
import org.openrdf.repository.object.traits.ObjectMessage;

public class SubMethodTest extends TestCase {
	private ClassResolver resolver;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface SubClassOf {
		@Iri("http://www.w3.org/2000/01/rdf-schema#subClassOf")
		String[] value();
	}

	@Iri("urn:test:Viewable")
	public interface Viewable {
		@Iri("urn:test:viewTrue")
		@SubClassOf("urn:test:thingTrue")
		boolean isTrue();
	}

	@Iri("urn:test:File")
	public interface File extends Viewable {
		@Iri("urn:test:fileTrue")
		@SubClassOf("urn:test:viewTrue")
		boolean isTrue();
	}

	@Iri("urn:test:Font")
	public interface Font extends File {
		@Iri("urn:test:fontTrue")
		@SubClassOf("urn:test:fileTrue")
		boolean isTrue(Object arg1);
	}

	@Iri("urn:test:Vector")
	public interface Vector extends File {
		@Iri("urn:test:vectorTrue")
		@SubClassOf("urn:test:viewTrue")
		boolean isTrue(Object arg1);
	}

	public static class BooleanAdvice implements AdviceProvider, AdviceFactory {
		public static String override;

		@Override
		public AdviceFactory getAdviserFactory(Class<?> annotationType) {
			if (SubClassOf.class.equals(annotationType))
				return this;
			else
				return null;
		}

		@Override
		public Advice createAdvice(final Method method) {
			return new Advice() {
				
				@Override
				public Object intercept(ObjectMessage message) throws Exception {
					String iri = method.getAnnotation(Iri.class).value();
					if (iri.equals(override))
						return true;
					return message.proceed();
				}
			};
		}
		
	}

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	public SubMethodTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		BooleanAdvice.override = null;
		resolver = new ClassResolver();
		resolver.getRoleMapper().addConcept(Viewable.class);
		resolver.getRoleMapper().addConcept(File.class);
		resolver.getRoleMapper().addConcept(Font.class);
		resolver.getRoleMapper().addConcept(Vector.class);
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDefaultValueFont() throws Exception {
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Font"));
		Class<? extends Font> classFile = (Class<? extends Font>) resolver.resolveEntity(entity, type);
		Font font = classFile.newInstance();
		assertFalse(font.isTrue(null));
	}

	public void testViewableSupportFont() throws Exception {
		BooleanAdvice.override = "urn:test:viewTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Font"));
		Class<? extends Font> classFile = (Class<? extends Font>) resolver.resolveEntity(entity, type);
		Font font = classFile.newInstance();
		assertTrue(font.isTrue(null));
	}

	public void testFileSupportFont() throws Exception {
		BooleanAdvice.override = "urn:test:fileTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Font"));
		Class<? extends Font> classFile = (Class<? extends Font>) resolver.resolveEntity(entity, type);
		Font font = classFile.newInstance();
		assertTrue(font.isTrue(null));
	}

	public void testFontSupportFont() throws Exception {
		BooleanAdvice.override = "urn:test:fontTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Font"));
		Class<? extends Font> classFile = (Class<? extends Font>) resolver.resolveEntity(entity, type);
		Font font = classFile.newInstance();
		assertTrue(font.isTrue(null));
	}

	public void testVectorSupportFont() throws Exception {
		BooleanAdvice.override = "urn:test:vectorTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Font"));
		Class<? extends Font> classFile = (Class<? extends Font>) resolver.resolveEntity(entity, type);
		Font font = classFile.newInstance();
		assertFalse(font.isTrue(null));
	}

	public void testDefaultValueVector() throws Exception {
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Vector"));
		Class<? extends Vector> classFile = (Class<? extends Vector>) resolver.resolveEntity(entity, type);
		Vector vector = classFile.newInstance();
		assertFalse(vector.isTrue(null));
	}

	public void testViewableSupportVector() throws Exception {
		BooleanAdvice.override = "urn:test:viewTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Vector"));
		Class<? extends Vector> classFile = (Class<? extends Vector>) resolver.resolveEntity(entity, type);
		Vector vector = classFile.newInstance();
		assertTrue(vector.isTrue(null));
	}

	public void testFontSupportVector() throws Exception {
		BooleanAdvice.override = "urn:test:fontTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Vector"));
		Class<? extends Vector> classFile = (Class<? extends Vector>) resolver.resolveEntity(entity, type);
		Vector vector = classFile.newInstance();
		assertFalse(vector.isTrue(null));
	}

	public void testVectorSupportVector() throws Exception {
		BooleanAdvice.override = "urn:test:vectorTrue";
		URI entity = vf.createURI("urn:test:entity");
		Set<URI> type = Collections.singleton(vf.createURI("urn:test:Vector"));
		Class<? extends Vector> classFile = (Class<? extends Vector>) resolver.resolveEntity(entity, type);
		Vector vector = classFile.newInstance();
		assertTrue(vector.isTrue(null));
	}

}
