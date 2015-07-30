package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.COLOR_LAYOUT_RAW_BODY)
public class ColorLayoutRawBody extends Body {
    
    @Iri(MICO.HAS_LOCATION)
    private String layoutLocation;
    
    @Iri(DCTERMS.FORMAT)
    private String format;

    public ColorLayoutRawBody() {
    }

    public ColorLayoutRawBody(String layoutLocation, String format) {
        this.layoutLocation = layoutLocation;
        this.format = format;
    }

    public String getLayoutLocation() {
        return layoutLocation;
    }

    public void setLayoutLocation(String layoutLocation) {
        this.layoutLocation = layoutLocation;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
