package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.impl.micotarget.MicoSpecificResource;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

/**
 * Body class for a matching segments of two videos.
 *
 * As this class inherits MicoSpecificResource, its also inherits its behaviour of having a Selector (to
 * specify the associated Fragment) and a Source (specifying the "target" of this "SpecificResource").
 */
@Iri(MMM.MATCHING_VIDEO_SEGMENT_BODY)
public interface MatchingVideoSegmentBody extends Body, MicoSpecificResource {

// Inherited Selector and Source edges from MicoSpecificResource.
}
