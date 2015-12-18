package eu.mico.platform.anno4j.model.impl.body;

import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.FACE_DETECTION_BODY)
public interface FaceDetectionBody extends MicoBody {


    @Iri(MMM.HAS_CONFIDENCE)
    Double getConfidence();

    @Iri(MMM.HAS_CONFIDENCE)
    void setConfidence(Double confidence);
}
