package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.FOAF;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.LangString;

import static org.junit.Assert.assertEquals;

/**
 * Test for the identifier builder, which constructs Java compliant names for
 * resources.
 */
public class IdentifierBuilderTest {

    private Anno4j anno4j;

    private OntGenerationConfig config;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        config = new OntGenerationConfig();
        config.setIdentifierLanguagePreference(new String[]{"de", OntGenerationConfig.UNTYPED_LITERAL});
    }

    @Test
    public void testPackageName() throws Exception {
        IdentifierBuilder builder = IdentifierBuilder.forObjectRepository(anno4j.getObjectRepository());
        assertEquals("com.xmlns", builder.packageName(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.HOMEPAGE))));

        assertEquals("de.uni_passau.fim", builder.packageName(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://fim.uni-passau.de/uri"))));

        assertEquals("over.its.finally_", builder.packageName(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://finally.its.over/uri"))));

        assertEquals("", builder.packageName(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://127.0.0.1/uri"))));

        assertEquals("", builder.packageName(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("urn:isbn:3827370191"))));
    }

    @Test
    public void testLowercaseIdentifier() throws Exception {
        IdentifierBuilder builder = IdentifierBuilder.forObjectRepository(anno4j.getObjectRepository());
        assertEquals("mboxSha1sum", builder.lowercaseIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.MBOX_SHA1SUM)), config));

        assertEquals("person", builder.lowercaseIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.PERSON)), config));

        RDFSClazz person = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.PERSON));
        person.setLabels(Sets.<CharSequence>newHashSet(new LangString("A living person", "de")));
        assertEquals("aLivingPerson", builder.lowercaseIdentifier(person, config));

        assertEquals("someClazz", builder.lowercaseIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("urn:test:some_clazz")), config));
    }

    @Test
    public void testCapitalizedIdentifier() throws Exception {
        IdentifierBuilder builder = IdentifierBuilder.forObjectRepository(anno4j.getObjectRepository());
        assertEquals("MboxSha1sum", builder.capitalizedIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.MBOX_SHA1SUM)), config));

        assertEquals("Person", builder.capitalizedIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.PERSON)), config));

        RDFSClazz person = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(FOAF.PERSON));
        person.setLabels(Sets.<CharSequence>newHashSet(new LangString("A living person", "de")));
        assertEquals("ALivingPerson", builder.capitalizedIdentifier(person, config));

        assertEquals("SomeClazz", builder.capitalizedIdentifier(anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("urn:test:some_clazz")), config));
    }
}