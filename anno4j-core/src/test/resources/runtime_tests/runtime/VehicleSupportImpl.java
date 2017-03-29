package runtime;

import com.github.anno4j.model.impl.ResourceObject;
import de.example.VehicleSupport;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.TypeManager;
import org.openrdf.store.blob.BlobStore;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the VehicleSupport class
 * simulating Anno4j behaviour (see Issue #129).
 */
public class VehicleSupportImpl extends VehicleSupport {

    public VehicleSupportImpl() {
        // Initialize the property fields:
        seatNums = new HashSet<>();
        names = new HashSet<>();
        officialNames = new HashSet<>();
    }

    @Override
    protected Object _getResourceObject() {
        return this;
    }

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
        return seatNums;
    }

    @Override
    public void setHasNames(Set<? extends CharSequence> names) {
        super.setHasNames(names);
    }

    @Override
    public ObjectConnection getObjectConnection() {
        return null;
    }

    @Override
    public Resource getResource() {
        return new URIImpl("http://example.org/ont#someResource");
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
    }

    @Override
    public void setHasOfficialNames(Set<? extends CharSequence> values) {
        super.setHasOfficialNames(values);
    }
}
