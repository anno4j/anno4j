package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.model.namespaces.FOAF;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the identifier builder, which constructs Java compliant names for
 * resources.
 */
public class IdentifierBuilderTest {

    @Test
    public void testPackageName() throws Exception {
        assertEquals("com.xmlns", IdentifierBuilder.builder(FOAF.HOMEPAGE)
                                                            .packageName());

        assertEquals("de.uni_passau.fim", IdentifierBuilder.builder("http://fim.uni-passau.de/uri")
                                                                    .packageName());

        assertEquals("over.its.finally_", IdentifierBuilder.builder("http://finally.its.over/uri")
                                                                    .packageName());

        assertEquals("", IdentifierBuilder.builder("http://127.0.0.1/uri")
                                                   .packageName());
    }

    @Test
    public void testLowercaseIdentifier() throws Exception {
        assertEquals("mboxSha1sum", IdentifierBuilder.builder(FOAF.MBOX_SHA1SUM)
                                                              .lowercaseIdentifier());

        assertEquals("person", IdentifierBuilder.builder(FOAF.PERSON).lowercaseIdentifier());

        assertEquals("aLivingPerson", IdentifierBuilder.builder(FOAF.PERSON)
                                                                .withRDFSLabel("A living person")
                                                                .lowercaseIdentifier());

        assertEquals("someblanknode", IdentifierBuilder.builder("someblanknode")
                                                                .lowercaseIdentifier());
    }

    @Test
    public void testCapitalizedIdentifier() throws Exception {
        assertEquals("MboxSha1sum", IdentifierBuilder.builder(FOAF.MBOX_SHA1SUM)
                                                              .capitalizedIdentifier());

        assertEquals("Person", IdentifierBuilder.builder(FOAF.PERSON)
                                                         .capitalizedIdentifier());

        assertEquals("ALivingPerson", IdentifierBuilder.builder(FOAF.PERSON)
                                                                .withRDFSLabel("A living person")
                                                                .capitalizedIdentifier());

        assertEquals("Someblanknode", IdentifierBuilder.builder("someblanknode")
                                                                .capitalizedIdentifier());
    }
}