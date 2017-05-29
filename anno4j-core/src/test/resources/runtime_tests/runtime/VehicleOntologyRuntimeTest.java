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

        // Test setters:
        // Setter on subproperty:
        vehicle.setHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item2"));
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasNames());
        // Setter on superproperty:
        vehicle.setHasNames(Sets.<CharSequence>newHashSet("item1"));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasNames());
        // Clear values:
        vehicle.setHasNames(Sets.<CharSequence>newHashSet());
        assertEquals(0, vehicle.getHasNames().size());
        assertEquals(0, vehicle.getHasOfficialNames().size());

        // Test adders:
        // Adder on subproperty:
        vehicle.addHasOfficialName("item1");
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasNames());
        // Adder on superproperty:
        vehicle.addHasName("item2");
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasNames());
        // Adder on subproperty intersecting superproperty values:
        vehicle.addHasOfficialName("item2");
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasNames());
        // Clear values:
        vehicle.setHasNames(Sets.<CharSequence>newHashSet());
        assertEquals(0, vehicle.getHasNames().size());
        assertEquals(0, vehicle.getHasOfficialNames().size());

        // Test adder-all:
        // Adder-all on subproperty:
        vehicle.addAllHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item2"));
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasNames());
        // Adder-all on superproperty:
        vehicle.addAllHasNames(Sets.<CharSequence>newHashSet("item3"));
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2", "item3"), vehicle.getHasNames());
        // Adder-all on subproperty intersecting suproperty values:
        vehicle.addAllHasOfficialNames(Sets.<CharSequence>newHashSet("item2", "item3"));
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2", "item3"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item2", "item3"), vehicle.getHasNames());
        // Clear values:
        vehicle.setHasNames(Sets.<CharSequence>newHashSet());
        assertEquals(0, vehicle.getHasNames().size());
        assertEquals(0, vehicle.getHasOfficialNames().size());

        // Test remover:
        vehicle.addAllHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item2"));
        vehicle.addHasName("item3");
        // Test remover on subproperty with existing value:
        assertTrue(vehicle.removeHasOfficialName("item2"));
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item3"), vehicle.getHasNames());
        // Test remover on subproperty with non-existing value:
        assertFalse(vehicle.removeHasOfficialName("item2"));
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item3"), vehicle.getHasNames());
        // Test remover on subproperty with superproperty-only value:
        assertFalse(vehicle.removeHasOfficialName("item3"));
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item3"), vehicle.getHasNames());
        // Test remover on superproperty with existing value:
        assertTrue(vehicle.removeHasName("item1"));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item3"), vehicle.getHasNames());
        // Test remover on superproperty with non-existing value:
        assertFalse(vehicle.removeHasName("item1"));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item3"), vehicle.getHasNames());
        // Remove superproperty value only:
        assertTrue(vehicle.removeHasName("item3"));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(0, vehicle.getHasNames().size());

        // Test remover-all:
        vehicle.addAllHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item2"));
        vehicle.addAllHasNames(Sets.<CharSequence>newHashSet("item3", "item4"));
        // Test remover-all on subproperty with only existing values:
        assertTrue(vehicle.removeAllHasOfficialNames(Sets.<CharSequence>newHashSet("item2")));
        assertEquals(Sets.<CharSequence>newHashSet("item1"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item1", "item3", "item4"), vehicle.getHasNames());
        // Test remover-all on subproperty with partially existing values:
        assertTrue(vehicle.removeAllHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item5")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item3", "item4"), vehicle.getHasNames());
        // Test remover-all on subproperty with value from superproperty:
        assertFalse(vehicle.removeAllHasOfficialNames(Sets.<CharSequence>newHashSet("item3")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item3", "item4"), vehicle.getHasNames());
        // Test remover-all on subproperty with non existing values:
        assertFalse(vehicle.removeAllHasOfficialNames(Sets.<CharSequence>newHashSet("item5")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item3", "item4"), vehicle.getHasNames());
        vehicle.addAllHasOfficialNames(Sets.<CharSequence>newHashSet("item1", "item2"));
        // Test remover-all on superproperty with only existing values intersecting subproperty:
        assertTrue(vehicle.removeAllHasNames(Sets.<CharSequence>newHashSet("item1")));
        assertEquals(Sets.<CharSequence>newHashSet("item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item2", "item3", "item4"), vehicle.getHasNames());
        // Test remover-all on superproperty with only existing values not intersecting subproperty:
        assertTrue(vehicle.removeAllHasNames(Sets.<CharSequence>newHashSet("item3")));
        assertEquals(Sets.<CharSequence>newHashSet("item2"), vehicle.getHasOfficialNames());
        assertEquals(Sets.<CharSequence>newHashSet("item2", "item4"), vehicle.getHasNames());
        // Test remover-all on superproperty with partially existing values intersecting subproperty:
        assertTrue(vehicle.removeAllHasNames(Sets.<CharSequence>newHashSet("item2", "item5")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(Sets.<CharSequence>newHashSet("item4"), vehicle.getHasNames());
        // Test remover-all on superproperty with partially existing values not intersecting subproperty:
        assertTrue(vehicle.removeAllHasNames(Sets.<CharSequence>newHashSet("item4", "item5")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(0, vehicle.getHasNames().size());
        // Test remover-all on superproperty with not existing values not intersecting subproperty:
        assertFalse(vehicle.removeAllHasNames(Sets.<CharSequence>newHashSet("item5")));
        assertEquals(0, vehicle.getHasOfficialNames().size());
        assertEquals(0, vehicle.getHasNames().size());
    }
}
