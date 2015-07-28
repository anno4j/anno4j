package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.model.selectors.GroupedSelector;

import java.util.ArrayList;

public class EvalGroupedSelector {

    /**
     * Evaluates the LDPath GroupSelector
     *
     * @param groupedSelector The GroupSelector to evaluate
     * @param elementGroup  ElementGroup containing the actual query parts
     * @param variable The latest created variable
     * @return the latest referenced variable
     */
    public static Var evaluate(GroupedSelector groupedSelector, ElementGroup elementGroup, Var variable) {
        ElementGroup newGroup = new ElementGroup();
        elementGroup.addElement(newGroup);
        return LDPathEvaluator.evaluate(groupedSelector.getContent(), newGroup, variable);
    }
}
