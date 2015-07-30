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
public class TextAnnotationBody extends Body {

    /**
     * The format that the given text annotation is supported in.
     */
    @Iri(DC.FORMAT)   private String format;

    /**
     * The actual textual content.
     */
    @Iri(RDF.VALUE)   private String value;

    /**
     * The language of the supported text annotation.
     */
    @Iri(DC.LANGUAGE) private String language;

    /**
     * Standard constructor.
     */
    public TextAnnotationBody() {};

    /**
     * Constructor setting the format, value, and language members.
     * @param format    The format of the text annotation.
     * @param value     The actual text of the annotation.
     * @param language  The language of the text.
     */
    public TextAnnotationBody(String format, String value, String language) {
        this.format = format;
        this.value = value;
        this.language = language;
    }

    /**
     * Gets The format that the given text annotation is supported in..
     *
     * @return Value of The format that the given text annotation is supported in..
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets new The actual textual content..
     *
     * @param value New value of The actual textual content..
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets The language of the supported text annotation..
     *
     * @return Value of The language of the supported text annotation..
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets new The language of the supported text annotation..
     *
     * @param language New value of The language of the supported text annotation..
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Sets new The format that the given text annotation is supported in..
     *
     * @param format New value of The format that the given text annotation is supported in..
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Gets The actual textual content..
     *
     * @return Value of The actual textual content..
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TextAnnotationBody{" +
                "format='" + format + '\'' +
                ", value='" + value + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
