package com.github.anno4j.rdfs_parser.validation;

import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

/**
 * A validator injecting code which checks that a given variable or symbol
 * is not null.
 * This check is inserted regardless of the passed datatype.
 */
public class NotNullValidator implements Validator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addValueSpaceCheck(MethodSpec.Builder methodBuilder, ParameterSpec symbol, RDFSClazz range) {
        ClassName illegalArgEx = ClassName.get(IllegalArgumentException.class);

        methodBuilder.beginControlFlow("if($N == null)", symbol)
                    .addStatement("throw new $T($S)", illegalArgEx, "Value must not be null")
                    .endControlFlow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueSpaceDefinition(RDFSClazz clazz) {
        return "The value is not null.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValueSpaceConstrained(RDFSClazz clazz) {
        return true;
    }
}
