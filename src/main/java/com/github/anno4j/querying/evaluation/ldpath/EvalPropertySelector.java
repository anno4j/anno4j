package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.VarIDGenerator;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.PropertySelector;

public class EvalPropertySelector {

    /**
     * Evaluates the PropertySelector and creates the corresponding SPARQL statement.
     *
     * @param propertySelector The PropertySelector to evaluate
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate(PropertySelector propertySelector, StringBuilder query, String variableName) {
        String id = VarIDGenerator.createID();
        query
                .append("?")
                .append(variableName)
                .append(" ")
                .append(propertySelector.getPathExpression(new SesameValueBackend()))
                .append(" ?")
                .append(id)
                .append(" .")
                .append(System.getProperty("line.separator"));

        return id;
    }
}
