package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.PathEqualityTest;
import org.openrdf.model.impl.LiteralImpl;

@Evaluator(PathEqualityTest.class)
public class PathEqualityTestEvaluator implements TestEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        TestingSelector testingSelector = (TestingSelector) nodeSelector;
        NodeTest nodeTest = testingSelector.getTest();
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, var, evaluatorConfiguration);

        elementGroup.addElementFilter(new ElementFilter(evaluate(nodeTest, elementGroup, delVar, evaluatorConfiguration)));
        return var;
    }

    @Override
    public Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        PathEqualityTest pathEqualityTest = (PathEqualityTest) nodeTest;
        Var tmpVar =  LDPathEvaluator.evaluate(pathEqualityTest.getPath(), elementGroup, var, evaluatorConfiguration);
        if(pathEqualityTest.getNode() instanceof org.openrdf.model.impl.LiteralImpl) {
            return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createLiteral(((LiteralImpl) pathEqualityTest.getNode()).getLabel().toString())));
        } else {
            return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createURI(pathEqualityTest.getNode().toString())));
        }
    }
}
