package eu.mico.platform.anno4j.model.impl.body;


import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

/**
 * Body-implementation for the Audio-Video-Quality extractor.
 * Has two subclasses {@link AVQShotBody} and {@link AVQKeyFrameBody}.
 */
@Iri(MICO.AVQ_BODY)
public class AVQBody extends Body {

    /**
     * Confidence value for the detected shot/keyframe
     */
    @Iri(MICO.HAS_CONFIDENCE)
    private Double confidence;

    public AVQBody() {
    }

    public AVQBody(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Sets new Confidence value for the detected shotkeyframe.
     *
     * @param confidence New value of Confidence value for the detected shotkeyframe.
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Gets Confidence value for the detected shotkeyframe.
     *
     * @return Value of Confidence value for the detected shotkeyframe.
     */
    public Double getConfidence() {
        return confidence;
    }
}
