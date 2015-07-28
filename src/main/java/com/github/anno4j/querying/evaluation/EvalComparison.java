package com.github.anno4j.querying.evaluation;

import com.github.anno4j.querying.Comparison;
import com.github.anno4j.querying.Criteria;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.*;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.model.tests.LiteralTypeTest;

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

        if (criteria.isNaN()) {
            if (Comparison.EQ.equals(criteria.getComparison())) {
                query
                        .append("FILTER regex( str(?")
                        .append(variableName)
                        .append(") ")
                        .append((criteria.isNaN()) ? ", \"" : ", ") // Adding quotes if the given value is not a number
                        .append(criteria.getConstraint())
                        .append((criteria.isNaN()) ? "\" ) ." : " ) ."); // Adding quotes if the given value is not a number
            } else {
                throw new IllegalStateException(criteria.getComparison() + " only allowed on Numbers.");
            }
        } else {
            query
                    .append("FILTER ( ?")
                    .append(variableName)
                    .append(" ")
                    .append(criteria.getComparison().getSparqlOperator())
                    .append(" ")
                    .append(criteria.getConstraint())
                    .append(" ) .");
        }
    }

    public static void evaluate(ElementGroup elementGroup, Criteria criteria, Var variable) {
        if (criteria.isNaN()) {
            if (Comparison.EQ.equals(criteria.getComparison())) {
                ElementFilter filter = new ElementFilter(new E_Regex(new E_Str(new ExprVar(variable.asNode())), criteria.getConstraint(), ""));
                elementGroup.addElementFilter(filter);
            } else {
                throw new IllegalStateException(criteria.getComparison() + " only allowed on Numbers.");
            }
        } else {
            Expr expr = new E_Equals(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));

            if (criteria.getComparison().equals(Comparison.GT)) {
                expr = new E_GreaterThan(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));
            } else if (criteria.getComparison().equals(Comparison.GTE)) {
                expr = new E_GreaterThanOrEqual(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));
            } else if (criteria.getComparison().equals(Comparison.LT)) {
                expr = new E_LessThan(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));
            } else if (criteria.getComparison().equals(Comparison.LTE)) {
                expr = new E_LessThanOrEqual(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));
            }

            ElementFilter filter = new ElementFilter(expr);
            elementGroup.addElementFilter(filter);
        }
    }
}
