package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.ANIMAL_DETECTION_BODY)
public class AnimalDetectionBody extends Body {

    /**
     * Confidence value for the detected animal
     */
    @Iri(MICO.HAS_CONFIDENCE)
    private Double confidence;

    public AnimalDetectionBody() {
    }

    public AnimalDetectionBody(Double confidence) {
        this.confidence = confidence;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
