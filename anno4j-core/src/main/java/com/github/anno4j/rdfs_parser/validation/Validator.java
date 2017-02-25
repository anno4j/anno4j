package com.github.anno4j.rdfs_parser.validation;

import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

/**
 * Classes implementing this interface provide capabilities to generate Java code
 * constraining the value space Anno4j resource object methods may receive,
 * if the range of the corresponding property is a constrained datatype.
 */
public interface Validator {

    /**
     * Adds code to the given method, checking for the Java variable or parameter <code>symbol</code> being in range.
     * If a validation fails the generated code throws a {@link IllegalArgumentException}
     * with a meaningful message.
     * Does not modify the method if <code>range</code> does not represent a datatype for which
     * checks are introduced by this validator.
     * @param methodBuilder The method builder which receives the validation code.
     * @param symbol The Java symbol to be checked in the validation code.
     * @param range The RDFS class representing the datatype against which value space must be checked.
     */
    void addValueSpaceCheck(MethodSpec.Builder methodBuilder, ParameterSpec symbol, RDFSClazz range);

    /**
     * Returns a human readable description of the value space of the datatype represented by
     * the given RDFS class.
     * @param clazz The datatype.
     * @return A human readable description of the datatypes value space or null if this
     * validator does not constrain the value space of this datatype.
     */
    String getValueSpaceDefinition(RDFSClazz clazz);

    /**
     * Whether this validator constrains the value space of <code>clazz</code>.
     * @param clazz The clazz to check for.
     * @return Returns true if and only if this validator constrains the value space of
     * the given datatype, i.e. {@link #addValueSpaceCheck(MethodSpec.Builder, ParameterSpec, RDFSClazz)}
     * would generate code for <code>range</code>.
     */
    boolean isValueSpaceConstrained(RDFSClazz clazz);
}
