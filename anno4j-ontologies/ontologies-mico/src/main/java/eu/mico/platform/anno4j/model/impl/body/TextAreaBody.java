package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.*;
import org.openrdf.annotations.Iri;

@Iri(DCTYPES.TEXT)
public interface TextAreaBody extends Body {

    @Iri(RDF.VALUE)
    String getType();

    @Iri(RDF.VALUE)
    void setType(String type);

    @Iri(DC.FORMAT)
    String getFormat();

    @Iri(DC.FORMAT)
    void setFormat(String format);

    @Iri(DC.LANGUAGE)
    String getLanguage();

    @Iri(DC.LANGUAGE)
    void setLanguage();
}
