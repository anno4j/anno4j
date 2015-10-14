package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.UnionSelector;

public class EvalUnionSelector {

    /**
     * Evaluates an UnionSelector. More precisely, it creates the UNION part of the SPARQL query.
     *
     * @param unionSelector The UnionSelector to evaluate
     * @param elementGroup  ElementGroup containing the actual query parts
     * @param variable      The latest created variable
     * @return the latest referenced variable name
     */
    public static Var evaluate(UnionSelector unionSelector, ElementGroup elementGroup, Var variable) {

        NodeSelector nodeSelectorLeft = unionSelector.getLeft();
        NodeSelector nodeSelectorRight = unionSelector.getRight();

        ElementGroup leftGroup = new ElementGroup();
        ElementGroup rightGroup = new ElementGroup();

        Var leftVar = LDPathEvaluator.evaluate(nodeSelectorLeft, leftGroup, variable);
        LDPathEvaluator.evaluate(nodeSelectorRight, rightGroup, variable);

        ElementUnion elementUnion = new ElementUnion();
        elementUnion.addElement(leftGroup);
        elementUnion.addElement(rightGroup);

        elementGroup.addElement(elementUnion);

        return leftVar;
    }
}
