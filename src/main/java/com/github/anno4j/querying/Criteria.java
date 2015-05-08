package com.github.anno4j.querying;

public class Criteria {

    private boolean isNaN;
    private String ldpath;

    private String constraint;

    private Comparison comparison;

    public Criteria() {
    }

    public Criteria(String ldpath, String constraint, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
        this.constraint = constraint;
        this.isNaN = true;
    }

    public Criteria(String ldpath, Number constraint, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
        this.constraint = constraint.toString();
        this.isNaN = false;
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

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public boolean isNaN() {
        return isNaN;
    }
}
