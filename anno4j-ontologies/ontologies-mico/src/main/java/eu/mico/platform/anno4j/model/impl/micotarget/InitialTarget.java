package eu.mico.platform.anno4j.model.impl.micotarget;

import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;
import eu.mico.platform.anno4j.model.namespaces.MICO;


@Iri(MICO.INITIAL_TARGET)
public interface InitialTarget extends Target {

    @Iri(MICO.HAS_LOCATION)
    String getLocation();

    @Iri(MICO.HAS_LOCATION)
    void setLocation(String location);
}
