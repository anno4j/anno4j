package eu.mico.platform.anno4j.model;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

/**
 * Class represents the overall Body class for MICO specific bodies.
 */
@Iri(MMM.BODY)
public interface MicoBody extends Body {
}
