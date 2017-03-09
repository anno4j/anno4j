package runtime;

import de.example.VehicleSupport;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the VehicleSupport class
 * simulating Anno4j behaviour.
 */
public class VehicleSupportImpl extends VehicleSupport {

    private Set<String> names = new HashSet<>();

    private Set<String> officialNames = new HashSet<>();

    private Set<Integer> numberOfSeats = new HashSet<>();

    @Override
    public Set<String> getHasOfficialNames() {
        return officialNames;
    }

    @Override
    public Set<String> getHasNames() {
        return names;
    }

    @Override
    public Set<Integer> getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public void setHasNames(Set<String> names) {
        this.names.clear();
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
    public void addHasName(String value) {
        names.add(value);
    }

    @Override
    public void addAllHasNames(Set<String> values) {
        names.addAll(values);
    }

    @Override
    public boolean removeHasName(String value) {
        return names.remove(value);
    }

    @Override
    public boolean removeAllHasNames(Set<String> values) {
        return names.removeAll(values);
    }

    @Override
    public void setNumberOfSeats(Set<Integer> values) {
        super.setNumberOfSeats(values);
        numberOfSeats.clear();
        numberOfSeats.addAll(values);
    }

    @Override
    public void setHasOfficialNames(Set<String> values) {
        super.setHasOfficialNames(values);
        officialNames.clear();
        officialNames.addAll(values);
    }
}
