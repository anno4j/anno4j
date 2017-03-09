package runtime;

import com.github.anno4j.Anno4j;
import com.google.common.collect.Sets;
import de.example.Car;
import de.example.Vehicle;

import java.util.Set;


/**
 * This test is compiled, loaded and executed at runtime by
 * {@link RDFSJavaFileGeneratorTest}.
 */
public class VehicleOntologyRuntimeTest {

    public void run() throws Exception {
        testXSDValidation();
        testSubProperty();
    }

    private void testXSDValidation() throws Exception {
        Vehicle vehicle = new runtime.VehicleSupportImpl();

        Set<Integer> seatNums = Sets.newHashSet(4);
        vehicle.setNumberOfSeats(seatNums);
        if(!seatNums.equals(vehicle.getNumberOfSeats())) {
            throw new Exception("Expected " + seatNums.toString() + " but was " + vehicle.getNumberOfSeats().toString());
        }

        seatNums.add(-42);
        boolean exceptionThrown = false;
        try {
            vehicle.setNumberOfSeats(seatNums);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if(!exceptionThrown) {
            throw new Exception();
        }

        Set<String> officialNames = Sets.newHashSet("Citroen 2CV");
        vehicle.setHasOfficialNames(officialNames);
        if(!officialNames.equals(vehicle.getHasOfficialNames())) {
            throw new Exception("Expected " + officialNames.toString() + " but was " + vehicle.getHasOfficialNames().toString());
        }

        vehicle.setHasOfficialNames(Sets.newHashSet());

        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet(" I have a leading whitespace"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if(!exceptionThrown || !vehicle.getHasOfficialNames().isEmpty()) {
            throw new Exception();
        }

        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a trailing whitespace "));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if(!exceptionThrown || !vehicle.getHasOfficialNames().isEmpty()) {
            throw new Exception();
        }

        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a  inner double-whitespace"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if(!exceptionThrown || !vehicle.getHasOfficialNames().isEmpty()) {
            throw new Exception();
        }

        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a\t tab"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        if(!exceptionThrown || !vehicle.getHasOfficialNames().isEmpty()) {
            throw new Exception();
        }
    }

    private void testSubProperty() throws Exception {
        Vehicle vehicle = new runtime.VehicleSupportImpl();

        Set<String> officialNames = Sets.newHashSet("Citroen 2CV");
        vehicle.setHasOfficialNames(officialNames);
        if(!officialNames.equals(vehicle.getHasNames())) {
            throw new Exception();
        }
    }
}
