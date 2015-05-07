package com.github.anno4j.example;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.ontologies.DC;
import com.github.anno4j.model.ontologies.RDF;
import org.openrdf.annotations.Iri;

/**
 * An exemplary body entity used in the ExampleTest.
 *
 * @see com.github.anno4j.example.ExampleTest
 */
@Iri("http://www.w3.org/ns/oa#EmbeddedContent")
public class TextAnnotationBody extends Body {

    @Iri(DC.FORMAT)   private String format;
    @Iri(RDF.VALUE)   private String value;
    @Iri(DC.LANGUAGE) private String language;

    public TextAnnotationBody() {};

    public TextAnnotationBody(String format, String value, String language) {
        this.format = format;
        this.value = value;
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
