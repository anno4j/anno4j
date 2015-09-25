package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.ANIMAL_DETECTION_BODY)
public class AnimalDetectionBody extends Body {

    /**
     * Confidence value for the detected animal
     */
    @Iri(MICO.HAS_CONFIDENCE)
    private Double confidence;

    /**
     * Type of the animal
     */
    @Iri(RDF.VALUE)
    private String value;

    /**
     * Version of the executed extractor
     */
    @Iri(MICO.HAS_EXTRACTION_VERSION)
    private String extractionVersion;

    public AnimalDetectionBody() {
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtractionVersion() {
        return extractionVersion;
    }

    public void setExtractionVersion(String extractionVersion) {
        this.extractionVersion = extractionVersion;
    }
}
