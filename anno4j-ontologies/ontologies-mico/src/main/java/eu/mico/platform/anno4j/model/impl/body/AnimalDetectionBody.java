package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.ANIMAL_DETECTION_BODY)
public interface AnimalDetectionBody extends Body {

    @Iri(MICO.HAS_CONFIDENCE)
    void setConfidence(Double confidence);

    @Iri(MICO.HAS_CONFIDENCE)
    String getConfidence();

    @Iri(RDF.VALUE)
    void setValue(String value);

    @Iri(RDF.VALUE)
    String getValue();

    @Iri(MICO.HAS_EXTRACTION_VERSION)
    void setExtractionVersion(String extractionVersion);

    @Iri(MICO.HAS_EXTRACTION_VERSION)
    String getExtractionVersion();
}
