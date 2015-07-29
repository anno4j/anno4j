package org.openrdf.repository.object.managers;

import java.util.Collection;

import junit.framework.TestCase;

import org.openrdf.annotations.Mixin;
import org.openrdf.model.impl.ValueFactoryImpl;

public class RoleMapperTest extends TestCase {

	public abstract class BehaviourClass {
		public boolean isBehaviourClass() {
			return true;
		}
	}

	@Mixin(BehaviourClass.class)
	public interface MixedClass {
		
	}

	@Mixin(name="org.openrdf.repository.object.managers.RoleMapperTest$BehaviourClass")
	public interface MixedClassName {
		
	}

	public void testMixinClass() throws Exception {
		RoleMapper rm = new RoleMapper();
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		rm.addConcept(MixedClass.class, vf.createURI("urn:MixedClass"));
		Collection<Class<?>> roles = rm.findRoles(vf.createURI("urn:MixedClass"));
		assertTrue(roles.contains(BehaviourClass.class));
	}

	public void testMixinName() throws Exception {
		RoleMapper rm = new RoleMapper();
		ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
		rm.addConcept(MixedClassName.class, vf.createURI("urn:MixedClass"));
		Collection<Class<?>> roles = rm.findRoles(vf.createURI("urn:MixedClass"));
		assertTrue(roles.contains(BehaviourClass.class));
	}

}
