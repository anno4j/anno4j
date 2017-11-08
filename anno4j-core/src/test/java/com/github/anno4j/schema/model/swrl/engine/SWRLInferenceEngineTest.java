package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.collections.RDFLists;
import com.github.anno4j.schema.model.swrl.*;
import com.github.anno4j.schema.model.swrl.engine.conflictorder.SequentialStrategy;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.advisers.helpers.RemotePropertySet;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Set;

import static org.junit.Assert.*;

public class SWRLInferenceEngineTest {

    @Iri("http://example.org/Person")
    public interface Person extends ResourceObject {

        @Iri("http://example.org/id")
        Set<Integer> getIds();

        @Iri("http://example.org/id")
        void setIds(Set<Integer> id);

        @Iri("http://example.org/name")
        String getName();

        @Iri("http://example.org/name")
        void setName(String name);

        @Iri("http://example.org/isMale")
        boolean getIsMale();

        @Iri("http://example.org/isMale")
        void setIsMale(boolean isMale);

        @Iri("http://example.org/hasFather")
        Person getFather();

        @Iri("http://example.org/hasFather")
        void setFather(Person father);

        @Iri("http://example.org/hasBrother")
        Person getBrother();

        @Iri("http://example.org/hasBrother")
        void setBrother(Person brother);

        @Iri("http://example.org/hasUncle")
        Person getUncle();

        @Iri("http://example.org/hasUncle")
        void setUncle(Person uncle);
    }

    @Test
    public void testIterative() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Person person1 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:person1"));
        Person father1 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:father1"));
        Person uncle1 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:uncle1"));
        person1.setFather(father1);
        father1.setBrother(uncle1);
        uncle1.setName("Sepp");
        Person person2 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:person2"));
        Person father2 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:father2"));
        Person uncle2 = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:uncle2"));
        person2.setFather(father2);
        father2.setBrother(uncle2);
        uncle2.setName("Hans");

        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:x"));
        Variable y = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:y"));
        Variable z = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:z"));
        Variable n = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:n"));

        // hasFather(x, y)
        IndividualPropertyAtom hasFatherAtom = anno4j.createObject(IndividualPropertyAtom.class);
        hasFatherAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/hasFather")));
        hasFatherAtom.setArgument1(x);
        hasFatherAtom.setArgument2(y);

        // hasBrother(y, z)
        IndividualPropertyAtom hasBrotherAtom = anno4j.createObject(IndividualPropertyAtom.class);
        hasBrotherAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/hasBrother")));
        hasBrotherAtom.setArgument1(y);
        hasBrotherAtom.setArgument2(z);

        // hasUncle(x, z)
        IndividualPropertyAtom hasUncleAtom = anno4j.createObject(IndividualPropertyAtom.class);
        hasUncleAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/hasUncle")));
        hasUncleAtom.setArgument1(x);
        hasUncleAtom.setArgument2(z);

        // name(z, n)
        DatavaluedPropertyAtom nameAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        nameAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/name")));
        nameAtom.setArgument1(z);
        nameAtom.setArgument2(n);

