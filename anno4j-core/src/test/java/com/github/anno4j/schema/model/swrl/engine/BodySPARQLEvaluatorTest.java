package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.collections.RDFLists;
import com.github.anno4j.schema.model.swrl.*;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link BodySPARQLEvaluator}.
 */
public class BodySPARQLEvaluatorTest {

    @Iri("http://example.org/Building")
    public interface Building extends ResourceObject {

        @Iri("http://example.org/heightInMeters")
        Double getHeightInMeters();

        @Iri("http://example.org/heightInMeters")
        void setHeightInMeters(Double meters);

        @Iri("http://example.org/heightInFeet")
        Double getHeightInFeet();

        @Iri("http://example.org/name")
        CharSequence getName();

        @Iri("http://example.org/name")
        void setName(CharSequence name);

        @Iri("http://example.org/next_to")
        Building getNextTo();

        @Iri("http://example.org/next_to")
        void setNextTo(Building neighbour);
    }

    private Anno4j anno4j;

    private ExecutionPlanner planner = new ExecutionPlanner();

    private BodySPARQLEvaluator evaluator = new BodySPARQLEvaluator();


    private List<Atom> toAtoms(List<Object> items) throws SWRLException {
        List<Atom> atoms = new ArrayList<>();
        for (Object item : items) {
            if(item instanceof Atom) {
                atoms.add((Atom) item);
            } else {
                throw new SWRLException("Item " + item.toString() + " in rules body must be an atom!");
            }
        }
        return atoms;
    }

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN(), null, false);

        /*URL ontUrl = getClass().getClassLoader().getResource("swrl_testing.owl");
        File file = new File(ontUrl.getFile());
        anno4j.getRepository().getConnection().add(file, "http://example.org/", RDFFormat.RDFXML);

        // Register Built-In types:
        SWRLBuiltInService builtInService = SWRLBuiltInService.getBuiltInService();
        builtInService.registerBuiltIns(anno4j.getObjectRepository().getConnection());

        // Explicitly state property instances:
        anno4j.getObjectRepository().getConnection().prepareUpdate(
                "INSERT {" +
                        "  ?p a rdfs:Property . " +
                        "} WHERE {" +
                        "  ?s ?p ?o . " +
                        "}"
        ).execute();

        anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("urn:test:myprop"));
        */
    }

    @Test
    public void testMixed() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building burjKhalifa = anno4j.createObject(Building.class, (Resource) new URIImpl("http://example.org/Burj_Khalifa"));
        burjKhalifa.setHeightInMeters(828.0);

        Rule rule = anno4j.createObject(Rule.class);

        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:b"));
        Variable meters = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:atom:m"));
        Variable feet = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:atom:f"));

        OWLClazz buildingClazz = anno4j.createObject(OWLClazz.class, (Resource) new URIImpl("http://example.org/Building"));
        RDFSProperty heightMeters = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters"));
        RDFSProperty heightFeet = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInFeet"));

        ClassAtom buildingAtom = anno4j.createObject(ClassAtom.class, (Resource) new URIImpl("urn:atom:Building"));
        buildingAtom.setClazzPredicate(buildingClazz);
        buildingAtom.setArgument1(x);

        DatavaluedPropertyAtom metersAtom = anno4j.createObject(DatavaluedPropertyAtom.class, (Resource) new URIImpl("urn:atom:heightMeters"));
        metersAtom.setPropertyPredicate(heightMeters);
        metersAtom.setArgument1(x);
        metersAtom.setArgument2(meters);

        BuiltinAtom multiplyAtom = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:atom:multiply"));
        multiplyAtom.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.MULTIPLY)));
        multiplyAtom.setArguments(RDFLists.asRDFList(connection, feet, meters, 3.28));

        DatavaluedPropertyAtom feetAtom = anno4j.createObject(DatavaluedPropertyAtom.class, (Resource) new URIImpl("urn:atom:heightFeet"));
        feetAtom.setPropertyPredicate(heightFeet);
        feetAtom.setArgument1(x);
        feetAtom.setArgument2(feet);

        // Building(x) swrlb:multiply(f, m, 3.28) heightMeters(x, m) -> heightFeet(x, f)
        rule.setBody(buildingAtom, multiplyAtom, metersAtom);
        rule.setHead(feetAtom);

        AtomList plan = planner.reorderAtoms(rule.getBody(), connection);

        SolutionSet candidates = evaluator.findCandidateBindings(plan, anno4j.getObjectRepository().getConnection());

        assertEquals(1, candidates.size());
        Bindings binding = candidates.iterator().next();
        assertEquals(burjKhalifa, binding.get(x));
        assertEquals(828.0, binding.get(meters));
    }

    @Test
    public void testEmptyBody() throws Exception {
        boolean exceptionThrown = false;
        try {
            evaluator.findCandidateBindings(anno4j.createObject(AtomList.class), anno4j.getObjectRepository().getConnection());
        } catch (SWRLInferenceEngine.IllegalSWRLRuleException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    public void testIndividual() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building building1 = anno4j.createObject(Building.class);
        Building building2 = anno4j.createObject(Building.class);
        building1.setNextTo(building2);

        Variable x = anno4j.createObject(Variable.class);

        IndividualPropertyAtom nextToAtom = anno4j.createObject(IndividualPropertyAtom.class);
        nextToAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/next_to")));
        nextToAtom.setArgument1(x);
        nextToAtom.setArgument2(building2);

        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.add(nextToAtom);

        AtomList plan = planner.reorderAtoms(atoms, connection);

        SolutionSet solutions = evaluator.findCandidateBindings(plan, connection);
        assertEquals(1, solutions.size());
        Bindings binding = solutions.getBindings().iterator().next();
        assertEquals(building1, binding.get(x));
    }

    @Test
    public void testUntypedLiteral() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building building = anno4j.createObject(Building.class);
        building.setName("Neuschwanstein Castle");

        Variable x = anno4j.createObject(Variable.class);

        DatavaluedPropertyAtom nameAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        nameAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/name")));
        nameAtom.setArgument1(x);
        nameAtom.setArgument2("Neuschwanstein Castle");

        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.add(nameAtom);

        AtomList plan = planner.reorderAtoms(atoms, connection);

        SolutionSet solutions = evaluator.findCandidateBindings(plan, connection);
        assertEquals(1, solutions.size());
        Bindings binding = solutions.getBindings().iterator().next();
        assertEquals(building, binding.get(x));
    }

    @Test
    public void testLanguageTaggedLiteral() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building building = anno4j.createObject(Building.class);
        building.setName(new LangString("Neuschwanstein Castle", "en"));

        Variable x = anno4j.createObject(Variable.class);

        DatavaluedPropertyAtom nameAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        nameAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/name")));
        nameAtom.setArgument1(x);
        nameAtom.setArgument2(new LangString("Neuschwanstein Castle", "en"));

        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.add(nameAtom);

        AtomList plan = planner.reorderAtoms(atoms, connection);

        SolutionSet solutions = evaluator.findCandidateBindings(plan, connection);
        assertEquals(1, solutions.size());
        Bindings binding = solutions.getBindings().iterator().next();
        assertEquals(building, binding.get(x));
    }

    @Test
    public void testDatatypedLiteral() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building building = anno4j.createObject(Building.class);
        building.setHeightInMeters(42.0);

        Variable x = anno4j.createObject(Variable.class);

        DatavaluedPropertyAtom heightAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        heightAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        heightAtom.setArgument1(x);
        heightAtom.setArgument2(42L); // Different datatype

        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.add(heightAtom);

        AtomList plan = planner.reorderAtoms(atoms, connection);

        SolutionSet solutions = evaluator.findCandidateBindings(plan, connection);
        assertEquals(1, solutions.size());
        Bindings binding = solutions.getBindings().iterator().next();
        assertEquals(building, binding.get(x));
    }

    @Test
    public void testBuiltin() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Building building1 = anno4j.createObject(Building.class);
        building1.setHeightInMeters(42.0);
        Building building2 = anno4j.createObject(Building.class);
        building2.setHeightInMeters(50.0);

        Variable x = anno4j.createObject(Variable.class);
        Variable y = anno4j.createObject(Variable.class);

        DatavaluedPropertyAtom heightAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        heightAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        heightAtom.setArgument1(x);
        heightAtom.setArgument2(y);

        BuiltinAtom lessThanAtom = anno4j.createObject(BuiltinAtom.class);
        lessThanAtom.setBuiltinResource(anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl(SWRLB.LESS_THAN)));
        lessThanAtom.setArguments(RDFLists.asRDFList(connection, y, 45));

        AtomList atoms = anno4j.createObject(AtomList.class);
        atoms.add(heightAtom);
        atoms.add(lessThanAtom);

        AtomList plan = planner.reorderAtoms(atoms, connection);

        SolutionSet solutions = evaluator.findCandidateBindings(plan, connection);
        assertEquals(1, solutions.size());
        Bindings binding = solutions.getBindings().iterator().next();
        assertEquals(building1, binding.get(x));
    }
}