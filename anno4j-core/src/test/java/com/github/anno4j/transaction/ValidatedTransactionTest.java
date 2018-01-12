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
import org.openrdf.repository.object.ObjectConnection;

import java.util.List;
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
    public interface TestResource extends ResourceObject {

        @Functional
        @Iri("http://example.de/functional")
        Set<Integer> getFunctional();

        @InverseFunctional
        @Iri("http://example.de/inverse_functional")
        Set<Integer> getInverseFunctional();

        @Bijective
        @Iri("http://example.de/bijective")
        Set<TestResource> getBijective();

        @Transitive
        @Iri("http://example.de/transitive")
        Set<TestResource> getTransitive();

        @Symmetric
        @Iri("http://example.de/symmetric")
        Set<TestResource> getSymmetric();

        @InverseOf("http://example.de/inverseof_2")
        @Iri("http://example.de/inverseof_1")
        Set<TestResource> getInverseOf1();

        @Iri("http://example.de/inverseof_2")
        Set<TestResource> getInverseOf2();

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

        @MinCardinality(value = 1, onClass = SpecialTestResource.class)
        @MaxCardinality(value = 2, onClass = SpecialTestResource.class)
        @Iri("http://example.de/qualified_cardinality")
        Set<TestResource> getQualifiedCardinality();

        @AllValuesFrom({SpecialTestResource.class})
        @Iri("http://example.de/all_values_from")
        Set<TestResource> getAllValuesFrom();

        @SomeValuesFrom({SpecialTestResource.class})
        @Iri("http://example.de/some_values_from")
        Set<TestResource> getSomeValuesFrom();

        @Iri("http://example.de/functional")
        void setFunctional(Set<Integer> values);

        @Iri("http://example.de/functional")
        void setInverseFunctional(Set<Integer> values);

        @Iri("http://example.de/bijective")
        void setBijective(Set<TestResource> values);

        @Iri("http://example.de/transitive")
        void setTransitive(Set<TestResource> values);

        @Iri("http://example.de/symmetric")
        void setSymmetric(Set<TestResource> values);

        @Iri("http://example.de/inverseof_1")
        void setInverseOf1(Set<TestResource> values);

        @Iri("http://example.de/inverseof_2")
        void setInverseOf2(Set<TestResource> values);

        @Iri("http://example.de/superproperty1")
        void setSuperproperty1(Set<Integer> values);

        @Iri("http://example.de/superproperty2")
        void setSuperproperty2(Set<Integer> values);

        @Iri("http://example.de/sub_property")
        void setSubpropertyOf(Set<Integer> values);

        @Iri("http://example.de/cardinality")
        void setCardinality(Set<Integer> values);

        @Iri("http://example.de/qualified_cardinality")
        void setQualifiedCardinality(Set<TestResource> values);

        @Iri("http://example.de/all_values_from")
        void setAllValuesFrom(Set<TestResource> values);

        @Iri("http://example.de/some_values_from")
        void setSomeValuesFrom(Set<TestResource> values);
    }

    /**
     * Sets valid unqualified cardinality (see {@link TestResource#setCardinality(Set)}
     * and qualified cardinality (see {@link TestResource#setQualifiedCardinality(Set)}
     * for the given resource.
     * @param resources The resources to make valid.
     */
    private void setValidCardinalities(SpecialTestResource... resources) {
        for(SpecialTestResource resource : resources) {
            // Set valid unqualified cardinality:
            resource.setCardinality(Sets.newHashSet(1, 2));
            // Set valid qualified cardinality:
            resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));
        }
    }

    @Iri("http://example.de/validated_transaction_test_resource_child")
    public interface SpecialTestResource extends TestResource { }

    @Test
    public void testPersistence() throws Exception {
        Anno4j anno4j = new Anno4j();
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Case 1: Test committing of valid resource:
        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class, (Resource) new URIImpl("http://example.de/res1"));
        resource.setCardinality(Sets.newHashSet(1, 2, 3));
        resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));

        // Commit the transaction should succeed:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);

        // Test whether data was persisted:
        SpecialTestResource found = anno4j.findByID(SpecialTestResource.class, "http://example.de/res1");
        assertNotNull(found);
        connection.refresh(found); // Make sure current values are read
        assertEquals(Sets.newHashSet(1, 2, 3), found.getCardinality());
        assertEquals(Sets.<TestResource>newHashSet(resource), found.getQualifiedCardinality());


        // Case 2: Test committing invalid resource with rollback:
        transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        resource = transaction.findByID(SpecialTestResource.class, "http://example.de/res1"); // Find resource created above
        resource.setCardinality(Sets.newHashSet(1)); // Too few elements

        // Committing the transaction should fail:
        exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            // Rollback transaction. All changes should be undone:
            e.rollback();
        }

        // Test whether no data was persisted:
        found = anno4j.findByID(SpecialTestResource.class, "http://example.de/res1");
        assertNotNull(found);
        connection.refresh(found); // Make sure current values are read
        assertEquals(Sets.newHashSet(1, 2, 3), found.getCardinality());
        assertEquals(Sets.<TestResource>newHashSet(resource), found.getQualifiedCardinality());


        // Case 3: Test committing invalid resource with forced commit:
        transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        resource = transaction.findByID(SpecialTestResource.class, "http://example.de/res1"); // Find resource created above
        resource.setCardinality(Sets.newHashSet(1)); // Too few elements

        // Committing the transaction should fail:
        exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
            // Commit anyway. Invalid changes should be persisted:
            e.forceCommit();
        }

        assertTrue(exceptionThrown);
        // Test whether invalid data was persisted:
        found = anno4j.findByID(SpecialTestResource.class, "http://example.de/res1");
        assertNotNull(found);
        connection.refresh(found); // Make sure current values are read
        assertEquals(Sets.newHashSet(1), found.getCardinality());
        assertEquals(Sets.<TestResource>newHashSet(resource), found.getQualifiedCardinality());


        // Case 4: Test committing invalid resource and fixing them afterwards:
        transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        resource = transaction.findByID(SpecialTestResource.class, "http://example.de/res1"); // Find resource created above
        resource.setCardinality(Sets.newHashSet(1)); // Too few elements

        // Committing the transaction should fail:
        exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;

            // Now set correct cardinality and commit again (with validation):
            found = e.getTransaction().findByID(SpecialTestResource.class, "http://example.de/res1");
            found.setCardinality(Sets.newHashSet(1, 2, 3));
            e.commit();
        }

        assertTrue(exceptionThrown);
        // Test whether invalid data was persisted:
        found = anno4j.findByID(SpecialTestResource.class, "http://example.de/res1");
        assertNotNull(found);
        connection.refresh(found); // Make sure current values are read
        assertEquals(Sets.newHashSet(1, 2, 3), found.getCardinality());
        assertEquals(Sets.<TestResource>newHashSet(resource), found.getQualifiedCardinality());
    }

    @Test
    public void testContextPersistence() throws Exception {
        Anno4j anno4j = new Anno4j();

        // The contexts:
        URI context1 = new URIImpl("urn:test:context1");

        SpecialTestResource resource1 = anno4j.createObject(SpecialTestResource.class, context1, new URIImpl("urn:test:res1"));
        // Make resources valid:
        setValidCardinalities(resource1);

        // Modify the resources using a validated transaction:
        Transaction transaction = anno4j.createValidatedTransaction(context1);
        transaction.begin();
        List<SpecialTestResource> resources = transaction.findAll(SpecialTestResource.class);
        for(SpecialTestResource current : resources) {
            current.setSuperproperty1(Sets.newHashSet(1, 2, 3));
        }
        transaction.commit();

        // Refind the resources in their respective context:
        resources = anno4j.createQueryService(context1)
                          .addCriteria(".", "urn:test:res1")
                          .execute(SpecialTestResource.class);
        assertFalse(resources.isEmpty());
        resource1 = resources.get(0);

        // Check that the changes are present:
        assertEquals(Sets.newHashSet(1, 2, 3), resource1.getSuperproperty1());
    }

    @Test
    public void testIndirectModification() throws Exception {
        Anno4j anno4j = new Anno4j();

        URI resource1URI = new URIImpl("http://example.de/r1");
        TestResource resource1 = anno4j.createObject(TestResource.class, (Resource) resource1URI);
        TestResource resource2 = anno4j.createObject(TestResource.class);
        resource1.setTransitive(Sets.newHashSet(resource2));

        ValidatedTransaction transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        TestResource foundResource = transaction.findByID(TestResource.class, resource1URI);
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
        Anno4j anno4j1 = new Anno4j();

        // Insert contradictory minCardinality:
        Transaction setupTransaction = anno4j1.createTransaction();
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
            new Anno4j(anno4j1.getRepository());
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
        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setFunctional(Sets.newHashSet(1));

        // Make the resource valid:
        setValidCardinalities(resource);

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
        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setFunctional(Sets.newHashSet(1, 2));

        // Make the resource valid wrt. the other constraints:
        setValidCardinalities(resource);

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
        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        resource1.setInverseFunctional(Sets.newHashSet(1));
        resource2.setInverseFunctional(Sets.newHashSet(2, 3));
        resource3.setInverseFunctional(Sets.newHashSet(4));

        // Make the resources valid:
        // Unqualified cardinalities:
        setValidCardinalities(resource1, resource2, resource3);

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
        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        resource1.setInverseFunctional(Sets.newHashSet(1, 2));
        resource2.setInverseFunctional(Sets.newHashSet(2, 3));
        resource3.setInverseFunctional(Sets.newHashSet(4));

        // Make the resources valid wrt. the other constraints:
        setValidCardinalities(resource1, resource2, resource3);

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        resource1.setBijective(Sets.<TestResource>newHashSet(resource2));
        resource2.setBijective(Sets.<TestResource>newHashSet(resource1));

        // Make the resources valid:
        setValidCardinalities(resource1, resource2);

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        resource1.setBijective(Sets.<TestResource>newHashSet(resource1, resource2));

        // Make the resources valid wrt. the other constraints:
        setValidCardinalities(resource1, resource2, resource3);

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

        resource1 = transaction.createObject(SpecialTestResource.class);
        resource2 = transaction.createObject(SpecialTestResource.class);
        resource3 = transaction.createObject(SpecialTestResource.class);

        // Make the new resources valid again wrt. the other constraints:
        setValidCardinalities(resource1, resource2, resource3);

        resource1.setBijective(Sets.<TestResource>newHashSet(resource2));
        resource3.setBijective(Sets.<TestResource>newHashSet(resource2));

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource4 = transaction.createObject(SpecialTestResource.class);
        resource1.setTransitive(Sets.<TestResource>newHashSet(resource2, resource3, resource4));
        resource2.setTransitive(Sets.<TestResource>newHashSet(resource3));

        // Make the resources valid:
        setValidCardinalities(resource1, resource2, resource3, resource4);

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource4 = transaction.createObject(SpecialTestResource.class);
        resource1.setTransitive(Sets.<TestResource>newHashSet(resource2, resource4));
        resource2.setTransitive(Sets.<TestResource>newHashSet(resource3));

        // Make the resource valid wrt. the other constraints:
        setValidCardinalities(resource1, resource2, resource3, resource4);

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // Test invalidation through deletion:
        resource1 = anno4j.createObject(SpecialTestResource.class, (Resource) new URIImpl("urn:test:res1"));
        resource1.setTransitive(Sets.<TestResource>newHashSet(resource2, resource3, resource4)); // Make the resource valid

        transaction = anno4j.createValidatedTransaction();
        resource1 = transaction.findByID(SpecialTestResource.class, "urn:test:res1");
        resource1.setTransitive(Sets.<TestResource>newHashSet(resource2, resource4)); // Edge to resource3 now removed

        exceptionThrown = false;
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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        resource1.setSymmetric(Sets.<TestResource>newHashSet(resource2));
        resource2.setSymmetric(Sets.<TestResource>newHashSet(resource1));

        // Make the resources valid:
        setValidCardinalities(resource1, resource2);

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        resource1.setSymmetric(Sets.<TestResource>newHashSet(resource2));
        resource2.setSymmetric(Sets.<TestResource>newHashSet());

        // Make the resources valid wrt. the other constraints:
        setValidCardinalities(resource1, resource2);

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // Test invalidation through deletion:
        // Create valid resources:
        resource1 = anno4j.createObject(SpecialTestResource.class, (Resource) new URIImpl("urn:test:res1"));
        resource2 = anno4j.createObject(SpecialTestResource.class, (Resource) new URIImpl("urn:test:res2"));
        resource1.setSymmetric(Sets.<TestResource>newHashSet(resource2));
        resource2.setSymmetric(Sets.<TestResource>newHashSet(resource1));
        setValidCardinalities(resource1, resource2);

        // Remove edge from resource1 to resource2 via transaction:
        transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        resource1 = transaction.createQueryService()
                                .addCriteria(".", "urn:test:res1")
                                .execute(SpecialTestResource.class)
                                .get(0);
        resource1.setSymmetric(Sets.<TestResource>newHashSet());

        exceptionThrown = false;
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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        resource1.setInverseOf1(Sets.<TestResource>newHashSet(resource2));
        resource2.setInverseOf2(Sets.<TestResource>newHashSet(resource1));

        // Make the resources valid:
        setValidCardinalities(resource1, resource2);

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

        SpecialTestResource resource1 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        resource1.setInverseOf1(Sets.<TestResource>newHashSet(resource2));

        // Make the resources valid wrt. the other constraints:
        setValidCardinalities(resource1, resource2);

        // Commit should fail:
        boolean exceptionThrown = false;
        try {
            transaction.commit();
        } catch (ValidatedTransaction.ValidationFailedException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // Test invalidation through deletion:
        // Create valid resources:
        resource1 = anno4j.createObject(SpecialTestResource.class, (Resource) new URIImpl("urn:test:res1"));
        resource2 = anno4j.createObject(SpecialTestResource.class, (Resource) new URIImpl("urn:test:res1"));
        resource1.setInverseOf1(Sets.<TestResource>newHashSet(resource2));
        resource2.setInverseOf2(Sets.<TestResource>newHashSet(resource1));
        setValidCardinalities(resource1, resource2);

        // Remove edge from resource1 to resource2 via transaction:
        transaction = anno4j.createValidatedTransaction();
        transaction.begin();
        resource1 = transaction.createQueryService()
                               .addCriteria(".", "urn:test:res1")
                                .execute(SpecialTestResource.class)
                                .get(0);
        resource1.setInverseOf1(Sets.<TestResource>newHashSet());

        exceptionThrown = false;
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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setSuperproperty1(Sets.newHashSet(1, 2, 3));
        resource.setSuperproperty2(Sets.newHashSet(1, 2));
        resource.setSubpropertyOf(Sets.newHashSet(1, 2));

        // Make the resource valid:
        setValidCardinalities(resource);

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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setSuperproperty1(Sets.newHashSet(1, 2, 3));
        resource.setSuperproperty2(Sets.newHashSet(1));
        resource.setSubpropertyOf(Sets.newHashSet(1, 2));

        // Make the resource valid wrt. the other constraints:
        setValidCardinalities(resource);

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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setCardinality(Sets.newHashSet(1, 2));

        // Set valid qualified cardinality:
        resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));

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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setCardinality(Sets.newHashSet(1));

        // Set valid qualified cardinality:
        resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));

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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setCardinality(Sets.newHashSet(1, 2, 3));

        // Set valid qualified cardinality:
        resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));

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

        SpecialTestResource resource = transaction.createObject(SpecialTestResource.class);
        resource.setCardinality(Sets.newHashSet(1, 2, 3, 4));

        // Set valid qualified cardinality:
        resource.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource));

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(SpecialTestResource.class);
        TestResource resource3 = transaction.createObject(SpecialTestResource.class);
        TestResource resource4 = transaction.createObject(TestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));
        resource2.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));
        resource3.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));
        resource4.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));

        // Set valid (unqualified) cardinalities:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource3.setCardinality(Sets.newHashSet(1, 2));
        resource4.setCardinality(Sets.newHashSet(1, 2));

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(SpecialTestResource.class);
        TestResource resource3 = transaction.createObject(TestResource.class);
        TestResource resource4 = transaction.createObject(TestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource3, resource4));
        resource2.setQualifiedCardinality(Sets.newHashSet(resource2));
        resource3.setQualifiedCardinality(Sets.newHashSet(resource2));
        resource4.setQualifiedCardinality(Sets.newHashSet(resource2));

        // Set valid (unqualified) cardinalities:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource3.setCardinality(Sets.newHashSet(1, 2));
        resource4.setCardinality(Sets.newHashSet(1, 2));

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(SpecialTestResource.class);
        TestResource resource3 = transaction.createObject(SpecialTestResource.class);
        TestResource resource4 = transaction.createObject(TestResource.class);
        TestResource resource5 = transaction.createObject(TestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource5));
        resource2.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource5));
        resource3.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource5));
        resource4.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource5));
        resource5.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource5));

        // Set valid (unqualified) cardinalities:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource3.setCardinality(Sets.newHashSet(1, 2));
        resource4.setCardinality(Sets.newHashSet(1, 2));
        resource5.setCardinality(Sets.newHashSet(1, 2));

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(SpecialTestResource.class);
        TestResource resource3 = transaction.createObject(SpecialTestResource.class);
        TestResource resource4 = transaction.createObject(SpecialTestResource.class);
        TestResource resource5 = transaction.createObject(SpecialTestResource.class);
        resource1.setQualifiedCardinality(Sets.newHashSet(resource2, resource3, resource4));
        resource2.setQualifiedCardinality(Sets.newHashSet(resource2, resource3));
        resource3.setQualifiedCardinality(Sets.newHashSet(resource2, resource3));
        resource4.setQualifiedCardinality(Sets.newHashSet(resource2, resource3));
        resource5.setQualifiedCardinality(Sets.newHashSet(resource2, resource3));

        // Set valid (unqualified) cardinalities:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource3.setCardinality(Sets.newHashSet(1, 2));
        resource4.setCardinality(Sets.newHashSet(1, 2));
        resource5.setCardinality(Sets.newHashSet(1, 2));

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource4 = transaction.createObject(SpecialTestResource.class);
        resource1.setAllValuesFrom(Sets.<TestResource>newHashSet(resource2, resource3, resource4));

        // Make resources valid:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource1.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource2));
        setValidCardinalities(resource2, resource3, resource4);

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        SpecialTestResource resource2 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        SpecialTestResource resource4 = transaction.createObject(SpecialTestResource.class);
        TestResource resource5 = transaction.createObject(TestResource.class);
        resource1.setAllValuesFrom(Sets.<TestResource>newHashSet(resource2, resource3, resource4, resource5));

        // Make resources valid wrt. the other constraints:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource5.setCardinality(Sets.newHashSet(1, 2));
        resource1.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource2));
        resource5.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource2));
        setValidCardinalities(resource2, resource3, resource4);

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(TestResource.class);
        SpecialTestResource resource3 = transaction.createObject(SpecialTestResource.class);
        TestResource resource4 = transaction.createObject(TestResource.class);
        resource1.setSomeValuesFrom(Sets.newHashSet(resource3, resource2, resource4));

        // Make resources valid:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource4.setCardinality(Sets.newHashSet(1, 2));
        resource1.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource3));
        resource2.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource3));
        resource4.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource3));
        setValidCardinalities(resource3);

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

        TestResource resource1 = transaction.createObject(TestResource.class);
        TestResource resource2 = transaction.createObject(TestResource.class);
        TestResource resource3 = transaction.createObject(TestResource.class);
        SpecialTestResource resource4 = transaction.createObject(SpecialTestResource.class);
        resource1.setSomeValuesFrom(Sets.newHashSet(resource2, resource3));

        // Make resources valid wrt. the other constraints:
        resource1.setCardinality(Sets.newHashSet(1, 2));
        resource2.setCardinality(Sets.newHashSet(1, 2));
        resource3.setCardinality(Sets.newHashSet(1, 2));
        resource1.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource4));
        resource2.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource4));
        resource3.setQualifiedCardinality(Sets.<TestResource>newHashSet(resource4));
        setValidCardinalities(resource4);

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