package com.github.anno4j.querying.evaluation;

import com.github.anno4j.querying.Comparison;
import com.github.anno4j.querying.Criteria;

/**
 * Created by schlegel on 03/06/15.
 */
public class EvalComparison {

    /**
     * Evaluates the comparison method defined in the Criteria object.
     *
     * @param query        StringBuilder for the SPARQL query
     * @param criteria     The current Criteria Object
     * @param variableName The latest created variable name
     */
    public static void evaluate(StringBuilder query, Criteria criteria, String variableName) {
        if (Comparison.EQ.equals(criteria.getComparison())) {
            query
                    .append("FILTER regex( str(?")
                    .append(variableName)
                    .append(") ")
                    .append((criteria.isNaN()) ? ", \"" : ", ") // Adding quotes if the given value is not a number
                    .append(criteria.getConstraint())
                    .append((criteria.isNaN()) ? "\" ) ." : " ) .") // Adding quotes if the given value is not a number
                    .append(System.getProperty("line.separator"));
        } else {
            if (!criteria.isNaN()) {
                query
                        .append("FILTER ( ?")
                        .append(variableName)
                        .append(" ")
                        .append(criteria.getComparison().getSparqlOperator())
                        .append(" ")
                        .append(criteria.getConstraint())
                        .append(" ) .")
                        .append(System.getProperty("line.separator"));
            } else {
                throw new IllegalStateException(criteria.getComparison() + " only allowed on Numbers.");
            }
        }
    }
}
