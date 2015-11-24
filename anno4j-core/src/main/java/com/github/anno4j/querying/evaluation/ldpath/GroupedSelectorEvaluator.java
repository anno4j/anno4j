package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.GroupedSelector;

/**
 * This evaluator only creates a new group, which will contain further
 * query parts.
 */
@Evaluator(GroupedSelector.class)
public class GroupedSelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        GroupedSelector groupedSelector = (GroupedSelector) nodeSelector;
        ElementGroup newGroup = new ElementGroup();
        elementGroup.addElement(newGroup);
        return LDPathEvaluator.evaluate(groupedSelector.getContent(), newGroup, var, evaluatorConfiguration);
    }
}
