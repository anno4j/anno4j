package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.FACE_RECOGNITION_BODY)
public class FaceRecognitionBody extends Body {

    /**
     * The name of the person that was detected
     */
    @Iri(RDF.VALUE)
    private String detection;

    /**
     * Confidence value for the detected face
     */
    @Iri(MICO.HAS_CONFIDENCE)
    private Double confidence;

    public FaceRecognitionBody() {
    }

    public FaceRecognitionBody(String detection, Double confidence) {
        this.detection = detection;
        this.confidence = confidence;
    }

    public String getDetection() {
        return detection;
    }

    public void setDetection(String detection) {
        this.detection = detection;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
