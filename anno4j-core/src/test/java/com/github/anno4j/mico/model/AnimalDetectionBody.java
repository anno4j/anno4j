package com.github.anno4j.mico.model;

import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.mico.namespace.MMMTERMS;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

/**
 * Exemplary Body class implemented for deliverable testing purposes.
 */
@Iri(MMMTERMS.ANIMAL_DETECTION_BODY)
public interface AnimalDetectionBody extends BodyMMM {

    @Iri(MMM.HAS_CONFIDENCE)
    void setConfidence(Double confidence);

    @Iri(MMM.HAS_CONFIDENCE)
    Double getConfidence();

    @Iri(RDF.VALUE)
    void setAnimal(String value);

    @Iri(RDF.VALUE)
    String getAnimal();
}
