package eu.mico.platform.anno4j.model.impl.micotarget;

import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;
import eu.mico.platform.anno4j.model.namespaces.MICO;


@Iri(MICO.INITIAL_TARGET)
public class InitialTarget extends Target {

    @Iri(MICO.HAS_LOCATION)
    private String location;

    public InitialTarget() {
    }

    public InitialTarget(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
