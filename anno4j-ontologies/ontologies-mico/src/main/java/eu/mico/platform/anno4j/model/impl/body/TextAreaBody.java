package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.CNT;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.DCTYPES;
import com.github.anno4j.model.namespaces.RDF;
import com.hp.hpl.jena.ontology.Ontology;
import org.openrdf.annotations.Iri;

@Iri(DCTYPES.TEXT)
public interface TextAreaBody extends Body {


    @Iri(RDF.VALUE)
    String getType();

    @Iri(RDF.VALUE)
    void setType(String type);

    @Iri(DCTERMS.FORMAT)
    String getFormat();

    @Iri(DCTERMS.FORMAT)
    void setFormat(String format);

    @Iri(CNT.CHARS)
    String getValue();

    @Iri(CNT.CHARS)
    void setValue(String value);
}
