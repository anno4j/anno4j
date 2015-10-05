package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.RDF;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

/**
 * Class represents the body for a SpeechToText annotation. The relevant timestamp information is stored in the
 * respective selector. The body itself contains which word has been detected.
 */
@Iri(MICO.STT_BODY_MICO)
public class SpeechToTextBody extends Body {

    /**
     * The value of the body corresponds to the word that is detected.
     */
    @Iri(RDF.VALUE)
    private LangString value;

    /**
     * Default constructor.
     */
    public SpeechToTextBody() {};

    /**
     * Constructor also setting the value.
     * @param value The word that is detected.
     */
    public SpeechToTextBody(LangString value) {
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return Value of value.
     */
    public LangString getValue() {
        return value;
    }

    /**
     * Sets new value.
     *
     * @param value New value of value.
     */
    public void setValue(LangString value) {
        this.value = value;
    }

}