package com.github.anno4j.transaction;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.ValidatedTransaction;
import com.github.anno4j.annotations.*;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.SchemaPersistingManager;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for {@link ValidatedTransaction}.
 */
public class ValidatedTransactionTest {

    /**
     * A resource class for testing. Defines one method for each annotation type.
     */
    @Iri("http://example.de/validated_transaction_test_resource")
    public interface ValidatedTransactionTestResource extends ResourceObject {

        @Functional
        @Iri("http://example.de/functional")
        Set<Integer> getFunctional();

        @InverseFunctional
        @Iri("http://example.de/inverse_functional")
        Set<Integer> getInverseFunctional();

        @Bijective
        @Iri("http://example.de/bijective")
        Set<ValidatedTransactionTestResource> getBijective();

        @Transitive
        @Iri("http://example.de/transitive")
        Set<ValidatedTransactionTestResource> getTransitive();

        @Symmetric
        @Iri("http://example.de/symmetric")
        Set<ValidatedTransactionTestResource> getSymmetric();

        @InverseOf("http://example.de/inverseof_2")
        @Iri("http://example.de/inverseof_1")
        Set<ValidatedTransactionTestResource> getInverseOf1();

        @Iri("http://example.de/inverseof_2")
        Set<ValidatedTransactionTestResource> getInverseOf2();

        @Iri("http://example.de/superproperty1")
        Set<Integer> getSuperproperty1();

        @SubPropertyOf("http://example.de/superproperty1")
        @Iri("http://example.de/superproperty2")
        Set<Integer> getSuperproperty2();

        @SubPropertyOf("http://example.de/superproperty2")
        @Iri("http://example.de/sub_property")
        Set<Integer> getSubpropertyOf();

        @MinCardinality(2)
        @MaxCardinality(3)
        @Iri("http://example.de/cardinality")
        Set<Integer> getCardinality();

        @MinCardinality(value = 2, onClass = ValidatedTransactionTestSpecialResource.class)
        @MaxCardinality(value = 3, onClass = ValidatedTransactionTestSpecialResource.class)
        @Iri("http://example.de/qualified_cardinality")
        Set<ValidatedTransactionTestResource> getQualifiedCardinality();

        @AllValuesFrom({ValidatedTransactionTestSpecialResource.class})
        @Iri("http://example.de/all_values_from")
        Set<ValidatedTransactionTestResource> getAllValuesFrom();

        @SomeValuesFrom({ValidatedTransactionTestSpecialResource.class})
        @Iri("http://example.de/some_values_from")
        Set<ValidatedTransactionTestResource> getSomeValuesFrom();

        @Iri("http://example.de/functional")
        void setFunctional(Set<Integer> values);

        @Iri("http://example.de/functional")
        void setInverseFunctional(Set<Integer> values);

