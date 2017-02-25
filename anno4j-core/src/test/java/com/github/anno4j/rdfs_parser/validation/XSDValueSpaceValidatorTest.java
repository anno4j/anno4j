package com.github.anno4j.rdfs_parser.validation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import javax.lang.model.element.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link XSDValueSpaceValidator}.
 */
public class XSDValueSpaceValidatorTest {

    private static Anno4j anno4j;

    private static ParameterSpec param;

    private static MethodSpec method;

    private static Validator validator = new XSDValueSpaceValidator();

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();

        param = ParameterSpec.builder(ClassName.get(Object.class), "x").build();

        method = MethodSpec.methodBuilder("foo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(param)
                .returns(void.class)
                .build();
    }

    @Test
    public void testNonPositive() throws Exception {
        ExtendedRDFSClazz xsdNonPositiveInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.NON_POSITIVE_INTEGER));

        MethodSpec.Builder methodBuilder = method.toBuilder();
        validator.addValueSpaceCheck(methodBuilder, param, xsdNonPositiveInt);
        String code = methodBuilder.build().code.toString();

        Matcher matcher = Pattern.compile("if\\s*\\((x\\s*>\\s*0|0\\s*<\\s*x)\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testNegative() throws Exception {
        ExtendedRDFSClazz xsdNegativeInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.NEGATIVE_INTEGER));

        MethodSpec.Builder methodBuilder = method.toBuilder();
        validator.addValueSpaceCheck(methodBuilder, param, xsdNegativeInt);
        String code = methodBuilder.build().code.toString();

        Matcher matcher = Pattern.compile("if\\s*\\((x\\s*>=\\s*0|0\\s*<=\\s*x)\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testNonNegative() throws Exception {
        ExtendedRDFSClazz xsdNonNegativeInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.NON_NEGATIVE_INTEGER));

        MethodSpec.Builder methodBuilder = method.toBuilder();
        validator.addValueSpaceCheck(methodBuilder, param, xsdNonNegativeInt);
        String code = methodBuilder.build().code.toString();

        Matcher matcher = Pattern.compile("if\\s*\\((x\\s*<\\s*0|0\\s*>\\s*x)\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testUnsigned() throws Exception {
        ExtendedRDFSClazz xsdUnsignedInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.UNSIGNED_INT));

        MethodSpec.Builder methodBuilder = method.toBuilder();
        validator.addValueSpaceCheck(methodBuilder, param, xsdUnsignedInt);
        String code = methodBuilder.build().code.toString();

        Matcher matcher = Pattern.compile("if\\s*\\((x\\s*<\\s*0|0\\s*>\\s*x)\\)").matcher(code);
        assertTrue(matcher.find());
    }

    @Test
    public void testPositive() throws Exception {
        ExtendedRDFSClazz xsdPositiveInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.POSITIVE_INTEGER));

        MethodSpec.Builder methodBuilder = method.toBuilder();
        validator.addValueSpaceCheck(methodBuilder, param, xsdPositiveInt);
        String code = methodBuilder.build().code.toString();

        Matcher matcher = Pattern.compile("if\\s*\\((x\\s*<=\\s*0|0\\s*>=\\s*x)\\)").matcher(code);
        assertTrue(matcher.find());
    }
}