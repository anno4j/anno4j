package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.LOW_LEVEL_FEATURE_BODY)
public class LowLevelFeatureBody extends Body {
    
    @Iri(DCTERMS.FORMAT)
    private String format;
    
    @Iri(MICO.HAS_LOCATION)
    private String location;

    public LowLevelFeatureBody() {
    }

    public LowLevelFeatureBody(String format, String location) {
        this.format = format;
        this.location = location;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
