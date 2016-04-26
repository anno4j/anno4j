package com.github.anno4j.querying;

import org.openrdf.query.algebra.evaluation.function.string.Contains;

/**
 * Possible comparison methods
 */
public enum Comparison {
    /**
     * Equal
     */
    EQ("="),

    /**
     * Greater than or equal
     */
    GTE(">="),

    /**
     * Greater than
     */
    GT(">"),

    /**
     * Lower than
     */
    LT("<"),

    /**
     * Lower than or equals
     */
    LTE("<="),

    CONTAINS("contains"),

    STARTS_WITH("startsWith"),

    ENDS_WITH("endWith");

    private String sparqlOperator;

    /**
     * Constructor
     * @param sparqlOperator comparison operator in SPARQL
     */
    Comparison(String sparqlOperator) {
        this.sparqlOperator = sparqlOperator;
    }

    /**
     * Getter for the SPARQL comparison operator
     * @return comparison operator in SPARQL
     */
    public String getSparqlOperator() {
        return sparqlOperator;
    }
}