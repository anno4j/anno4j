package com.github.anno4j.querying.evaluation.ldpath;

import org.apache.marmotta.ldpath.model.selectors.GroupedSelector;

public class EvalGroupedSelector {

    /**
     * Evaluates the complex GroupedSelector.
     *
     * @param groupedSelector The GroupedSelector to evaluate
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate (GroupedSelector groupedSelector, StringBuilder query, String variableName) {
        query
                .append("{")
                .append(System.getProperty("line.separator"));

        String newVarname = LDPathEvaluator.evaluate(groupedSelector.getContent(), query, variableName);

        query
                .append("} ")
                .append(System.getProperty("line.separator"));

        return newVarname;
    }
}
