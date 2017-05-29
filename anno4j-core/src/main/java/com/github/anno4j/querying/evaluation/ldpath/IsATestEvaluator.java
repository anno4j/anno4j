package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.github.anno4j.annotations.Evaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.IsATest;

/**
 * Creates the part of the SPARQL query that tests for a specific rdf:type.
 */
@Evaluator(IsATest.class)
public class IsATestEvaluator implements TestEvaluator {


    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        NodeTest nodeTest = testingSelector.getTest();
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, var, evaluatorConfiguration);

        IsATest isATest = (IsATest) nodeTest;
        elementGroup.addTriplePattern(new Triple(delVar.asNode(), RDF.type.asNode(), NodeFactory.createURI(isATest.getPathExpression(new SesameValueBackend()).replace("<", "").replace(">", "").replaceFirst("is-a ", ""))));

        return delVar;
    }

    @Override
    public Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        IsATest isATest = (IsATest) nodeTest;
        Var tmpVar = Var.alloc(Var.alloc(VarIDGenerator.createID()));
        elementGroup.addTriplePattern(new Triple(var.asNode(), RDF.type.asNode(), tmpVar.asNode()));
        return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createURI(isATest.getPathExpression(new SesameValueBackend()).replace("<", "").replace(">", "").replaceFirst("is-a ", ""))));
    }
}
