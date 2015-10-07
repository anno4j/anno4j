package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.COLOR_LAYOUT_RAW_BODY)
public interface ColorLayoutRawBody extends Body {

    @Iri(MICO.HAS_LOCATION)
    String getLayoutLocation();

    @Iri(MICO.HAS_LOCATION)
    void setLayoutLocation(String layoutLocation);

    @Iri(DCTERMS.FORMAT)
    String getFormat();

    @Iri(DCTERMS.FORMAT)
    void setFormat(String format);
}
