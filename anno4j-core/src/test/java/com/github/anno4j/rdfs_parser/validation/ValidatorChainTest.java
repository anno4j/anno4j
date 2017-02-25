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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link ValidatorChain}
 */
public class ValidatorChainTest {

    private static Anno4j anno4j;

    private static ExtendedRDFSClazz xsdPositiveInt;

    private static ExtendedRDFSClazz myCustomType;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        xsdPositiveInt = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(XSD.POSITIVE_INTEGER));
        myCustomType = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/mct"));
    }

    @Test
    public void addValueSpaceChecks() throws Exception {


        ParameterSpec param = ParameterSpec.builder(ClassName.get(Object.class), "x").build();

        MethodSpec method = MethodSpec.methodBuilder("foo")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(param)
                        .returns(void.class)
                        .build();

        MethodSpec.Builder methodBuilder = method.toBuilder();

        Pattern notNullCheckPattern = Pattern.compile("if\\s*\\((x\\s*==\\s*null|null\\s*==\\s*x)\\)");
        Pattern positiveCheckPattern = Pattern.compile("if\\s*\\((x\\s*<=\\s*0|0\\s*>=\\s*x)\\)");


        ValidatorChain chain = new ValidatorChain();
        chain.add(new NotNullValidator());

        chain.addValueSpaceChecks(methodBuilder, param, xsdPositiveInt);
        String code = methodBuilder.build().code.toString();

        Matcher notNullCheckMatcher = notNullCheckPattern.matcher(code);
        Matcher positiveCheckMatcher = positiveCheckPattern.matcher(code);

        assertTrue(notNullCheckMatcher.find());
        assertFalse(positiveCheckMatcher.find());

        chain.add(new XSDValueSpaceValidator());
        chain.addValueSpaceChecks(methodBuilder, param, xsdPositiveInt);
        code = methodBuilder.build().code.toString();

        notNullCheckMatcher = notNullCheckPattern.matcher(code);
        positiveCheckMatcher = positiveCheckPattern.matcher(code);

        assertTrue(notNullCheckMatcher.find());
        assertTrue(positiveCheckMatcher.find());
        assertTrue(notNullCheckMatcher.end() < positiveCheckMatcher.start());
    }

    @Test
    public void isValueSpaceConstrained() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        chain.add(new XSDValueSpaceValidator());

        assertTrue(chain.isValueSpaceConstrained(xsdPositiveInt));
        assertFalse(chain.isValueSpaceConstrained(myCustomType));
    }

}