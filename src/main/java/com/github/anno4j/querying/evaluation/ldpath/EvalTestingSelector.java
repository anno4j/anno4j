package com.github.anno4j.querying.evaluation.ldpath;

import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.TestingSelector;
import org.apache.marmotta.ldpath.model.tests.IsATest;
import org.apache.marmotta.ldpath.model.tests.LiteralLanguageTest;

public class EvalTestingSelector {

    /**
     * Evaluates the TestingSelector.
     *
     * @param testingSelector The TestingSelector to evaluate
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest referenced variable name
     */
    public static String evaluate(TestingSelector testingSelector, StringBuilder query, String variableName) {

        NodeTest nodeTest = testingSelector.getTest();
        String delVarName = LDPathEvaluator.evaluate(testingSelector.getDelegate(), query, variableName);
        if (nodeTest instanceof IsATest) {
            IsATest isATest = (IsATest) nodeTest;
            query
                    .append("?")
                    .append(delVarName)
                    .append(" ")
                    .append(isATest.getPathExpression(new SesameValueBackend()).replaceFirst("is-", ""))
                    .append(" .")
                    .append(System.getProperty("line.separator"));

            return delVarName;
        } else if (nodeTest instanceof LiteralLanguageTest) {
            LiteralLanguageTest languageTest = (LiteralLanguageTest) nodeTest;
            query
                    .append("FILTER langmatches( lang(?")
                    .append(delVarName)
                    .append("), \"")
                    .append(languageTest.getLang())
                    .append("\" ) .")
                    .append(System.getProperty("line.separator"));

            return delVarName;
        } else {
            throw new IllegalStateException(nodeTest.getClass() + " is not supported.");
        }

    }
}
