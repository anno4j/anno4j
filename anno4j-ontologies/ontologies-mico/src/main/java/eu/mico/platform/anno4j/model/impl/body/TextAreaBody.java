package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.CNT;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.DCTYPES;
import com.github.anno4j.model.namespaces.RDF;
import com.hp.hpl.jena.ontology.Ontology;
import org.openrdf.annotations.Iri;

@Iri(DCTYPES.TEXT)
public class TextAreaBody extends Body {

    @Iri(DCTERMS.FORMAT)
    private String format;
    
    @Iri(CNT.CHARS)
    private String value;
    
    @Iri(RDF.VALUE)
    private final String TYPE = CNT.CONTENT_AS_TEXT;

    public TextAreaBody() {
    }

    public TextAreaBody(String format, String value) {
        this.format = format;
        this.value = value;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
