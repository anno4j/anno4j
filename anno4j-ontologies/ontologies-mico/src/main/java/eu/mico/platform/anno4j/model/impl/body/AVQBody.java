package eu.mico.platform.anno4j.model.impl.body;


import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

/**
 * Body-implementation for the Audio-Video-Quality extractor.
 * Has two subclasses {@link AVQShotBody} and {@link AVQKeyFrameBody}.
 */
@Iri(MICO.AVQ_BODY)
public interface AVQBody extends Body {

    /**
     * Sets new Confidence value for the detected shotkeyframe.
     *
     * @param confidence New value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MICO.HAS_CONFIDENCE)
    void setConfidence(Double confidence);

    /**
     * Gets Confidence value for the detected shotkeyframe.
     *
     * @return Value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MICO.HAS_CONFIDENCE)
    Double getConfidence();
}
