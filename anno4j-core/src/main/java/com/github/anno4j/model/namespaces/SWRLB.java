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

    /**
     * Satisfied iff the first argument is the smallest number with no fractional part that is greater than or equal to the second argument.
     */
    public final static String CEILING = NS + "ceiling";

    /**
     * Satisfied iff the first argument is the largest number with no fractional part that is less than or equal to the second argument.
     */
    public static final String FLOOR = NS + "floor";

    /**
     * Satisfied iff the first argument is equal to the nearest number to the second argument with no fractional part.
     */
    public static final String ROUND = NS + "round";

    /**
     * Satisfied iff the first argument is the same as the second argument (upper/lower case ignored)
     */
    public static final String STRING_EQUAL_IGNORE_CASE = NS + "stringEqualIgnoreCase";

    /**
     * Satisfied iff the first argument is equal to the string resulting from the concatenation of the strings the second argument through the last argument.
     */
    public static final String STRING_CONCAT = NS + "stringConcat";

    /**
     * Satisfied iff the first argument is equal to the substring of optional length the fourth argument starting at
     * character offset the third argument in the string the second argument.
     */
    public static final String SUBSTRING = NS + "substring";

    /**
     * Satisfied iff the first argument is equal to the length of the second argument.
     */
    public static final String STRING_LENGTH = NS + "stringLength";

    /**
     * Satisfied iff the first argument is equal to the whitespace-normalized value of the second argument.
     */
    public static final String NORMALIZE_SPACE = NS + "normalizeSpace";

    /**
     * Satisfied iff the first argument is equal to the upper-cased value of the second argument.
     */
    public static final String UPPERCASE = NS + "upperCase";

    /**
     * Satisfied iff the first argument is equal to the lower-cased value of the second argument.
     */
    public static final String LOWERCASE = NS + "lowerCase";

    /**
     * Satisfied iff the first argument contains the second argument (case sensitive).
     */
    public static final String CONTAINS = NS + "contains";

    /**
     * Satisfied iff the first argument contains the second argument (case ignored).
     */
    public static final String CONTAINS_IGNORE_CASE = NS + "containsIgnoreCase";

    /**
     * Satisfied iff the first argument starts with the second argument.
     */
    public static final String STARTS_WITH = NS + "startsWith";

    /**
     * Satisfied iff the first argument ends with the second argument.
     */
    public static final String ENDS_WITH = NS + "endsWith";

    /**
     * Satisfied iff the first argument matches the regular expression the second argument.
     */
    public static final String MATCHES = NS + "matches";
}
