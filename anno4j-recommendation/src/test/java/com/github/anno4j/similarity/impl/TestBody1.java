package com.github.anno4j.similarity.impl;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

@Iri("http://somepage.com#TestBody1")
public interface TestBody1 extends Body {
    @Iri(RDF.VALUE)
    void setValue(String value);

    @Iri(RDF.VALUE)
    String getValue();
}