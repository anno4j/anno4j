package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.PathSelector;

@Evaluator(PathSelector.class)
public class PathSelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        PathSelector pathSelector = (PathSelector) nodeSelector;
        Var leftVar = LDPathEvaluator.evaluate(pathSelector.getLeft(), elementGroup, var, evaluatorConfiguration);
        return LDPathEvaluator.evaluate(pathSelector.getRight(), elementGroup, leftVar, evaluatorConfiguration);
    }
}
