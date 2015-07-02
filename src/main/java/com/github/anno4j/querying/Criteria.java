package com.github.anno4j.querying;

/**
 * A Criteria represents an object, that encapsulates
 * the constraints passed to the QueryService by the
 * user.
 */
public class Criteria {

    /**
     * Indicates if the given constraint is a number
     */
    private boolean isNaN;

    /**
     * The LDPath string
     *
     * @see <a href="http://marmotta.apache.org/ldpath/">http://marmotta.apache.org/ldpath/</a>
     */
    private String ldpath;

    /**
     * The actual value to compare against.
     */
    private String constraint;

    /**
     * The comparison method. Allowed values specified by {@link com.github.anno4j.querying.Comparison}
     */
    private Comparison comparison;

    /**
     * Default constructor is required by AliBaba!
     */
    public Criteria() {
    }

    /**
     * Custom constructor for a textual constraint
     *
     * @param ldpath     The LDPath value
     * @param constraint A textual constraint
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     */
    public Criteria(String ldpath, String constraint, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
        this.constraint = constraint;
        this.isNaN = true;
    }

    /**
     * Custom constructor for a numerical constraint.
     *
     * @param ldpath     The LDPath value
     * @param constraint A numerical constraint
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     */
    public Criteria(String ldpath, Number constraint, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
        this.constraint = constraint.toString();
        this.isNaN = false;
    }

    /**
     * Constructor without a constraint
     *
     * @param ldpath     The LDPath value
     * @param comparison he comparison mode, e.g. Comparison.EQ (=)
     */
    public Criteria(String ldpath, Comparison comparison) {
        this.ldpath = ldpath;
        this.comparison = comparison;
    }

    /**
     * @return the ldpath string
     */
    public String getLdpath() {
        return ldpath;
    }

    /**
     * @param ldpath The LDPath string ()
     */
    public void setLdpath(String ldpath) {
        this.ldpath = ldpath;
    }

    /**
     * @return the comparison method
     */
    public Comparison getComparison() {
        return comparison;
    }

    /**
     * @param comparison The comparison method
     */
    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    /**
     * @return the constraint The constraint as string
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * @param constraint The textual constraint
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    /**
     * @param constraint The numerical constraint
     */
    public void setContstraint(Number constraint) {
        this.constraint = constraint.toString();
    }

    /**
     * @return Value of Indicates if the given constraint is a number.
     */
    public boolean isNaN() {
        return isNaN;
    }
}
