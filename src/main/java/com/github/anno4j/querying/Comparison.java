package com.github.anno4j.querying;

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
    LTE("<=");

    private String sparqlOperator;

    Comparison(String s) {
        this.sparqlOperator = s;
    }

    public String getSparqlOperator() {
        return sparqlOperator;
    }
}