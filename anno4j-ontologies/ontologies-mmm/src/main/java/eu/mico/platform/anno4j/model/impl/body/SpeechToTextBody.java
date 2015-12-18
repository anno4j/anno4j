package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

/**
 * Class represents the body for a SpeechToText annotation. The relevant timestamp information is stored in the
 * respective selector. The body itself contains which word has been detected.
 */
@Iri(MMM.STT_BODY_MICO)
public interface SpeechToTextBody extends MicoBody {

    @Iri(RDF.VALUE)
    LangString getValue();

    /**
     * The value of the body corresponds to the word that is detected.
     */
    @Iri(RDF.VALUE)
    void setValue(LangString value);

}