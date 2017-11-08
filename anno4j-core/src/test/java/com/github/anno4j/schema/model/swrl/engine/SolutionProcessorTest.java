package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.swrl.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SolutionProcessor}.
 */
public class SolutionProcessorTest {

    /**
     * The {@link SolutionProcessor} instance tested.
     */
    private SolutionProcessor processor = new SolutionProcessor();

    @Test
    public void testNonAxiomatic() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Some building resources for testing:
        ResourceObject building1 = anno4j.createObject(ResourceObject.class);
        ResourceObject building2 = anno4j.createObject(ResourceObject.class);

        // Variables used:
        Variable x = anno4j.createObject(Variable.class);
        Variable y = anno4j.createObject(Variable.class);
        Variable z = anno4j.createObject(Variable.class);

        // Building(x)
        ClassAtom classAtom = anno4j.createObject(ClassAtom.class);
        classAtom.setClazzPredicate(anno4j.createObject(ClassAtom.class, (Resource) new URIImpl("http://example.org/Building")));
        classAtom.setArgument1(x);

        // next_to(x, y)
        IndividualPropertyAtom individualPropAtom = anno4j.createObject(IndividualPropertyAtom.class);
        individualPropAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/next_to")));
        individualPropAtom.setArgument1(x);
        individualPropAtom.setArgument2(y);

        // heightInMeters(x, 1337)
        DatavaluedPropertyAtom dataPropAtom1 = anno4j.createObject(DatavaluedPropertyAtom.class);
        dataPropAtom1.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        dataPropAtom1.setArgument1(x);
        dataPropAtom1.setArgument2(1337.0);

        // name(x, z)
        DatavaluedPropertyAtom dataPropAtom2 = anno4j.createObject(DatavaluedPropertyAtom.class);
        dataPropAtom2.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/name")));
        dataPropAtom2.setArgument1(x);
        dataPropAtom2.setArgument2(z);

        AtomList head = anno4j.createObject(AtomList.class);
        head.addAll(Lists.<Atom>newArrayList(classAtom, individualPropAtom, dataPropAtom1, dataPropAtom2));

        // Create a solution as determined by SWRL rule body evaluation:
        Bindings bindings = new Bindings();
        bindings.bind(x, building1);
        bindings.bind(y, building2);
        bindings.bind(z, new LangString("Some Building", "en"));

        // Commit without axiomatic assertions:
        processor.commitHead(head, bindings, new HashSet<Atom>(), connection);

        BooleanQuery query = connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <" + building1.getResourceAsString() + "> a <http://example.org/Building> . " +
                        "   <" + building1.getResourceAsString() + "> <http://example.org/next_to> <" + building2.getResourceAsString() + "> . " +
                        "   <" + building1.getResourceAsString() + "> <http://example.org/heightInMeters> ?hm . " +
                        "   <" + building1.getResourceAsString() + "> <http://example.org/name> ?n . " +
                        "   FILTER(?hm = 1337 && LANG(?n) = 'en' && STR(?n) = 'Some Building') " +
                        "}"
        );
        assertTrue(query.evaluate());
    }

    @Test
    public void testAxiomaticAssertions() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Some building resources for testing:
        ResourceObject building = anno4j.createObject(ResourceObject.class);

        Variable x = anno4j.createObject(Variable.class);

        // Let Building(x) be an axiom:
        ClassAtom classAtom = anno4j.createObject(ClassAtom.class);
        classAtom.setClazzPredicate(anno4j.createObject(ClassAtom.class, (Resource) new URIImpl("http://example.org/Building")));
        classAtom.setArgument1(x);

        // heightInMeters(x, 1337)
        DatavaluedPropertyAtom dataPropAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        dataPropAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/heightInMeters")));
        dataPropAtom.setArgument1(x);
        dataPropAtom.setArgument2(1337.0);

        // The head consists of only one atom:
        AtomList head = anno4j.createObject(AtomList.class);
        head.addAll(Lists.<Atom>newArrayList(dataPropAtom));

        Bindings bindings = new Bindings();
        bindings.bind(x, building);

        processor.commitHead(head, bindings, Sets.<Atom>newHashSet(classAtom), connection);

        BooleanQuery query = connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <" + building.getResourceAsString() + "> a <http://example.org/Building> . " +
                        "   <" + building.getResourceAsString() + "> <http://example.org/heightInMeters> ?hm . " +
                        "   FILTER(?hm = 1337) " +
                        "}"
        );
        assertTrue(query.evaluate());
    }
}