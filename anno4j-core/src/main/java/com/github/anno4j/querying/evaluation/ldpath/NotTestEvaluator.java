package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.annotations.Evaluator;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.NotTest;

import java.util.Collection;

/**
 * This evaluator creates a filter for the logical negation.
 */
@Evaluator(NotTest.class)
public class NotTestEvaluator implements TestEvaluator {

    @Override
    public Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        NotTest notTest = (NotTest) nodeTest;
        return new E_LogicalNot(LDPathEvaluator.evaluate(notTest.getDelegate(), elementGroup, var, evaluatorConfiguration));
    }

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, var, evaluatorConfiguration);
        elementGroup.addElementFilter(new ElementFilter(evaluate(testingSelector.getTest(), elementGroup, delVar, evaluatorConfiguration)));
        return delVar;
    }
}
