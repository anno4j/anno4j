package com.github.anno4j.querying.evaluation;

import com.github.anno4j.querying.Comparison;
import com.github.anno4j.querying.Criteria;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.*;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDouble;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;

/**
 * Created by schlegel on 03/06/15.
 */
public class EvalComparison {

    public static void evaluate(ElementGroup elementGroup, Criteria criteria, Var variable, String resolvedConstraint) {

        if (criteria.isNaN()) {

            String constraint = "";

            // Setting the boundaries (\b) to the RegExp, according to the comparison type
            if (Comparison.EQ.equals(criteria.getComparison())) {
                constraint = "^" + resolvedConstraint + "$";
            } else if (Comparison.CONTAINS.equals(criteria.getComparison())) {
                constraint = resolvedConstraint;
            } else if (Comparison.STARTS_WITH.equals(criteria.getComparison())) {
                constraint = "^" + resolvedConstraint;
            } else if (Comparison.ENDS_WITH.equals(criteria.getComparison())) {
                constraint = resolvedConstraint + "$";
            } else {
                throw new IllegalStateException(criteria.getComparison() + " is only allowed on Numbers.");
            }

            if (!constraint.equals("")) {
                elementGroup.addElementFilter(new ElementFilter(new E_Regex(new E_Str(new ExprVar(variable.asNode())), constraint , "")));
            }
        } else {
            Expr expr;

            if (criteria.getComparison().equals(Comparison.GT)) {
                expr = new E_GreaterThan(new ExprVar(variable.asNode()), new NodeValueDouble(Double.parseDouble(resolvedConstraint)));
            } else if (criteria.getComparison().equals(Comparison.GTE)) {
                expr = new E_GreaterThanOrEqual(new ExprVar(variable.asNode()), new NodeValueDouble(Double.parseDouble(resolvedConstraint)));
            } else if (criteria.getComparison().equals(Comparison.LT)) {
                expr = new E_LessThan(new ExprVar(variable.asNode()), new NodeValueDouble(Double.parseDouble(resolvedConstraint)));
            } else if (criteria.getComparison().equals(Comparison.LTE)) {
                expr = new E_LessThanOrEqual(new ExprVar(variable.asNode()), new NodeValueDouble(Double.parseDouble(resolvedConstraint)));
            } else if (criteria.getComparison().equals(Comparison.EQ)) {
                expr = new E_Equals(new ExprVar(variable.asNode()), new ExprVar(criteria.getConstraint()));
            } else {
                throw new IllegalStateException(criteria.getComparison() + " is not allowed on Numbers.");
            }

            if(expr != null) {
                elementGroup.addElementFilter(new ElementFilter(expr));
            }
        }
    }
}
