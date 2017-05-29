package com.github.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to http://www.w3.org/ns/oa#TextualBody.
 */
@Iri(OADM.TEXTUAL_BODY)
public interface TextualBody extends Body {

    @Iri(RDF.VALUE)
    void setValue(String value);

    @Iri(RDF.VALUE)
    String getValue();

    @Iri(OADM.HAS_PURPOSE)
    void setPurposes(Set<Motivation> purposes);

    @Iri(OADM.HAS_PURPOSE)
    Set<Motivation> getPurposes();

    void addPurpose(Motivation purpose);
}
