package com.github.anno4j.schema;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.annotations.*;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.namespaces.FOAF;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.owl.Restriction;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link OWLSchemaPersistingManager}.
 */
public class OWLSchemaPersistingManagerTest {

    /**
     * A person class with consistent schema annotations.
     */
    @Iri("http://example.de/#validly_annotated_person")
    private interface ValidlyAnnotatedPerson extends ResourceObject {

        @Bijective
        @MinCardinality(1) @MaxCardinality(1)
        @Iri("http://example.de/#id")
        public Set<Integer> getIds();

        @Functional @InverseFunctional
        @MinCardinality(1) @MaxCardinality(1)
        @Iri("http://example.de/#id")
        public void setIds(Set<Integer> id);

        public Set<ValidlyAnnotatedPerson> getPartners();

        public void setPartners(Set<ValidlyAnnotatedPerson> partners);

        @Transitive
        @InverseOf({"http://example.de/#has_subordinate"})
        @Iri("http://example.de/#has_boss")
        @AllValuesFrom({ValidlyAnnotatedPerson.class, Person.class})
        public Set<ValidlyAnnotatedPerson> getBosses();

        @Iri("http://example.de/#has_boss")
        public void setBosses(Set<ValidlyAnnotatedPerson> bosses);

        @Transitive
        @InverseOf({"http://example.de/#has_boss"})
        @Iri("http://example.de/#has_subordinate")
        public Set<ValidlyAnnotatedPerson> getSubordinates();

        @Iri("http://example.de/#has_subordinate")
        public void setSubordinates(Set<ValidlyAnnotatedPerson> subordinates);
    }

    @Partial
    public static abstract class ValidlyAnnotatedPersonSupport extends ResourceObjectSupport implements ValidlyAnnotatedPerson {

        @Symmetric
        @MinCardinality(value = 0, onClass = ValidlyAnnotatedPerson.class) @MaxCardinality(1)
        @Iri("http://example.de/#partner")
        private Set<ValidlyAnnotatedPerson> partners;

        @Override
        public Set<ValidlyAnnotatedPerson> getPartners() {
            return this.partners;
        }

        @Override
        public void setPartners(Set<ValidlyAnnotatedPerson> partners) {
            this.partners.clear();
            this.partners.addAll(partners);
        }
    }

    private static final String QUERY_PREFIX = "PREFIX owl: <" + OWL.NS + "> " +
                            "PREFIX rdfs: <" + RDFS.NS + "> " +
                            "PREFIX xsd: <" + XSD.NS + "> ";

    @Test
    public void testConsistentAnnotations() throws Exception {
        Anno4j anno4j = new Anno4j();

        Reflections types = new Reflections(
                new ConfigurationBuilder()
                .setUrls(
                        ClasspathHelper.forClass(ValidlyAnnotatedPerson.class, ClasspathHelper.staticClassLoader()),
                        ClasspathHelper.forClass(ValidlyAnnotatedPersonSupport.class, ClasspathHelper.staticClassLoader())
                )
                .setScanners(new MethodAnnotationsScanner(), new FieldAnnotationsScanner())
        );

        Transaction transaction = anno4j.createTransaction();
        transaction.begin();
        SchemaPersistingManager persistingManager = new OWLSchemaPersistingManager(transaction.getConnection());

        persistingManager.persistSchema(types);

        // Query property characteristics:
        String q = QUERY_PREFIX + "ASK { " +
                "   <http://example.de/#id> a owl:FunctionalProperty . " +
                "   <http://example.de/#id> a owl:InverseFunctionalProperty . " +
                "   <http://example.de/#partner> a owl:SymmetricProperty . " +
                "   <http://example.de/#has_subordinate> a owl:TransitiveProperty . " +
                "   <http://example.de/#has_boss> a owl:TransitiveProperty . " +
                "   <http://example.de/#has_boss> owl:inverseOf <http://example.de/#has_subordinate> . " +
                "   <http://example.de/#has_subordinate> owl:inverseOf <http://example.de/#has_boss> . " +
                "}";
        BooleanQuery characteristicsQuery = transaction.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL, q);
        assertTrue(characteristicsQuery.evaluate());

        // Query property restrictions:
        q = QUERY_PREFIX + "ASK { " +
                "   ?r1 a owl:Restriction . " +
                "   <http://example.de/#validly_annotated_person> rdfs:subClassOf ?r1 . " +
                "   ?r1 owl:onProperty <http://example.de/#id> . " +
                "   ?r1 owl:minCardinality ?v1 . " +
                "   FILTER ( ?v1 = 1 )" +

