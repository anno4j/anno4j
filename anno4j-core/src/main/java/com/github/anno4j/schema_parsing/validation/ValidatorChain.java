package com.github.anno4j.schema_parsing.validation;

import com.github.anno4j.model.rdfs.RDFSClazz;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Defines a sequence of {@link Validator} instances which inject value space
 * validation code into Java methods.
 */
public class ValidatorChain extends ArrayList<Validator> {

    /**
     * Adds code to the given method, checking for the Java variable or parameter <code>symbol</code> being in range
     * for every {@link Validator} in this chain.
     * If a validation fails the generated code throws a {@link IllegalArgumentException}
     * with a meaningful message.
     * The validation code is inserted in the sequence of validators in this chain.
     * @param methodBuilder The method to add code to.
     * @param symbol The variable or parameter to validate.
     * @param range The datatype of the symbol.
     */
    public void addValueSpaceChecks(MethodSpec.Builder methodBuilder, ParameterSpec symbol, RDFSClazz range) {
        for (Validator validator : this) {
            validator.addValueSpaceCheck(methodBuilder, symbol, range);
        }
    }

    /**
     * Returns the human readable value space definitions of the given datatype, i.e.
     * a list of constraints made by the validators on the datatypes value space.
     * @param range The datatype to get the definitions for.
     * @return The definitions in the same order as the validators imposing them.
     */
    public String[] getValueSpaceDefinitions(RDFSClazz range) {
        String[] definitions = new String[size()];

        ListIterator<Validator> iterator = listIterator();
        while (iterator.hasNext()) {
            definitions[iterator.nextIndex()] = iterator.next().getValueSpaceDefinition(range);
        }

        return definitions;
    }

    /**
     * Checks whether this validator chain constraines the value of a datatype.
     * @param range The datatype to check for.
     * @return Returns true if and only if any of the validators in the chain
     * imposes a constraint on the datatypes value space, i.e. generates code
     * validating symbols of this type.
     */
    public boolean isValueSpaceConstrained(RDFSClazz range) {
        boolean isConstrained = false;
        for (Validator validator : this) {
            isConstrained |= validator.isValueSpaceConstrained(range);
        }
        return isConstrained;
    }

    /**
     * Creates the default validator chain for RDFS.
     * This chain has the following elements (in this order):
     * <ol>
     *     <li>{@link NotNullValidator}</li>
     *     <li>{@link XSDValueSpaceValidator}</li>
     * </ol>
     * @return The default validator chain for RDFS.
     */
    public static ValidatorChain getRDFSDefault() {
        ValidatorChain chain = new ValidatorChain();
        chain.add(new NotNullValidator());
        chain.add(new XSDValueSpaceValidator());
        return chain;
    }
}
