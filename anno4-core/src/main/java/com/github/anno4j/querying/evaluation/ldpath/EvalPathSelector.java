package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.model.selectors.PathSelector;

import java.util.ArrayList;

public class EvalPathSelector {

    public static Var evaluate(PathSelector pathSelector, ElementGroup elementGroup, Var variable) {
        Var leftVar = LDPathEvaluator.evaluate(pathSelector.getLeft(), elementGroup, variable);
        return LDPathEvaluator.evaluate(pathSelector.getRight(), elementGroup, leftVar);
    }
}
