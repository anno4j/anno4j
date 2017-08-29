package com.github.anno4j.schema_parsing.validation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
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
 * Test for {@link NotNullValidator}
 */
public class NotNullValidatorTest {

    private static Anno4j anno4j;

    private static BuildableRDFSClazz rdfsClazz;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        rdfsClazz = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl(RDFS.CLAZZ));
    }

    @Test
    public void addValueSpaceCheck() throws Exception {
        ParameterSpec paramS = ParameterSpec.builder(ClassName.get(String.class), "s")
                                            .build();

        MethodSpec.Builder method = MethodSpec.methodBuilder("foo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramS)
                .returns(void.class);

        NotNullValidator validator = new NotNullValidator();
        validator.addValueSpaceCheck(method, paramS, rdfsClazz);

        String code = method.build().code.toString();
        Matcher matcher = Pattern.compile("if\\s*\\((s\\s*==\\s*null|null\\s*==\\s*s)\\)").matcher(code);
        assertTrue(matcher.find());
    }

}