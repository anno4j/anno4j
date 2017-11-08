package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.collections.RDFLists;
import com.github.anno4j.schema.model.swrl.*;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for {@link ExecutionPlanner}
 */
public class ExecutionPlanFactoryTest {

    private ClassAtom classAtom;

    private DatavaluedPropertyAtom propAtom1;

    private DatavaluedPropertyAtom propAtom2;

    private BuiltinAtom eqAtomXY;

    private BuiltinAtom eqAtomUZ;

    private BuiltinAtom addAtomUYZ;

    private BuiltinAtom eqAtomYZ;

    private BuiltinAtom addAtomWUZ;

    private BuiltinAtom addAtomVUW;

    private ObjectConnection connection;

    /**
     * @param atom The atom to check.
     * @return Returns true iff {@code atom} is one of:
     * <ul>
     *     <li>{@link ClassAtom}</li>
     *     <li>{@link IndividualPropertyAtom}</li>
     *     <li>{@link DatavaluedPropertyAtom}</li>
     * </ul>
     */
    private static boolean isClassOrRoleAtom(Object atom) {
        return atom instanceof ClassAtom
                || atom instanceof IndividualPropertyAtom
                || atom instanceof DatavaluedPropertyAtom;
    }

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();
        connection = anno4j.getObjectRepository().getConnection();

        // Variables:
        Variable u = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:u"));
        Variable v = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:v"));
        Variable w = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:w"));
        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:x"));
        Variable y = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:y"));
        Variable z = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:z"));

        // Construct atoms: C(x) p(x, y) p(x, z) equals(u, z) add(u, y, z)
        List<Object> atoms = new ArrayList<>();

        classAtom = anno4j.createObject(ClassAtom.class);
        classAtom.setClazzPredicate(anno4j.createObject(OWLClazz.class));
        classAtom.setArgument1(x);

        propAtom1 = anno4j.createObject(DatavaluedPropertyAtom.class);
        propAtom1.setPropertyPredicate(anno4j.createObject(RDFSProperty.class));
        propAtom1.setArgument1(x);
        propAtom1.setArgument2(y);

        propAtom2 = anno4j.createObject(DatavaluedPropertyAtom.class);
        propAtom2.setPropertyPredicate(anno4j.createObject(RDFSProperty.class));
        propAtom2.setArgument1(x);
        propAtom2.setArgument2(z);

        eqAtomYZ = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:eqYZ"));
        eqAtomYZ.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.EQUAL)));
        eqAtomYZ.setArguments(RDFLists.asRDFList(connection, y, z));

        eqAtomUZ = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:eqUZ"));
        eqAtomUZ.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.EQUAL)));
        eqAtomUZ.setArguments(RDFLists.asRDFList(connection, u, z));

        eqAtomXY = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:eqXY"));
        eqAtomXY.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.EQUAL)));
        eqAtomXY.setArguments(RDFLists.asRDFList(connection, x, y));

        addAtomUYZ = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:addUYZ"));
        addAtomUYZ.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        addAtomUYZ.setArguments(RDFLists.asRDFList(connection, u, y, z));

        addAtomWUZ = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:addWUZ"));
        addAtomWUZ.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        addAtomWUZ.setArguments(RDFLists.asRDFList(connection, w, u, z));

        addAtomVUW = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:builtin:addVUW"));
        addAtomVUW.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        addAtomVUW.setArguments(RDFLists.asRDFList(connection, v, u, w));
    }

    @Test
    public void testEmptyPlan() throws Exception {
        ExecutionPlanner factory = new ExecutionPlanner();
        AtomList plan = factory.reorderAtoms(connection.createObject(AtomList.class), connection);
        assertTrue(plan.isEmpty());
    }

    @Test
    public void testSimpleDependentPlan() throws Exception {
        // Determine plan:
        ExecutionPlanner factory = new ExecutionPlanner();
        AtomList atoms = connection.createObject(AtomList.class);
        atoms.addAll(Lists.newArrayList(eqAtomUZ, classAtom, addAtomUYZ, propAtom1, propAtom2));
        AtomList plan = factory.reorderAtoms(atoms, connection);

        // Test created plan:
        assertTrue(isClassOrRoleAtom(plan.get(0)));
        assertTrue(isClassOrRoleAtom(plan.get(1)));
        assertTrue(isClassOrRoleAtom(plan.get(2)));
        assertEquals(addAtomUYZ, plan.get(3));
        assertEquals(eqAtomUZ, plan.get(4));
    }

    @Test
    public void testMultiDependentPlan() throws Exception {
        // Axioms: C(x) p(x, y) p(x, z) add(v, u, w) add(u, y, z) add(w, u, z) eq(x, y)
        // Determine plan:
        ExecutionPlanner factory = new ExecutionPlanner();
        AtomList atoms = connection.createObject(AtomList.class);
        atoms.addAll(Lists.newArrayList(classAtom, propAtom1, propAtom2, addAtomVUW, addAtomUYZ, addAtomWUZ));
        AtomList plan = factory.reorderAtoms(atoms, connection);

        // Test created plan:
        assertEquals(6, plan.size());
        assertTrue(isClassOrRoleAtom(plan.get(0)));
        assertTrue(isClassOrRoleAtom(plan.get(1)));
        assertTrue(isClassOrRoleAtom(plan.get(2)));
        assertEquals(addAtomUYZ, plan.get(3));
        assertEquals(addAtomWUZ, plan.get(4));
        assertEquals(addAtomVUW, plan.get(5));
    }

    @Test
    public void testIndependentPlan() throws Exception {
        // Determine plan:
        ExecutionPlanner factory = new ExecutionPlanner();
        AtomList atoms = connection.createObject(AtomList.class);
        atoms.addAll(Lists.newArrayList(eqAtomYZ, classAtom, propAtom1, propAtom2, addAtomUYZ));
        AtomList plan = factory.reorderAtoms(atoms, connection);

        // Test created plan:
        assertTrue(isClassOrRoleAtom(plan.get(0)));
        assertTrue(isClassOrRoleAtom(plan.get(1)));
        assertTrue(isClassOrRoleAtom(plan.get(2)));
        assertFalse(isClassOrRoleAtom(plan.get(3)));
        assertFalse(isClassOrRoleAtom(plan.get(4)));
    }

    @Test
    public void testUnderDeterminedPlan() throws Exception {
        // Determine plan:
        ExecutionPlanner factory = new ExecutionPlanner();

        AtomList atoms = connection.createObject(AtomList.class);
        atoms.addAll(Lists.newArrayList(eqAtomUZ, classAtom));

        boolean exceptionThrown = false;
        try {
            factory.reorderAtoms(atoms, connection);
        } catch (SWRLInferenceEngine.UnboundVariableException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}