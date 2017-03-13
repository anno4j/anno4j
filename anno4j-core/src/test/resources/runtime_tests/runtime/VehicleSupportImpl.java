package runtime;

import de.example.VehicleSupport;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the VehicleSupport class
 * simulating Anno4j behaviour (see Issue #129).
 */
public class VehicleSupportImpl extends VehicleSupport {

    private Set<CharSequence> names = new HashSet<>();

    private Set<CharSequence> officialNames = new HashSet<>();

    private Set<Integer> numberOfSeats = new HashSet<>();

    @Override
    public Set<? extends CharSequence> getHasOfficialNames() {
        return officialNames;
    }

    @Override
    public Set<? extends CharSequence> getHasNames() {
        return names;
    }

    @Override
    public Set<Integer> getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public void setHasNames(Set<? extends CharSequence> names) {
        this.names = new HashSet<>();
        this.names.addAll(names);
    }

    @Override
    public ObjectConnection getObjectConnection() {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public void addHasName(CharSequence value) {
        super.addHasName(value);
    }

    @Override
    public void addAllHasNames(Set<? extends CharSequence> values) {
        super.addAllHasNames(values);
    }

    @Override
    public boolean removeHasName(CharSequence value) {
        return super.removeHasName(value);
    }

    @Override
    public boolean removeAllHasNames(Set<? extends CharSequence> values) {
        return super.removeAllHasNames(values);
    }

    @Override
    public void setNumberOfSeats(Set<Integer> values) {
        super.setNumberOfSeats(values);
        numberOfSeats = new HashSet<>();
        numberOfSeats.addAll(values);
    }

    @Override
    public void setHasOfficialNames(Set<? extends CharSequence> values) {
        super.setHasOfficialNames(values);
        officialNames = new HashSet<>();
        officialNames.addAll(values);
    }
}
