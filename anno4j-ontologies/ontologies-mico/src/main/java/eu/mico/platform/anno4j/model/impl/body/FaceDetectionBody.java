package eu.mico.platform.anno4j.model.impl.body;


import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.FACE_DETECTION_BODY)
public interface FaceDetectionBody extends Body {


    @Iri(MICO.HAS_CONFIDENCE)
    Double getConfidence();

    @Iri(MICO.HAS_CONFIDENCE)
    void setConfidence(Double confidence);
}
