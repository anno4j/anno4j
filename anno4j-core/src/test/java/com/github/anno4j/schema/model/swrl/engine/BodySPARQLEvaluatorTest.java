package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.collections.RDFLists;
import com.github.anno4j.schema.model.swrl.*;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.ArrayList;
import java.util.List;

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
    }

    private Anno4j anno4j;

    private ExecutionPlanFactory planner = new ExecutionPlanFactory();

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
    public void findCandidateBindings() throws Exception {
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

        rule.setBody(buildingAtom, multiplyAtom, metersAtom);
        rule.setHead(feetAtom);

        /*List<Atom> plan = planner.reorderAtoms(toAtoms(rule.getBody()));

        SolutionSet candidates = evaluator.findCandidateBindings(plan, anno4j.getObjectRepository().getConnection());
*/

        // Building(?b) && heightMeters(?b, ?m) && multiply(?f, ?m, 3.28) -> heightFeet(?b, ?f)

        // TODO remove:
        SWRLInferenceEngine engine = new SWRLInferenceEngine();
        engine.executeSWRLRuleBase(connection, Sets.newHashSet(rule));

        /*assertEquals(1, candidates.size());
        Bindings binding = candidates.iterator().next();
        assertEquals(burjKhalifa, binding.get(x));
        assertEquals(828.0, binding.get(meters));*/
    }

}