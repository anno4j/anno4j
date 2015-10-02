package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Datatype;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_URI;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.LiteralTypeTest;

@Evaluator(LiteralTypeTest.class)
public class LiteralTypeTestEvaluator implements QueryEvaluator {
    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration)
    {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        NodeTest nodeTest = testingSelector.getTest();
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, var, evaluatorConfiguration);

        LiteralTypeTest literalTypeTest = (LiteralTypeTest) nodeTest;
        elementGroup.addElementFilter(new ElementFilter(new E_Equals(new E_Datatype(new ExprVar(delVar)), new E_URI(new NodeValueString(literalTypeTest.getTypeUri().toString())))));

        return delVar;
    }
}
