package com.github.anno4j.querying.extension;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.LeftBesidesTestFunction;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.FunctionTest;

@Evaluator(LeftBesidesTestFunction.class)
public class LeftBesidesTestFunctionEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        FunctionTest functionTest = (FunctionTest) testingSelector.getTest();
        LeftBesidesTestFunction leftBesidesTestFunction = (LeftBesidesTestFunction) functionTest.getTest();

        System.out.println("inside leftBesides.evaluate()");
        return null;
    }
}
