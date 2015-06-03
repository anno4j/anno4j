package com.github.anno4j.querying.ldpath;

import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.*;

public class LDPathEvaluator {

    /**
     * Function to transform LDPath to SPARQL. Recursively splits the LDPath expression into the
     * separate parts and evaluate them. More specifically it creates the SPARQL query portions
     * for each considered part.
     *
     * @param nodeSelector The current NodeSelector of the LDPath
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate(NodeSelector nodeSelector, StringBuilder query, String variableName) {
        if (nodeSelector instanceof PropertySelector) {
            return EvalPropertySelector.evaluate((PropertySelector) nodeSelector, query, variableName);
        } else if (nodeSelector instanceof PathSelector) {
            return EvalPathSelector.evaluate((PathSelector) nodeSelector, query, variableName);
        } else if (nodeSelector instanceof TestingSelector) {
            return EvalTestingSelector.evaluate((TestingSelector) nodeSelector, query, variableName);
        } else if (nodeSelector instanceof org.apache.marmotta.ldpath.model.selectors.GroupedSelector) {
            return EvalGroupedSelector.evaluate((GroupedSelector) nodeSelector, query, variableName);
        } else if (nodeSelector instanceof EvalUnionSelector) {
            return EvalUnionSelector.evaluate((UnionSelector) nodeSelector, query, variableName);
        } else {
            throw new IllegalStateException(nodeSelector.getClass() + " is not supported.");
        }
    }
}
