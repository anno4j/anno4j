package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.*;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.*;
import org.openrdf.model.impl.LiteralImpl;

public class EvalTestingSelector {

    /**
     * Evaluates the TestingSelector.
     *
     * @param testingSelector The TestingSelector to evaluate
     * @param elementGroup    ElementGroup containing the actual query parts
     * @param variable        The latest created variable
     * @return the latest referenced variable name
     */
    public static Var evaluate(TestingSelector testingSelector, ElementGroup elementGroup, Var variable) {
        NodeTest nodeTest = testingSelector.getTest();
        Var delVar = LDPathEvaluator.evaluate(testingSelector.getDelegate(), elementGroup, variable);

        if (nodeTest instanceof OrTest) {
            OrTest orTest = (OrTest) nodeTest;

            Expr expr1 = evaluateTestType(orTest.getLeft(), elementGroup, delVar);
            Expr expr2 = evaluateTestType(orTest.getRight(), elementGroup, delVar);

            elementGroup.addElementFilter(new ElementFilter(new E_LogicalOr(expr1, expr2)));
        } else if (nodeTest instanceof AndTest) {
            AndTest andTest = (AndTest) nodeTest;

            Expr expr1 = evaluateTestType(andTest.getLeft(), elementGroup, delVar);
            Expr expr2 = evaluateTestType(andTest.getRight(), elementGroup, delVar);

            elementGroup.addElementFilter(new ElementFilter(new E_LogicalAnd(expr1, expr2)));
        } else {
            elementGroup.addElementFilter(new ElementFilter(evaluateTestType(nodeTest, elementGroup, delVar)));
        }
        return delVar;
    }

    private static Expr evaluateTestType(NodeTest nodeTest, ElementGroup elementGroup, Var var) {
        if (nodeTest instanceof IsATest) {
            IsATest isATest = (IsATest) nodeTest;
            Var tmpVar = Var.alloc(Var.alloc(VarIDGenerator.createID()));
            elementGroup.addTriplePattern(new Triple(var.asNode(), RDF.type.asNode(), tmpVar.asNode()));
            return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createURI(isATest.getPathExpression(new SesameValueBackend()).replace("<", "").replace(">", "").replaceFirst("is-a ", ""))));
        } else if (nodeTest instanceof LiteralTypeTest) {
            LiteralTypeTest literalTypeTest = (LiteralTypeTest) nodeTest;
            return new E_Equals(new E_Datatype(new ExprVar(var)), new E_URI(new NodeValueString(literalTypeTest.getTypeUri().toString())));
        } else if (nodeTest instanceof LiteralLanguageTest) {
            LiteralLanguageTest literalLanguageTest = (LiteralLanguageTest) nodeTest;
            return new E_LangMatches(new E_Lang(new ExprVar(var)), new NodeValueString(literalLanguageTest.getLang()));
        } else if (nodeTest instanceof PathEqualityTest) {
            PathEqualityTest pathEqualityTest = (PathEqualityTest) nodeTest;
            Var tmpVar =  LDPathEvaluator.evaluate(pathEqualityTest.getPath(), elementGroup, var);
            if(pathEqualityTest.getNode() instanceof org.openrdf.model.impl.LiteralImpl) {
                return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createLiteral(((LiteralImpl) pathEqualityTest.getNode()).getLabel().toString())));
            } else {
                return new E_Equals(new ExprVar(tmpVar.asNode()), new NodeValueNode(NodeFactory.createURI(pathEqualityTest.getNode().toString())));
            }
        } else {
            throw new IllegalStateException(nodeTest.getClass() + " is not supported.");
        }
    }
}
