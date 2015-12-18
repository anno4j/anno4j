package eu.mico.platform.anno4j.model.impl.micotarget;

import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;
import eu.mico.platform.anno4j.model.namespaces.MMM;


@Iri(MMM.INITIAL_TARGET)
public interface InitialTarget extends Target {

    @Iri(MMM.HAS_LOCATION)
    String getLocation();

    @Iri(MMM.HAS_LOCATION)
    void setLocation(String location);
}
