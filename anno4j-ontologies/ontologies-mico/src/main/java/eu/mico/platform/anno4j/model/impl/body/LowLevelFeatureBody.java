package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.LOW_LEVEL_FEATURE_BODY)
public interface LowLevelFeatureBody extends Body {
    
    @Iri(DCTERMS.FORMAT)
    String getFormat();

    @Iri(DCTERMS.FORMAT)
    void setFormat(String format);

    @Iri(MICO.HAS_LOCATION)
    String getLocation();

    @Iri(MICO.HAS_LOCATION)
    void setLocation(String location);
}
