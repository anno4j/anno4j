package com.github.anno4j.model.namespaces;

/**
 * Ontology class for SWRL built-in functions.
 * See <a href="http://www.w3.org/2003/11/swrlb">http://www.w3.org/2003/11/swrlb</a>.
 */
public class SWRLB {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/2003/11/swrlb#";

    /**
     * Satisfied iff the first argument and the second argument are the same.
     */
    public final static String EQUAL = NS + "equal";

    /**
     * Satisfied iff the first argument and the second argument are not the same.
     */
    public final static String NOT_EQUAL = NS + "notEqual";

    /**
     * Satisfied iff the first argument and the second argument are both in some implemented type
     * and the first argument is less than the second argument according to
     * a type-specific ordering (partial or total), if there is one defined for the type.
     */
    public final static String LESS_THAN = NS + "lessThan";

    /**
     * Satisfied iff the first argument and the second argument are both in some implemented type
     * and the first argument is less than or equal the second argument according to
     * a type-specific ordering (partial or total), if there is one defined for the type.
     */
    public final static String LESS_THAN_OR_EQUAL = NS + "lessThanOrEqual";

    /**
     * Satisfied iff the first argument and the second argument are both in some implemented type
     * and the first argument is greater than the second argument according to
     * a type-specific ordering (partial or total), if there is one defined for the type.
     */
    public final static String GREATER_THAN = NS + "greaterThan";

    /**
     * Satisfied iff the first argument and the second argument are both in some implemented type
     * and the first argument is greater than or equal the second argument according to
     * a type-specific ordering (partial or total), if there is one defined for the type.
     */
    public final static String GREATER_THAN_OR_EQUAL = NS + "greaterThanOrEqual";

    /**
     * Satisfied iff the first argument is equal to the arithmetic sum of the second argument through the last argument.
     */
    public final static String ADD = NS + "add";

    /**
     * Satisfied iff the first argument is equal to the arithmetic difference of the second argument minus the third argument.
     */
    public final static String SUBTRACT = NS + "subtract";

    /**
     * Satisfied iff the first argument is equal to the arithmetic product of the second argument through the last argument.
     */
    public final static String MULTIPLY = NS + "multiply";

    /**
     * Satisfied iff the first argument is equal to the arithmetic quotient of the second argument divided by the third argument.
     */
    public final static String DIVIDE = NS + "divide";

    /**
     * Satisfied if the first argument is the arithmetic quotient of the second argument idiv the third argument.
     */
    public final static String INTEGER_DIVIDE = NS + "integerDivide";

    /**
     * Satisfied if the first argument represents the remainder resulting from dividing the second argument,
     * the dividend, by the third argument, the divisor.
     */
    public final static String MOD = NS + "mod";

    /**
     * Satisfied iff the first argument is equal to the result of the second argument raised to the third argument power.
     */
    public final static String POW = NS + "pow";

    /**
     * Satisfied iff the first argument is equal to the second argument with its sign unchanged.
     */
    public final static String UNARY_PLUS = NS + "unaryPlus";

    /**
     * Satisfied iff the first argument is equal to the second argument with its sign reversed.
     */
    public final static String UNARY_MINUS = NS + "unaryMinus";

    /**
     * Satisfied iff the first argument is the absolute value of the second argument.
     */
    public final static String ABS = NS + "abs";
}
