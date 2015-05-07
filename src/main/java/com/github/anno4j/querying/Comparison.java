package com.github.anno4j.querying;

public enum Comparison {
    EQ("="), GTE(">="), GT(">"), LT("<"), LTE("<=");

    private String sparqlOperator;

    Comparison(String s) {
        this.sparqlOperator = s;
    }

    public String getSparqlOperator() {
        return sparqlOperator;
    }
}