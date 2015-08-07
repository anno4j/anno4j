package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.MULTIMEDIA_BODY)
public class MultiMediaBody extends Body {

    @Iri(DCTERMS.FORMAT)
    private String format;

    public MultiMediaBody() {
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
