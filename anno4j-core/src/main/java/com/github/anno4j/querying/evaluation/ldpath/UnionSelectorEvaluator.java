package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.annotations.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.UnionSelector;

/**
 * Evaluates an UnionSelector. More precisely, it creates the UNION part of the SPARQL query.
 */
@Evaluator(UnionSelector.class)
public class UnionSelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        UnionSelector unionSelector = (UnionSelector) nodeSelector;

        NodeSelector nodeSelectorLeft = unionSelector.getLeft();
        NodeSelector nodeSelectorRight = unionSelector.getRight();

        ElementGroup leftGroup = new ElementGroup();
        ElementGroup rightGroup = new ElementGroup();

        Var leftVar = LDPathEvaluator.evaluate(nodeSelectorLeft, leftGroup, var, evaluatorConfiguration);
        LDPathEvaluator.evaluate(nodeSelectorRight, rightGroup, var, evaluatorConfiguration);

        ElementUnion elementUnion = new ElementUnion();
        elementUnion.addElement(leftGroup.getElements().get(0));
        elementUnion.addElement(rightGroup.getElements().get(0));

        elementGroup.addElement(elementUnion);

        return leftVar;
    }
}
