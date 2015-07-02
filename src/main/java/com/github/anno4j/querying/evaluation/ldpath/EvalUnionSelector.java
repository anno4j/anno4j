package com.github.anno4j.querying.evaluation.ldpath;

import org.apache.marmotta.ldpath.model.selectors.UnionSelector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalUnionSelector {

    /**
     * Evaluates an UnionSelector. More precisely, it creates the UNION part of the SPARQL query.
     *
     * @param unionSelector The UnionSelector to evaluate
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate(UnionSelector unionSelector, StringBuilder query, String variableName) {
        query
                .append("{")
                .append(System.getProperty("line.separator"));

        String leftVarName = LDPathEvaluator.evaluate(unionSelector.getLeft(), query, variableName);

        query
                .append("} UNION {")
                .append(System.getProperty("line.separator"));

        String rightVarName = LDPathEvaluator.evaluate(unionSelector.getRight(), query, variableName);

        query
                .append("}")
                .append(System.getProperty("line.separator"));

        // Both subtrees have to refer to the same variable name
        replaceAll(query, Pattern.compile("\\?" + rightVarName), "?" + leftVarName);

        return leftVarName;
    }

    /**
     * String replace function on a StringBuilder.
     *
     * @param sb          The StringBuilder
     * @param pattern     The pattern
     * @param replacement The replacement String
     */
    private static void replaceAll(StringBuilder sb, Pattern pattern, String replacement) {
        Matcher m = pattern.matcher(sb);
        while (m.find()) {
            sb.replace(m.start(), m.end(), replacement);
        }
    }
}
