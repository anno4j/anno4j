package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.AndTest;

/**
 * This evaluator creates a filter for the logical conjunction. The evaluation of the
 * particular expressions will be handled by a more specific evaluator.
 */
@Evaluator(AndTest.class)
public class AndTestEvaluator implements TestEvaluator {
    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, var, evaluatorConfiguration);
        elementGroup.addElementFilter(new ElementFilter(evaluate(testingSelector.getTest(), elementGroup, delVar, evaluatorConfiguration)));
        return var;
    }

    @Override
    public Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        AndTest andTest = (AndTest) nodeTest;
        Expr expr1 = LDPathEvaluator.evaluate(andTest.getLeft(), elementGroup, var, evaluatorConfiguration);
        Expr expr2 = LDPathEvaluator.evaluate(andTest.getRight(), elementGroup, var, evaluatorConfiguration);
        return new E_LogicalAnd(expr1, expr2);
    }
}
