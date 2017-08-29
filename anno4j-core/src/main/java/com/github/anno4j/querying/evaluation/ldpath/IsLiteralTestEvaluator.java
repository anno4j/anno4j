package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.annotations.Evaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_IsLiteral;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.PropertySelector;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.FunctionTest;

/**
 * Creates the part of the SPARQL query that tests for literal values
 */
@Evaluator(IsLiteralTest.class)
public class IsLiteralTestEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        FunctionTest functionTest = (FunctionTest) testingSelector.getTest();

        if(functionTest.getArgSelectors().get(0) instanceof PropertySelector) {
            PropertySelector arg = (PropertySelector) functionTest.getArgSelectors().get(0);
            PropertySelector delegate = (PropertySelector) testingSelector.getDelegate();

            Var target = Var.alloc(VarIDGenerator.createID());
            elementGroup.addTriplePattern(new Triple(var.asNode(), NodeFactory.createURI(delegate.getProperty().toString()), target));

            Var selector = Var.alloc(VarIDGenerator.createID());
            elementGroup.addTriplePattern(new Triple(target.asNode(), NodeFactory.createURI(arg.getProperty().toString()), selector.asNode()));

            elementGroup.addElementFilter(new ElementFilter(new E_IsLiteral(new ExprVar(selector))));

            return selector;
        } else {
            throw new IllegalStateException("Argument of function isLiteral has to be a PropertySelector");
        }
    }
}
