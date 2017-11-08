package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.collections.RDFLists;
import com.github.anno4j.schema.model.swrl.*;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link InMemoryEvaluator}.
 */
public class InMemoryEvaluatorTest {

    private Anno4j anno4j;

    private ObjectConnection connection;

    private InMemoryEvaluator evaluator = new InMemoryEvaluator();

    private ExecutionPlanner planner = new ExecutionPlanner();


    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        connection = anno4j.getObjectRepository().getConnection();
    }

    @Test
    public void testEmptyPlan() throws Exception {
        Variable x = anno4j.createObject(Variable.class);

        Variable a = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:a"));
        Variable b = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:b"));

        DatavaluedPropertyAtom roleAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        roleAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        roleAtom.setArgument1(a);
        roleAtom.setArgument2(b);

        SolutionSet solutions = new SolutionSet();
        solutions.add(new Bindings(), x, 5);

        assertEquals(solutions, evaluator.evaluate(Lists.<Atom>newArrayList(roleAtom), solutions));
    }

    @Test
    public void testEmptyCandidates() throws Exception {
        SolutionSet solutions = new SolutionSet();

        BuiltinAtom atom = anno4j.createObject(BuiltinAtom.class);
        atom.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.EQUAL)));
        atom.setArguments(RDFLists.asRDFList(connection));

        assertEquals(solutions, evaluator.evaluate(Lists.<Atom>newArrayList(atom), solutions));
    }

    @Test
    public void testFullyDetermined() throws Exception {
        Variable x = anno4j.createObject(Variable.class);

        BuiltinAtom atom1 = anno4j.createObject(BuiltinAtom.class);
        atom1.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.MULTIPLY)));
        atom1.setArguments(RDFLists.asRDFList(connection, 12.0, 3, 4));

        BuiltinAtom atom2 = anno4j.createObject(BuiltinAtom.class);
        atom2.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.SUBTRACT)));
        atom2.setArguments(RDFLists.asRDFList(connection, 0, 42, 42));

        BuiltinAtom atom3 = anno4j.createObject(BuiltinAtom.class);
        atom3.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.SUBTRACT)));
        atom3.setArguments(RDFLists.asRDFList(connection, 42, 42, 42));

        SolutionSet candidates = new SolutionSet();
        candidates.add(new Bindings(), x, 1337);

        assertEquals(candidates, evaluator.evaluate(Lists.<Atom>newArrayList(atom1, atom2), candidates));
        assertEquals(new SolutionSet(), evaluator.evaluate(Lists.<Atom>newArrayList(atom1, atom2, atom3), candidates));
    }

    @Test
    public void testSimpleComputation() throws Exception {
        Variable x = anno4j.createObject(Variable.class);

        BuiltinAtom atom = anno4j.createObject(BuiltinAtom.class);
        atom.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.MULTIPLY)));
        atom.setArguments(RDFLists.asRDFList(connection, 12.0, x, 4));

        // Candidate solutions:
        SolutionSet candidates = new SolutionSet();
        candidates.add(new Bindings(), x, 1);
        candidates.add(new Bindings(), x, 2);
        candidates.add(new Bindings(), x, 3);

        SolutionSet solutions = evaluator.evaluate(Lists.<Atom>newArrayList(atom), candidates);

        assertEquals(1, solutions.size());
        assertEquals(3, solutions.iterator().next().get(x));
    }

    @Test
    public void testChainedComputation() throws Exception {
        Variable b = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:b"));
        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:x"));
        Variable y = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:y"));
        Variable z = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:z"));
        Variable r = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:r"));

        // We need a role-atom for the rule being well-formed:
        DatavaluedPropertyAtom roleAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        roleAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        roleAtom.setArgument1(b);
        roleAtom.setArgument2(x);

        BuiltinAtom twice = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:atom:twice"));
        twice.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.MULTIPLY)));
        twice.setArguments(RDFLists.asRDFList(connection, y, 2, x));

        BuiltinAtom plusOne = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:atom:plusOne"));
        plusOne.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        plusOne.setArguments(RDFLists.asRDFList(connection, z, x, 1));

        BuiltinAtom sum = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:atom:sum"));
        sum.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        sum.setArguments(RDFLists.asRDFList(connection, r, y, z));

        // heightInMeters(b, x) multiply(y, 2, x) add(z, x, 1) add(r, y, z)
        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.addAll(Lists.newArrayList(roleAtom, twice, plusOne, sum));

        atoms = planner.reorderAtoms(atoms, connection);

        // Candidate solutions:
        SolutionSet candidates = new SolutionSet();
        candidates.add(new Bindings(), x, 2.0);

        SolutionSet solutions = evaluator.evaluate(atoms.asList(), candidates);
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(2.0, bindings.get(x));
        assertEquals(4.0, bindings.get(y));
        assertEquals(3.0, bindings.get(z));
        assertEquals(7.0, bindings.get(r));
    }
}