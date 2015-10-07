package eu.mico.platform.anno4j.model.impl.body;


import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.TVS_BODY)
public interface TVSBody extends Body {


    /**
     * Gets Confidence value for the detected shotkeyframe.
     *
     * @return Value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MICO.HAS_CONFIDENCE)
    Double getConfidence();

    /**
     * Sets new Confidence value for the detected shotkeyframe.
     *
     * @param confidence New value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MICO.HAS_CONFIDENCE)
    void setConfidence(Double confidence);
}
