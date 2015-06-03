package com.github.anno4j.querying.ldpath;

import org.apache.marmotta.ldpath.model.selectors.PathSelector;

public class EvalPathSelector {

    /**
     * Evaluates the PathSelector and creates the corresponding SPARQL statement.
     *
     * @param pathSelector The PropertySelector to evaluate
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate(PathSelector pathSelector, StringBuilder query, String variableName) {
        String leftVarName = LDPathEvaluator.evaluate(pathSelector.getLeft(), query, variableName);
        return LDPathEvaluator.evaluate(pathSelector.getRight(), query, leftVarName);
    }
}
