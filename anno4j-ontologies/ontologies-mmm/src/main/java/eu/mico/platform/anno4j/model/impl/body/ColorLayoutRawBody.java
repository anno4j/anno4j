package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.COLOR_LAYOUT_RAW_BODY)
public interface ColorLayoutRawBody extends MicoBody {

    @Iri(MMM.HAS_LOCATION)
    String getLayoutLocation();

    @Iri(MMM.HAS_LOCATION)
    void setLayoutLocation(String layoutLocation);

    @Iri(DCTERMS.FORMAT)
    String getFormat();

    @Iri(DCTERMS.FORMAT)
    void setFormat(String format);
}