                "   ?r2 a owl:Restriction . " +
                "   <http://example.de/#validly_annotated_person> rdfs:subClassOf ?r2 . " +
                "   ?r2 owl:onProperty <http://example.de/#id> . " +
                "   ?r2 owl:maxCardinality ?v2 . " +
                "   FILTER ( ?v2 = 1 )" +

                "   ?r3 a owl:Restriction . " +
                "   <http://example.de/#validly_annotated_person> rdfs:subClassOf ?r3 . " +
                "   ?r3 owl:onProperty <http://example.de/#partner> . " +
                "   ?r3 owl:onClass <http://example.de/#validly_annotated_person> . " +
                "   ?r3 owl:minCardinality ?v3 . " +
                "   FILTER ( ?v3 = 0 )" +

                "   ?r4 a owl:Restriction . " +
                "   <http://example.de/#validly_annotated_person> rdfs:subClassOf ?r4 . " +
                "   ?r4 owl:onProperty <http://example.de/#has_boss> . " +
                "   ?r4 owl:allValuesFrom <http://example.de/#validly_annotated_person> . " +
                "}";

        BooleanQuery restrictionQuery = transaction.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL, q);
        assertTrue(restrictionQuery.evaluate());
    }

    @Test
    public void testMatchingExistingSchema() throws Exception {
        Anno4j anno4j = new Anno4j();

        OWLClazz myPerson = anno4j.createObject(OWLClazz.class, (Resource) new URIImpl("http://example.de/#validly_annotated_person"));
        RDFSProperty bossProperty = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.de/#has_boss"));

        Restriction bossAllValuesFromRestr = anno4j.createObject(Restriction.class);
        bossAllValuesFromRestr.setOnClazz(Sets.newHashSet(myPerson));
        bossAllValuesFromRestr.setOnProperty(Sets.newHashSet(bossProperty));
        bossAllValuesFromRestr.setAllValuesFrom(Sets.<OWLClazz>newHashSet(myPerson));

        Reflections types = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(
                                ClasspathHelper.forClass(ValidlyAnnotatedPerson.class, ClasspathHelper.staticClassLoader()),
                                ClasspathHelper.forClass(ValidlyAnnotatedPersonSupport.class, ClasspathHelper.staticClassLoader())
                        )
                        .setScanners(new MethodAnnotationsScanner(), new FieldAnnotationsScanner())
        );

        Transaction transaction = anno4j.createTransaction();
        transaction.begin();
        SchemaPersistingManager persistingManager = new OWLSchemaPersistingManager(transaction.getConnection());

        boolean exceptionThrown = false;
        try {
            persistingManager.persistSchema(types);
        } catch (SchemaPersistingManager.ContradictorySchemaException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testContradictoryExistingSchema() throws Exception {
        Anno4j anno4j = new Anno4j();

        OWLClazz myPerson = anno4j.createObject(OWLClazz.class, (Resource) new URIImpl("http://example.de/#validly_annotated_person"));
        OWLClazz foafOrganization = anno4j.createObject(OWLClazz.class, (Resource) new URIImpl(FOAF.ORGANIZATION));
        RDFSProperty bossProperty = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.de/#has_boss"));

        Restriction bossAllValuesFromRestr = anno4j.createObject(Restriction.class);
        bossAllValuesFromRestr.setOnClazz(Sets.newHashSet(myPerson));
        bossAllValuesFromRestr.setOnProperty(Sets.newHashSet(bossProperty));
        bossAllValuesFromRestr.setAllValuesFrom(Sets.newHashSet(myPerson, foafOrganization));

        Transaction transaction = anno4j.createTransaction();
        transaction.begin();
        SchemaPersistingManager persistingManager = new OWLSchemaPersistingManager(transaction.getConnection());

        Reflections types = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(
                                ClasspathHelper.forClass(ValidlyAnnotatedPerson.class, ClasspathHelper.staticClassLoader()),
                                ClasspathHelper.forClass(ValidlyAnnotatedPersonSupport.class, ClasspathHelper.staticClassLoader())
                        )
                        .setScanners(new MethodAnnotationsScanner(), new FieldAnnotationsScanner())
        );

        boolean exceptionThrown = false;
        try {
            persistingManager.persistSchema(types);
        } catch (SchemaPersistingManager.ContradictorySchemaException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}