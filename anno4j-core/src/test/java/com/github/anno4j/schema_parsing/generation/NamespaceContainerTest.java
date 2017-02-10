package com.github.anno4j.schema_parsing.generation;

import com.squareup.javapoet.JavaFile;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the NamespaceContainer class.
 */
public class NamespaceContainerTest {

    private final static String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";
    private final static String CIDOC_PREFIX = "cidoc";

    private final static String ENTITY_URI = CIDOC_NS + "E1_CRM_Entity";
    private final static String TEMPORAL_ENTITY_URI = CIDOC_NS + "E2_Temporal_Entity";

    private final static String WAS_MOTIVATED_BY_URI = CIDOC_NS + "P17_was_motivated_by";
    private final static String WAS_INFLUENCED_BY_URI = CIDOC_NS + "P15_was_influenced_by";

    private final static String OUTPUT = "package com.github.anno4j.schema_parsing.generation;\n\n" +

    "import java.lang.String;\n\n" +

    "public class CIDOC {\n" +
    "  public static final String NS = \"http://www.cidoc-crm.org/cidoc-crm/\";\n\n" +

    "  public static final String PREFIX = \"cidoc\";\n\n" +

    "  public static final String E1_CRM_ENTITY = NS + \"E1_CRM_Entity\";\n\n" +

    "  public static final String E2_TEMPORAL_ENTITY = NS + \"E2_Temporal_Entity\";\n\n" +

    "  public static final String P15_WAS_INFLUENCED_BY = NS + \"P15_was_influenced_by\";\n\n" +

    "  public static final String P17_WAS_MOTIVATED_BY = NS + \"P17_was_motivated_by\";\n" +
    "}\n";

    @Test
    public void testNameSpaceContainer() throws IOException {
        NamespaceContainer nc = new NamespaceContainer(CIDOC_NS, CIDOC_PREFIX, "com.github.anno4j.schema_parsing.generation");

        nc.addClazz(ENTITY_URI);
        nc.addClazz(TEMPORAL_ENTITY_URI);

        nc.addProperty(WAS_INFLUENCED_BY_URI);
        nc.addProperty(WAS_MOTIVATED_BY_URI);

        JavaFile javaFile = nc.generate();

        assertEquals(OUTPUT, javaFile.toString());
    }
}