package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.ANIMAL_DETECTION_BODY)
public interface AnimalDetectionBody extends MicoBody {

    @Iri(MMM.HAS_CONFIDENCE)
    void setConfidence(Double confidence);

    @Iri(MMM.HAS_CONFIDENCE)
    String getConfidence();

    @Iri(RDF.VALUE)
    void setValue(String value);

    @Iri(RDF.VALUE)
    String getValue();

    @Iri(MMM.HAS_EXTRACTION_VERSION)
    void setExtractionVersion(String extractionVersion);

    @Iri(MMM.HAS_EXTRACTION_VERSION)
    String getExtractionVersion();
}
