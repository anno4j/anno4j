package org.openrdf.repository.object;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class CovarianceTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(CovarianceTest.class);
	}

	@Iri("urn:test:Base")
	public interface Base<B> {
		B getParent();
		void setParent(B parent);
		B[] getChildren();
		void setChildren(B[] children);
		@Iri("urn:test:sibling")
		B getSibling();
		void setSibling(B sibling);
		@Iri("urn:test:self")
		B getSelf();
		void setSelf(B self);
	}

	@Iri("urn:test2:Covariance")
	public interface Covariance extends Base<Covariance> {
		@Iri("urn:test2:sibling")
		Covariance getSibling();
		void setSibling(Covariance sibling);
	}

	public abstract static class CovarianceSupport implements Covariance {
		private Covariance parent;
		private Covariance[] children;
		public Covariance getParent() {
			return parent;
		}
		public void setParent(Covariance parent) {
			this.parent = parent;
		}
		public Covariance[] getChildren() {
			return children;
		}
		public void setChildren(Covariance[] children) {
			this.children = children;
		}
	}

	public void testNumberOfBaseMethods() throws Exception {
		assertEquals(8, Base.class.getDeclaredMethods().length);
		Base obj = con.addDesignation(con.getObjectFactory().createObject(), Base.class);
		assertEquals(1, findMethods(obj, "getParent").size());
		assertEquals(1, findMethods(obj, "setParent").size());
		assertEquals(1, findMethods(obj, "getChildren").size());
		assertEquals(1, findMethods(obj, "setChildren").size());
		assertEquals(1, findMethods(obj, "getSibling").size());
		assertEquals(1, findMethods(obj, "setSibling").size());
		assertEquals(1, findMethods(obj, "getSelf").size());
		assertEquals(1, findMethods(obj, "setSelf").size());
	}

	public void testNumberOfCovarianceMethods() throws Exception {
		assertEquals(2, Covariance.class.getDeclaredMethods().length);
		Covariance obj = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		// support class with bridges
		assertEquals(2, findMethods(obj, "getParent").size());
		assertEquals(2, findMethods(obj, "setParent").size());
		assertEquals(2, findMethods(obj, "getChildren").size());
		assertEquals(2, findMethods(obj, "setChildren").size());
		// two properties
		assertEquals(2, findMethods(obj, "getSibling").size());
		assertEquals(2, findMethods(obj, "setSibling").size());
		// only one property
		assertEquals(1, findMethods(obj, "getSelf").size());
		assertEquals(1, findMethods(obj, "setSelf").size());
	}

	public void testCovariance() throws Exception {
		Covariance obj = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Covariance parent = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		obj.setParent(parent);
		assertEquals(parent, Covariance.class.getMethod("getParent").invoke(obj));
		assertEquals(parent, obj.getParent());
		Base base = obj;
		base.setParent(parent);
		assertEquals(parent, Base.class.getMethod("getParent").invoke(obj));
		assertEquals(parent, base.getParent());
	}

	public void testArrayCovariance() throws Exception {
		Covariance obj = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Covariance child = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Covariance[] children = new Covariance[]{child};
		obj.setChildren(children);
		assertEquals(children, obj.getChildren());
		Base base = obj;
		base.setChildren(children);
		assertEquals(children, base.getChildren());
	}

	public void testDifferentProperties() throws Exception {
		Covariance obj = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Covariance sibling = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Base base = obj;
		base.setSibling(sibling);
		assertEquals(sibling, base.getSibling());
		assertEquals(sibling, obj.getSibling());
		obj.setSibling(null);
		// base property should remain unchanged
		assertEquals(sibling, base.getSibling());
		base.setSibling(null);
		assertEquals(null, base.getSibling());
		obj.setSibling(sibling);
		assertEquals(sibling, obj.getSibling());
		assertEquals(sibling, base.getSibling());
	}

	public void testSameProperty() throws Exception {
		Covariance obj = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Covariance self = con.addDesignation(con.getObjectFactory().createObject(), Covariance.class);
		Base base = obj;
		base.setSelf(self);
		assertEquals(self, base.getSelf());
		assertEquals(self, obj.getSelf());
		obj.setSelf(null);
		assertEquals(null, obj.getSelf());
		assertEquals(null, base.getSelf());
		obj.setSelf(self);
		assertEquals(self, obj.getSelf());
		assertEquals(self, base.getSelf());
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(Base.class);
		config.addConcept(Covariance.class);
		config.addBehaviour(CovarianceSupport.class);
		super.setUp();
	}

	private Set<Method> findMethods(Object obj, String name) {
		Set<Method> methods = new HashSet<Method>();
		for (Method m : obj.getClass().getMethods()) {
			if (m.getName().equals(name))
				methods.add(m);
		}
		System.out.println(methods);
		return methods;
	}

}
