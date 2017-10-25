package com.github.anno4j.schema;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.SanitizingTestResource;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SchemaSanitizingObjectSupport}.
 */
public class SchemaSanitizingObjectSupportTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
    }

    @Test
    public void testSuperpropertySanitizing() throws Exception {
        // Setup the required resource:
        SanitizingTestResource resource = anno4j.createObject(SanitizingTestResource.class);
        resource.setSuperproperty(Sets.newHashSet(1));
        resource.setSubproperty(Sets.newHashSet(2));

        // Check that the values for the subproperty are also set for the superproperty:
        assertEquals(Sets.newHashSet(1, 2), resource.getSuperproperty());

        resource.setSuperproperty(Sets.newHashSet(3));

        // Now the superproperty should have only the new value and the subproperty should be cleared (no intersection with old values):
        assertEquals(Sets.newHashSet(3), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(), resource.getSubproperty());

        // Check that subproperty keeps values that are also present in updated superproperty:
        resource.setSubproperty(Sets.newHashSet(4));
        assertEquals(Sets.newHashSet(3, 4), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(4), resource.getSubproperty());

        resource.setSuperproperty(Sets.newHashSet(4, 5));
        assertEquals(Sets.newHashSet(4, 5), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(4), resource.getSubproperty());

        // Test setting empty set to superproperty:
        resource.setSuperproperty(Sets.<Integer>newHashSet());
        assertEquals(Sets.<Integer>newHashSet(), resource.getSuperproperty());
        assertEquals(Sets.<Integer>newHashSet(), resource.getSubproperty());
    }

    @Test
    public void testEquivalenceSubpropertySanitizing() throws Exception {
        // Prepare the resources used in the test:
        SanitizingTestResource resource = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource x1 = anno4j.createObject(SanitizingTestResource.class, (Resource) new URIImpl("urn:anno4j:x1"));
        SanitizingTestResource x2 = anno4j.createObject(SanitizingTestResource.class, (Resource) new URIImpl("urn:anno4j:x2"));

        // Declare x1,x2 equivalent:
        anno4j.getObjectRepository().getConnection().prepareUpdate(QueryLanguage.SPARQL,
                "INSERT DATA {" +
                        "   <" + x1 + "> owl:sameAs <" + x2 + "> . " +
                        "}"
        ).execute();

        // Setup values:
        resource.setObjectSubproperty(Sets.newHashSet(x2));
        assertEquals(Sets.newHashSet(x2), resource.getObjectSuperproperty());
        assertEquals(Sets.newHashSet(x2), resource.getObjectSubproperty());

        // Setting superproperty to equivalent value:
        resource.setObjectSuperproperty(Sets.newHashSet(x1));
        assertEquals(Sets.newHashSet(x1), resource.getObjectSuperproperty());
        assertEquals(Sets.newHashSet(x2), resource.getObjectSubproperty());
    }

    @Test
    public void testRemoveValue() throws Exception {
        SanitizingTestResource resource = anno4j.createObject(SanitizingTestResource.class, (Resource) new URIImpl("urn:anno4j:r"));
        SanitizingTestResource x1 = anno4j.createObject(SanitizingTestResource.class, (Resource) new URIImpl("urn:anno4j:x1"));
        SanitizingTestResource x2 = anno4j.createObject(SanitizingTestResource.class, (Resource) new URIImpl("urn:anno4j:x2"));

        // Setup values:
        resource.setSuperproperty(Sets.newHashSet(1));
        resource.setSubproperty(Sets.newHashSet(2));
        resource.setObjectSuperproperty(Sets.newHashSet(x1));
        resource.setObjectSubproperty(Sets.newHashSet(x2));

        // Actual test happens here:
        resource.testRemoveValue(x2);
    }

    @Test
    public void testSymmetrySanitizing() throws Exception {
        // Setup required resources:
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);

        // Set one side of the symmetric relation:
        resource1.setSymmetric(Sets.newHashSet(resource2));
        resource1.sanitizeSchema("urn:anno4j_test:sanitizing_transitive");

        // Check other side is inserted:
        assertEquals(Sets.newHashSet(resource2), resource1.getSymmetric());
        assertEquals(Sets.newHashSet(resource1), resource2.getSymmetric());
    }

    @Test
    public void testTransitivitySanitizing() throws Exception {
        // Setup required resources:
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource3 = anno4j.createObject(SanitizingTestResource.class);

        // Important: Must be in this order because sanitizing is local by specification:
        resource2.setTransitive(Sets.newHashSet(resource3));
        resource1.setTransitive(Sets.newHashSet(resource2));
        resource1.sanitizeSchema("urn:anno4j_test:sanitizing_transitive");

        // Check that transitive edges are inserted:
        assertEquals(Sets.newHashSet(resource2, resource3), resource1.getTransitive());
    }

    @Test
    public void testInversePropertySanitizing() throws Exception {
        // Setup required resources:
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);

        // Set a value for a property:
        resource1.setInverse1(Sets.newHashSet(resource2));

        // Check its inverse is also set:
        assertTrue(resource2.getInverse2().contains(resource1));
    }
}