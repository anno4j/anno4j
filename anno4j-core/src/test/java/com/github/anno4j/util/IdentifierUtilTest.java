package com.github.anno4j.util;

import com.github.anno4j.model.namespaces.FOAF;
import com.github.anno4j.model.namespaces.SKOS;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for the IdentifierUtil class.
 */
public class IdentifierUtilTest {

    @Test
    public void testTrimNamespace() {
        String skosNotation = IdentifierUtil.trimNamespace(SKOS.NOTATION);
        String foafPerson = IdentifierUtil.trimNamespace(FOAF.PERSON);
        String cidocEntity = IdentifierUtil.trimNamespace("http://www.cidoc-crm.org/cidoc-crm/E1_CRM_Entity");

        assertEquals("notation", skosNotation);
        assertEquals("Person", foafPerson);
        assertEquals("E1_CRM_Entity", cidocEntity);

        String noNamespace = "noNamespace";
        assertEquals("noNamespace", IdentifierUtil.trimNamespace(noNamespace));
    }

}