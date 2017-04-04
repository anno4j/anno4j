package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.validation.NotNullValidator;
import com.github.anno4j.schema_parsing.validation.ValidatorChain;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Test for the generation of method implementations.
 */
public class PropertySupportSpecTest {

    private static RDFSModelBuilder modelBuilder;

    private static OntGenerationConfig generationConfig;

    /**
     * Returns a {@link ExtendedRDFSProperty} instance from {@link #modelBuilder}
     * with the specified URI.
     * @param uri The URI to get the property object for.
     * @return The property object or null if no property with the given URI is in the model.
     */
    private static ExtendedRDFSProperty getPropertyFromModel(String uri) {
        for(ExtendedRDFSProperty property : modelBuilder.getProperties()) {
            if(property.getResourceAsString().equals(uri)) {
                return property;
            }
        }
        return null;
    }

    @Before
    public void setUp() throws Exception {
        generationConfig = new OntGenerationConfig();
        // Language preference:
        List<String> javaDocLangPreference = Arrays.asList("en", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> identifierLangPreference = Arrays.asList("en", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        // Validator chain:
        ValidatorChain chain = new ValidatorChain();
        chain.add(new NotNullValidator());
        generationConfig.setValidators(chain);

        // Create a RDFS model builder instance:
        VehicleOntologyLoader ontologyLoader = new VehicleOntologyLoader();
        modelBuilder = ontologyLoader.getVehicleOntologyModelBuilder();

        // Build the ontology model:
        modelBuilder.build();
    }

    @Test
    public void testSetterImplementation() throws Exception {
        ExtendedRDFSProperty seatNumProp = getPropertyFromModel("http://example.de/ont#seat_num");
        assertNotNull(seatNumProp);

        MethodSpec setter = seatNumProp.buildSetterImplementation(generationConfig);

        // Override annotation:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
        assertEquals(1, setter.annotations.size());
        assertEquals(overrideAnnotation, setter.annotations.get(0));

        // Modifiers:
        assertEquals(1, setter.modifiers.size());
        assertTrue(setter.modifiers.contains(Modifier.PUBLIC));

        // Name:
        assertEquals("setNumberOfSeats", setter.name);

        // Parameter:
        ClassName set = ClassName.get(Set.class);
        ParameterizedTypeName paramType = ParameterizedTypeName.get(set, ClassName.get(Integer.class));
        assertEquals(1, setter.parameters.size());
        assertEquals(paramType, setter.parameters.get(0).type);

        // Test validation code:
        String code = setter.code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\(((.+)\\s*==\\s*null|null\\s*==\\s*(.+))\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testAdderImplementation() throws Exception {
        ExtendedRDFSProperty seatNumProp = getPropertyFromModel("http://example.de/ont#seat_num");
        assertNotNull(seatNumProp);

        MethodSpec adder = seatNumProp.buildAdderImplementation(generationConfig);

        // Override annotation:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
        assertEquals(1, adder.annotations.size());
        assertEquals(overrideAnnotation, adder.annotations.get(0));

        // Modifiers:
        assertEquals(1, adder.modifiers.size());
        assertTrue(adder.modifiers.contains(Modifier.PUBLIC));

        // Name:
        assertEquals("addNumberOfSeats", adder.name);

        // Parameter:
        assertEquals(1, adder.parameters.size());
        assertEquals(ClassName.get(Integer.class), adder.parameters.get(0).type);

        // Test validation code is present:
        String code = adder.code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\(((.+)\\s*==\\s*null|null\\s*==\\s*(.+))\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testAdderAllImplementation() throws Exception {
        ExtendedRDFSProperty seatNumProp = getPropertyFromModel("http://example.de/ont#seat_num");
        assertNotNull(seatNumProp);

        MethodSpec adderAll = seatNumProp.buildAdderAllImplementation(generationConfig);

        // Override annotation:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
        assertEquals(1, adderAll.annotations.size());
        assertEquals(overrideAnnotation, adderAll.annotations.get(0));

        // Modifiers:
        assertEquals(1, adderAll.modifiers.size());
        assertTrue(adderAll.modifiers.contains(Modifier.PUBLIC));

        // Name:
        assertEquals("addAllNumberOfSeats", adderAll.name);

        // Parameter:
        ClassName set = ClassName.get(Set.class);
        ParameterizedTypeName paramType = ParameterizedTypeName.get(set, ClassName.get(Integer.class));
        assertEquals(1, adderAll.parameters.size());
        assertEquals(paramType, adderAll.parameters.get(0).type);

        // Test validation code:
        String code = adderAll.code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\(((.+)\\s*==\\s*null|null\\s*==\\s*(.+))\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testRemoverImplementation() throws Exception {
        ExtendedRDFSProperty seatNumProp = getPropertyFromModel("http://example.de/ont#seat_num");
        assertNotNull(seatNumProp);

        MethodSpec remover = seatNumProp.buildRemoverImplementation(generationConfig);

        // Override annotation:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
        assertEquals(1, remover.annotations.size());
        assertEquals(overrideAnnotation, remover.annotations.get(0));

        // Modifiers:
        assertEquals(1, remover.modifiers.size());
        assertTrue(remover.modifiers.contains(Modifier.PUBLIC));

        // Name:
        assertEquals("removeNumberOfSeats", remover.name);

        // Parameter:
        assertEquals(1, remover.parameters.size());
        assertEquals(ClassName.get(Integer.class), remover.parameters.get(0).type);

        // Test validation code is not present:
        String code = remover.code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\(((.+)\\s*==\\s*null|null\\s*==\\s*(.+))\\)").matcher(code);
        assertFalse(matcher.find());
    }

    @Test
    public void testRemoverAllImplementation() throws Exception {
        ExtendedRDFSProperty seatNumProp = getPropertyFromModel("http://example.de/ont#seat_num");
        assertNotNull(seatNumProp);

        MethodSpec adderAll = seatNumProp.buildRemoverAllImplementation(generationConfig);

        // Override annotation:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();
        assertEquals(1, adderAll.annotations.size());
        assertEquals(overrideAnnotation, adderAll.annotations.get(0));

        // Modifiers:
        assertEquals(1, adderAll.modifiers.size());
        assertTrue(adderAll.modifiers.contains(Modifier.PUBLIC));

        // Name:
        assertEquals("removeAllNumberOfSeats", adderAll.name);

        // Parameter:
        ClassName set = ClassName.get(Set.class);
        ParameterizedTypeName paramType = ParameterizedTypeName.get(set, ClassName.get(Integer.class));
        assertEquals(1, adderAll.parameters.size());
        assertEquals(paramType, adderAll.parameters.get(0).type);

        // Test validation code:
        String code = adderAll.code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\(((.+)\\s*==\\s*null|null\\s*==\\s*(.+))\\)").matcher(code);
        assertFalse(matcher.find());
    }
}
