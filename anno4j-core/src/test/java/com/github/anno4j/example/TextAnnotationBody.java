package com.github.anno4j.example;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DC;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

/**
 * An exemplary body entity used in the ExampleTest.
 *
 * @see com.github.anno4j.example.ExampleTest
 */
@Iri("http://www.w3.org/ns/oa#EmbeddedContent")
public interface TextAnnotationBody extends Body {

    /**
     * Gets The format that the given text annotation is supported in..
     *
     * @return Value of The format that the given text annotation is supported in..
     */
    @Iri(DC.FORMAT)
    String getFormat();

    /**
     * Sets new The actual textual content..
     *
     * @param value New value of The actual textual content..
     */
    @Iri(RDF.VALUE)
    void setValue(String value);

    /**
     * Gets The language of the supported text annotation..
     *
     * @return Value of The language of the supported text annotation..
     */
    @Iri(DC.LANGUAGE)
    String getLanguage();

    /**
     * Sets new The language of the supported text annotation..
     *
     * @param language New value of The language of the supported text annotation..
     */
    @Iri(DC.LANGUAGE)
    void setLanguage(String language);

    /**
     * Sets new The format that the given text annotation is supported in..
     *
     * @param format New value of The format that the given text annotation is supported in..
     */
    @Iri(DC.FORMAT)
    void setFormat(String format);

    /**
     * Gets The actual textual content..
     *
     * @return Value of The actual textual content..
     */
    @Iri(RDF.VALUE)
    String getValue();
}
