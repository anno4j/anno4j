package eu.mico.platform.anno4j.model.impl.body;

import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.TVS_BODY)
public interface TVSBody extends MicoBody {


    /**
     * Gets Confidence value for the detected shotkeyframe.
     *
     * @return Value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MMM.HAS_CONFIDENCE)
    Double getConfidence();

    /**
     * Sets new Confidence value for the detected shotkeyframe.
     *
     * @param confidence New value of Confidence value for the detected shotkeyframe.
     */
    @Iri(MMM.HAS_CONFIDENCE)
    void setConfidence(Double confidence);
}
