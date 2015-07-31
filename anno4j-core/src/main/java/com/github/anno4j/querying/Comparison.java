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