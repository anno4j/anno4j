package com.github.anno4j.mico.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.annotations.Iri;

/**
 * Support class for the AnimalDetectionBody.
 */
@Partial
public abstract class AnimalDetectionBodySupport extends ResourceObjectSupport implements AnimalDetectionBody {

    @Iri(MMM.HAS_CONFIDENCE)
    private Double confidence;

    @Override
    public void setConfidence(Double confidence) {
        if(confidence < 0) {
            this.confidence = 0.0;
        } else if(confidence > 1) {
            this.confidence = 1.0;
        } else {
            this.confidence = confidence;
        }
    }

    @Override
    public Double getConfidence() {
        return this.confidence;
    }
}
