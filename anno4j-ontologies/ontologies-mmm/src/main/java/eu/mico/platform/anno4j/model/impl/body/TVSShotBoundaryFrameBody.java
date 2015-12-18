package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.namespaces.DC;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

/**
 * Class represents a Shot Boundary Frame of a given TVS analysis.
 */
@Iri(MMM.TVS_SHOT_BOUNDARY_FRAME_BODY)
public interface TVSShotBoundaryFrameBody  extends TVSBody {

    @Iri(DC.FORMAT)
    String getFormat();

    @Iri(DC.FORMAT)
    void setFormat(String format);
}