        @Iri("http://example.de/bijective")
        void setBijective(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/transitive")
        void setTransitive(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/symmetric")
        void setSymmetric(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/inverseof_1")
        void setInverseOf1(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/inverseof_2")
        void setInverseOf2(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/superproperty1")
        void setSuperproperty1(Set<Integer> values);

        @Iri("http://example.de/superproperty2")
        void setSuperproperty2(Set<Integer> values);

        @Iri("http://example.de/sub_property")
        void setSubpropertyOf(Set<Integer> values);

        @Iri("http://example.de/cardinality")
        void setCardinality(Set<Integer> values);

        @Iri("http://example.de/qualified_cardinality")
        void setQualifiedCardinality(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/all_values_from")
        void setAllValuesFrom(Set<ValidatedTransactionTestResource> values);

        @Iri("http://example.de/some_values_from")
        void setSomeValuesFrom(Set<ValidatedTransactionTestResource> values);
    }

    @Iri("http://example.de/validated_transaction_test_resource_child")
    public interface ValidatedTransactionTestSpecialResource extends ValidatedTransactionTestResource { }



    @Test
    public void testPersistence() throws Exception {
        Anno4j anno4j = new Anno4j();

        // Add some initial objects:
        ValidatedTransactionTestResource r = anno4j.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("http://example.de/res1"));
        r.setCardinality(Sets.<Integer>newHashSet(1, 2, 3));
        r.setSuperproperty1(Sets.<Integer>newHashSet(1, 2));

        // Open the validated transaction:
        Transaction transaction = anno4j.createValidatedTransaction();
        r = transaction.findByID(ValidatedTransactionTestResource.class, "http://example.de/res1");
        assertNotNull(r);
        r.setCardinality(Sets.newHashSet(1, 2));

        r = transaction.findByID(ValidatedTransactionTestResource.class, "http://example.de/res1");
        assertEquals(Sets.newHashSet(1, 2), r.getCardinality());

        // Test working context active:
        BooleanQuery query = transaction.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL, "ASK " +
                "FROM NAMED <urn:anno4j:valtrans0_working> " +
                "{" +
                "   ?s ?p ?o . " +
                "}");
        assertTrue(query.evaluate());

        // Commit the transaction:
        transaction.commit();

        // Test that working context is cleared:
        transaction = anno4j.createTransaction();
        query = transaction.getConnection().prepareBooleanQuery(QueryLanguage.SPARQL, "ASK " +
                "FROM NAMED <urn:anno4j:valtrans0_working> " +
                "{" +
                "   ?s ?p ?o . " +
                "}");
        assertFalse(query.evaluate());
    }

    @Test
    public void testIndirectModification() throws Exception {
        Anno4j anno4j = new Anno4j();

        URI resource1URI = new URIImpl("http://example.de/r1");
        ValidatedTransactionTestResource resource1 = anno4j.createObject(ValidatedTransactionTestResource.class, (Resource) resource1URI);
        ValidatedTransactionTestResource resource2 = anno4j.createObject(ValidatedTransactionTestResource.class);
        resource1.setTransitive(Sets.newHashSet(resource2));

        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        ValidatedTransactionTestResource foundResource = transaction.findByID(ValidatedTransactionTestResource.class, resource1URI);
        foundResource.getTransitive().iterator().next().setCardinality(Sets.newHashSet(1, 2, 3, 4, 5));

        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testContradictorySchema() throws Exception {
        Anno4j anno4j = new Anno4j();

        // Insert contradictory minCardinality:
        Transaction setupTransaction = anno4j.createTransaction();
        setupTransaction.begin();
        setupTransaction.getConnection().prepareUpdate("INSERT DATA { " +
                "     <http://example.de/validated_transaction_test_resource> rdfs:subClassOf _:restr . " +
                "     _:restr a owl:Restriction . " +
                "     _:restr owl:onProperty <http://example.de/cardinality> . " +
                "     _:restr owl:minCardinality '42'^^xsd:nonNegativeInteger . " +
                "}").execute();
        setupTransaction.commit();

        boolean exceptionThrown = false;
        try {
            anno4j.createValidatedTransaction();
        } catch (SchemaPersistingManager.ContradictorySchemaException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // More tricky. Functional must not work with minCardinality of 2:
        setupTransaction = anno4j.createTransaction();
        setupTransaction.begin();
        setupTransaction.getConnection().prepareUpdate("INSERT DATA { " +
                "     <http://example.de/cardinality> a owl:FunctionalProperty . " +
                "}").execute();
        setupTransaction.commit();

        exceptionThrown = false;
        try {
            anno4j.createValidatedTransaction();
        } catch (SchemaPersistingManager.ContradictorySchemaException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidFunctional() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        // Test resource with single value for functional property:
        ValidatedTransactionTestSpecialResource resource = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource.setFunctional(Sets.newHashSet(1));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidFunctional() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        // Test resource with single value for functional property:
        ValidatedTransactionTestSpecialResource resource = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource.setFunctional(Sets.newHashSet(1, 2));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidInverseFunctional() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        // Test resource with single value for inverse functional property:
        ValidatedTransactionTestSpecialResource resource1 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestSpecialResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestSpecialResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource1.setInverseFunctional(Sets.newHashSet(1));
        resource2.setInverseFunctional(Sets.newHashSet(2, 3));
        resource3.setInverseFunctional(Sets.newHashSet(4));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidInverseFunctional() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        // Test resource with single value for inverse functional property:
        ValidatedTransactionTestSpecialResource resource1 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestSpecialResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestSpecialResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource1.setInverseFunctional(Sets.newHashSet(1, 2));
        resource2.setInverseFunctional(Sets.newHashSet(2, 3));
        resource3.setInverseFunctional(Sets.newHashSet(4));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidBijective() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setBijective(Sets.newHashSet(resource2));
        resource2.setBijective(Sets.newHashSet(resource1));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidBijective() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestSpecialResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource1.setBijective(Sets.newHashSet(resource1, resource2));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        resource1 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);

        resource1.setBijective(Sets.newHashSet(resource2));
        resource3.setBijective(Sets.newHashSet(resource2));

        // Commit should fail:
        exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidTransitive() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n1"));
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n2"));
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n3"));
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n4"));
        resource1.setTransitive(Sets.newHashSet(resource2, resource3, resource4));
        resource2.setTransitive(Sets.newHashSet(resource3));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidTransitive() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n1"));
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n2"));
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n3"));
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestResource.class, (Resource) new URIImpl("urn:test:n4"));
        resource1.setTransitive(Sets.newHashSet(resource2, resource4));
        resource2.setTransitive(Sets.newHashSet(resource3));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidSymmetric() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSymmetric(Sets.newHashSet(resource2));
        resource2.setSymmetric(Sets.newHashSet(resource1));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidSymmetric() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSymmetric(Sets.newHashSet(resource2));
        resource2.setSymmetric(Sets.<ValidatedTransactionTestResource>newHashSet());

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidInverseOf() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setInverseOf1(Sets.newHashSet(resource2));
        resource2.setInverseOf2(Sets.newHashSet(resource1));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidInverseOf() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setInverseOf1(Sets.newHashSet(resource2));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidSubPropertyOf() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSuperproperty1(Sets.newHashSet(1, 2, 3));
        resource1.setSuperproperty2(Sets.newHashSet(1, 2));
        resource1.setSubpropertyOf(Sets.newHashSet(1, 2));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidSubPropertyOf() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSuperproperty1(Sets.newHashSet(1, 2, 3));
        resource1.setSuperproperty2(Sets.newHashSet(1));
        resource1.setSubpropertyOf(Sets.newHashSet(1, 2));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidMinCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setCardinality(Sets.newHashSet(1, 2));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidMinCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setCardinality(Sets.newHashSet(1));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidMaxCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setCardinality(Sets.newHashSet(1, 2, 3));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidMaxCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setCardinality(Sets.newHashSet(1, 2, 3, 4));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidQualifiedMinCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidQualifiedMinCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidQualifiedMaxCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource5 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4, resource5));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidQualifiedMaxCardinality() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource5 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4, resource5));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidAllValuesFrom() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        resource1.setAllValuesFrom(Sets.newHashSet(resource2, resource3, resource4));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidAllValuesFrom() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource5 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setAllValuesFrom(Sets.newHashSet(resource2, resource3, resource4, resource5));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testValidSomeValuesFrom() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestSpecialResource.class);
        ValidatedTransactionTestResource resource4 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSomeValuesFrom(Sets.newHashSet(resource3, resource2, resource4));

        // Commit should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
    }

    @Test
    public void testInvalidSomeValuesFrom() throws Exception {
        Anno4j anno4j = new Anno4j();
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();

        ValidatedTransactionTestResource resource1 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource2 = transaction.createObject(ValidatedTransactionTestResource.class);
        ValidatedTransactionTestResource resource3 = transaction.createObject(ValidatedTransactionTestResource.class);
        resource1.setSomeValuesFrom(Sets.newHashSet(resource2, resource3));

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}