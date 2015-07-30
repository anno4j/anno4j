package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.*;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.IsATest;
import org.apache.marmotta.ldpath.model.tests.LiteralLanguageTest;
import org.apache.marmotta.ldpath.model.tests.LiteralTypeTest;

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
        if (nodeTest instanceof IsATest) {
            IsATest isATest = (IsATest) nodeTest;
            elementGroup.addTriplePattern(new Triple(delVar.asNode(), RDF.type.asNode(), NodeFactory.createURI(isATest.getPathExpression(new SesameValueBackend()).replace("<", "").replace(">", "").replaceFirst("is-a ", ""))));
        } else if (nodeTest instanceof LiteralLanguageTest) {
            LiteralLanguageTest languageTest = (LiteralLanguageTest) nodeTest;
            elementGroup.addElementFilter(new ElementFilter(new E_LangMatches(new E_Lang(new ExprVar(delVar)), new NodeValueString(languageTest.getLang()))));
        } else if (nodeTest instanceof LiteralTypeTest) {
            LiteralTypeTest literalTypeTest = (LiteralTypeTest) nodeTest;
            elementGroup.addElementFilter(new ElementFilter(new E_Equals(new E_Datatype(new ExprVar(delVar)), new E_URI(new NodeValueString(literalTypeTest.getTypeUri().toString())))));
        } else {
            throw new IllegalStateException(nodeTest.getClass() + " is not supported.");
        }
        return delVar;
    }
}