        // equals(n, "Sepp")
        BuiltinAtom nameCheckAtom = anno4j.createObject(BuiltinAtom.class);
        nameCheckAtom.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.EQUAL)));
        nameCheckAtom.setArguments(RDFLists.asRDFList(connection, n, "Sepp"));

        // isMale(z, true)
        DatavaluedPropertyAtom isMaleAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        isMaleAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/isMale")));
        isMaleAtom.setArgument1(z);
        isMaleAtom.setArgument2(true);

        // Rule 1 (matches first): name(z, n) equal(n, "Sepp") => isMale(z, true)
        Rule rule1 = anno4j.createObject(Rule.class);
        rule1.setBody(nameAtom, nameCheckAtom);
        rule1.setHead(isMaleAtom);

        // Rule 2 (matches second): hasFather(x, y) hasBrother(y, z) isMale(z, true) => hasUncle(x, z)
        Rule rule2 = anno4j.createObject(Rule.class);
        rule2.setBody(hasFatherAtom, hasBrotherAtom, isMaleAtom);
        rule2.setHead(hasUncleAtom);

        // Do magic:
        SWRLInferenceEngine engine = new SWRLInferenceEngine(Lists.newArrayList(rule1, rule2), connection);
        engine.executeSWRLRuleBase();

        assertTrue(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <urn:person:uncle1> <http://example.org/isMale> true ." +
                        "   <urn:person:person1> <http://example.org/hasUncle> <urn:person:uncle1> ." +
                        "}"
        ).evaluate());

        assertFalse(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <urn:person:person2> <http://example.org/hasUncle> <urn:person:uncle2> ." +
                        "}"
        ).evaluate());
    }

    @Test(timeout = 30000) // Timeout 30s. Test fails if inference takes too long
    public void testTermination() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Person person = anno4j.createObject(Person.class);
        person.setIds(Sets.newHashSet(1));

        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:x"));
        Variable y = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:y"));
        Variable z = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:z"));

        // Create rule:
        // Infinitely often increments ID and adds it.
        // id(x, y) add(z, y, 1) => id(x, z)
        DatavaluedPropertyAtom propAtomXY = anno4j.createObject(DatavaluedPropertyAtom.class, (Resource) new URIImpl("urn:atom:idXY"));
        propAtomXY.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/id")));
        propAtomXY.setArgument1(x);
        propAtomXY.setArgument2(y);

        BuiltinAtom addAtom = anno4j.createObject(BuiltinAtom.class, (Resource) new URIImpl("urn:atom:add"));
        addAtom.setBuiltinResource(anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(SWRLB.ADD)));
        addAtom.setArguments(RDFLists.asRDFList(connection, z, y, 1));

        DatavaluedPropertyAtom propAtomXZ = anno4j.createObject(DatavaluedPropertyAtom.class, (Resource) new URIImpl("urn:atom:idXZ"));
        propAtomXZ.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/id")));
        propAtomXZ.setArgument1(x);
        propAtomXZ.setArgument2(z);

        Rule rule = anno4j.createObject(Rule.class);
        rule.setBody(propAtomXY, addAtom);
        rule.setHead(propAtomXZ);

        SWRLInferenceEngine engine = new SWRLInferenceEngine(Lists.newArrayList(rule), connection);
        engine.setMaxExecutionRounds(5);
        engine.setConflictResolutionStrategy(new SequentialStrategy());

        engine.executeSWRLRuleBase();
        Set<?> ids = person.getIds();
        ((RemotePropertySet) ids).refresh(); // Refresh values. Can be removed after resolution of issue #156

        assertEquals(6, ids.size());
    }

    @Test
    public void testAxioms() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Person sepp = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:sepp"));
        Person maria = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:person:maria"));
        sepp.setName("Sepp");
        maria.setName("Maria");

        Variable x = anno4j.createObject(Variable.class, (Resource) new URIImpl("urn:var:x"));

        // Positive assertion: => isMale(x, true)
        // isMale(x, true)
        DatavaluedPropertyAtom isMaleAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        isMaleAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/isMale")));
        isMaleAtom.setArgument1(x);
        isMaleAtom.setArgument2(true);
        Rule positiveAxiom = anno4j.createObject(Rule.class);
        positiveAxiom.setHead(isMaleAtom);

        // Negative assertion: name(x, "Sepp") =>
        DatavaluedPropertyAtom nameAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        nameAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/name")));
        nameAtom.setArgument1(x);
        nameAtom.setArgument2("Maria");
        Rule negativeAxiom = anno4j.createObject(Rule.class);
        negativeAxiom.setBody(nameAtom);

        // Rule: Person(x) => hasId(x, 1)
        ClassAtom isPersonAtom = anno4j.createObject(ClassAtom.class);
        isPersonAtom.setClazzPredicate(anno4j.createObject(OWLClazz.class, (Resource) new URIImpl("http://example.org/Person")));
        isPersonAtom.setArgument1(x);

        DatavaluedPropertyAtom idAtom = anno4j.createObject(DatavaluedPropertyAtom.class);
        idAtom.setPropertyPredicate(anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/id")));
        idAtom.setArgument1(x);
        idAtom.setArgument2(1);

        Rule rule = anno4j.createObject(Rule.class);
        rule.setBody(isPersonAtom);
        rule.setHead(idAtom);

        // Do magic:
        SWRLInferenceEngine engine = new SWRLInferenceEngine(Lists.newArrayList(rule, positiveAxiom, negativeAxiom), connection);
        engine.executeSWRLRuleBase();

        assertTrue(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <urn:person:sepp> <http://example.org/isMale> true ." +
                        "   <urn:person:sepp> <http://example.org/id> 1 ." +
                        "}"
        ).evaluate());

        assertFalse(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <urn:person:maria> <http://example.org/isMale> true ." +
                        "}"
        ).evaluate());
        assertFalse(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   <urn:person:maria> <http://example.org/id> 1 ." +
                        "}"
        ).evaluate());
    }
}