package com.github.anno4j.querying;

public class Criteria {

    private String ldpath;

    private String value;

    private Comparison comparison;

    public Criteria() {
    }

    public Criteria(String ldpath, String value, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
        this.value = value;
    }

    public String getLdpath() {
        return ldpath;
    }

    public void setLdpath(String ldpath) {
        this.ldpath = ldpath;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
