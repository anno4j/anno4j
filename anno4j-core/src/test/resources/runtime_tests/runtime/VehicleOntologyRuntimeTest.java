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

    /**
     * Entry point of the test.
     * @throws Exception Thrown if an assertion failes or an error occurs, i.e. if the test fails.
     */
    public void run() throws Exception {
        testXSDValidation();
        testSubProperty();
    }

    /**
     * Throws an exception if the given parameters are not equal accoriding to their {@link Object#equals(Object)}
     * method.
     * This method is the same as the one in JUnit, but was remimplemented in order to avoid JUnit as an additional
     * dependency when this test is compiled at runtime.
     * @param expected The value expected. Must not be null.
     * @param actual The value to compare to. May be null.
     * @param <S> The type of the expected value.
     * @param <T> The type of the value to compare to.
     * @throws Exception Thrown if the values are not equal or <code>expected</code> is null.
     */
    private static <S, T> void assertEquals(S expected, T actual) throws Exception {
        if(!expected.equals(actual)) {
            throw new Exception("Expected " + expected.toString() + " but was " + actual.toString());
        }
    }

    /**
     * Throws an exception if the given parameter is false.
     * This method is the same as the one in JUnit, but was remimplemented in order to avoid JUnit as an additional
     * dependency when this test is compiled at runtime.
     * @param value The value to check.
     * @throws Exception Thrown if <code>value</code> is not true.
     */
    private static void assertTrue(boolean value) throws Exception {
        if(!value) {
            throw new Exception("Expected true, but was false.");
        }
    }

    /**
     * Throws an exception if the given parameter is true.
     * This method is the same as the one in JUnit, but was remimplemented in order to avoid JUnit as an additional
     * dependency when this test is compiled at runtime.
     * @param value The value to check.
     * @throws Exception Thrown if <code>value</code> is not false.
     */
    private static void assertFalse(boolean value) throws Exception {
        if(value) {
            throw new Exception("Expected false, but was true.");
        }
    }

    /**
     * Test for the XSD value space checks implemented in the support classes.
     * @throws Exception Thrown if an assertion fails or an error occurs.
     */
    private void testXSDValidation() throws Exception {
        Vehicle vehicle = new runtime.VehicleSupportImpl();

        // Test XSD datatype xsd:unsignedInt

        // Test with positive integer:
        Set<Integer> seatNums = Sets.newHashSet(4);
        vehicle.setNumberOfSeats(seatNums);
        assertEquals(seatNums, vehicle.getNumberOfSeats());

        // Test with (not allowed) negative integer:
        seatNums.add(-42);
        boolean exceptionThrown = false;
        try {
            vehicle.setNumberOfSeats(seatNums);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // Test XSD datatype xsd:token
        // Test correct token:
        Set<String> officialNames = Sets.newHashSet("Citroen 2CV");
        vehicle.setHasOfficialNames(officialNames);
        assertEquals(officialNames, vehicle.getHasOfficialNames());

        // Set empty:
        officialNames = Sets.newHashSet();
        vehicle.setHasOfficialNames(officialNames);
        assertEquals(officialNames, vehicle.getHasOfficialNames());

        // Test (not-allowed) leading whitespace:
        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet(" I have a leading whitespace"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(true, vehicle.getHasOfficialNames().isEmpty());

        // Test (not-allowed) trailing whitespace:
        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a trailing whitespace "));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(true, vehicle.getHasOfficialNames().isEmpty());

        // Test (not-allowed) inner whitespace:
        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a  inner double-whitespace"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(true, vehicle.getHasOfficialNames().isEmpty());

        // Test (not-allowed) special character:
        exceptionThrown = false;
        try {
            vehicle.setHasOfficialNames(Sets.newHashSet("I have a\t tab"));
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(true, vehicle.getHasOfficialNames().isEmpty());
    }

    /**
     * Tests the handling of values stored for superproperties.
     * @throws Exception Thrown if an assertion fails or an error occurs.
     */
    private void testSubProperty() throws Exception {
        Vehicle vehicle = new runtime.VehicleSupportImpl();

        Set<String> names = Sets.newHashSet();

        // Test setter:
        names.add("Citroen 2CV");
        vehicle.setHasOfficialNames(Sets.newHashSet("Citroen 2CV"));
        assertEquals(names, vehicle.getHasNames());

        // Test adder:
        vehicle.addHasOfficialName("VW Golf V");
        names.add("VW Golf V");
        assertEquals(names, vehicle.getHasNames());

        // Test adder all:
        vehicle.addAllHasOfficialNames(Sets.newHashSet("BMW M3", "Audi A3"));
        names.addAll(Sets.newHashSet("BMW M3", "Audi A3"));
        assertEquals(names, vehicle.getHasNames());

        // Test remover on actual official name:
        names.remove("BMW M3");
        assertTrue(vehicle.removeHasOfficialName("BMW M3"));
        assertEquals(names, vehicle.getHasNames());

        // Test remover on non-official name:
        vehicle.addHasName("Ente");
        names.add("Ente");
        assertFalse(vehicle.removeHasOfficialName("Ente"));
        assertEquals(names, vehicle.getHasNames());
    }
}
