package com.github.anno4j.rdfs_parser.building;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.LangString;

import static org.junit.Assert.*;

/**
 * Test for the {@link OntGenerationConfig} class.
 */
public class OntGenerationConfigTest {

    private static OntGenerationConfig config;

    @Before
    public void setUp() throws Exception {
        config = new OntGenerationConfig();
        String[] identifierLangPref = {"en", OntGenerationConfig.UNTYPED_LITERAL, "it"};
        String[] javaDocLangPref = {"de", "en", OntGenerationConfig.UNTYPED_LITERAL};
        config.setIdentifierLanguagePreference(identifierLangPref);
        config.setJavaDocLanguagePreference(javaDocLangPref);
    }

    @Test
    public void isIdentifierLanguagePreferred() throws Exception {
        assertTrue(config.isIdentifierLanguagePreferred("en", "it"));
        assertTrue(config.isIdentifierLanguagePreferred(OntGenerationConfig.UNTYPED_LITERAL, "it"));
        assertFalse(config.isIdentifierLanguagePreferred("it", OntGenerationConfig.UNTYPED_LITERAL));
    }

    @Test
    public void isPreferredForIdentifiers() throws Exception {
        LangString english = new LangString("This is a test.", "en");
        String untyped = "This is a test.";
        LangString italian = new LangString("Questo è un test", "it");
        LangString french = new LangString("Ceci est un test", "fr");

        assertTrue(config.isPreferredForIdentifiers(english, italian));
        assertTrue(config.isPreferredForIdentifiers(untyped, italian));
        assertFalse(config.isPreferredForIdentifiers(italian, english));
        assertTrue(config.isPreferredForJavaDoc(english, null));
        assertFalse(config.isPreferredForJavaDoc(french, null));
    }

    @Test
    public void isJavaDocLanguagePreferred() throws Exception {
        assertTrue(config.isJavaDocLanguagePreferred("de", "en"));
        assertTrue(config.isJavaDocLanguagePreferred("de", OntGenerationConfig.UNTYPED_LITERAL));
        assertFalse(config.isJavaDocLanguagePreferred(OntGenerationConfig.UNTYPED_LITERAL, "en"));
    }

    @Test
    public void isPreferredForJavaDoc() throws Exception {
        LangString english = new LangString("This is a test.", "en");
        String untyped = "This is a test.";
        LangString german = new LangString("Das ist ein Test.", "de");
        LangString french = new LangString("Ceci est un test", "fr");

        assertTrue(config.isPreferredForJavaDoc(german, english));
        assertTrue(config.isPreferredForJavaDoc(german, untyped));
        assertFalse(config.isPreferredForJavaDoc(untyped, english));
        assertTrue(config.isPreferredForJavaDoc(english, null));
        assertFalse(config.isPreferredForJavaDoc(french, null));
    }

}