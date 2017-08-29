package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.model.selectors.*;
import org.apache.marmotta.ldpath.model.tests.FunctionTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Function to transform LDPath to SPARQL. Recursively splits the LDPath expression into the
 * separate parts and evaluate them. More specifically it creates the SPARQL query portions
 * for each considered part, using the Jena ARQ query engine
 */
public class LDPathEvaluator {

    private final static Logger logger = LoggerFactory.getLogger(LDPathEvaluator.class);

    /**
     * Function to transform LDPath to SPARQL. Recursively splits the LDPath expression into the
     * separate parts and evaluate them. More specifically it creates the SPARQL query portions
     * for each considered part, using the Jena ARQ query engine
     *
     * @param nodeSelector The current NodeSelector of the LDPath
     * @param elementGroup ElementGroup containing the actual query parts
     * @param variable     The latest created variable
     * @return the latest referenced variable
     * @see <a href="https://jena.apache.org/documentation/query/">https://jena.apache.org/documentation/query/</a>
     */
    public static Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var variable, LDPathEvaluatorConfiguration evaluatorConfiguration) {

        Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> defaultEvaluators = evaluatorConfiguration.getDefaultEvaluators();
        Map<Class<? extends TestFunction>, Class<QueryEvaluator>> testFunctionEvaluators = evaluatorConfiguration.getTestFunctionEvaluators();
        Map<Class<? extends NodeTest>, Class<TestEvaluator>> testEvaluators = evaluatorConfiguration.getTestEvaluators();
        Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> functionEvaluators = evaluatorConfiguration.getFunctionEvaluators();

        try {
            if (defaultEvaluators.containsKey(nodeSelector.getClass())) {
                return defaultEvaluators.get(nodeSelector.getClass()).newInstance().evaluate(nodeSelector, elementGroup, variable, evaluatorConfiguration);
            } else if (nodeSelector instanceof TestingSelector) {
                TestingSelector testingSelector = (TestingSelector) nodeSelector;

                if (testingSelector.getTest() instanceof FunctionTest) {
                    FunctionTest functionTest = (FunctionTest) testingSelector.getTest();

                    if (testFunctionEvaluators.containsKey(functionTest.getTest().getClass())) {
                        return testFunctionEvaluators.get(functionTest.getTest().getClass()).newInstance().evaluate(nodeSelector, elementGroup, variable, evaluatorConfiguration);
                    } else {
                        throw new IllegalStateException("No FunctionTest evaluator for " + functionTest.getClass().getCanonicalName());
                    }
                } else {
                    NodeTest nodeTest = testingSelector.getTest();

                    if (testEvaluators.containsKey(nodeTest.getClass())) {
                        return testEvaluators.get(nodeTest.getClass()).newInstance().evaluate(nodeSelector, elementGroup, variable, evaluatorConfiguration);
                    } else {
                        throw new IllegalStateException("No NodeTest evaluator for " + nodeTest.getClass().getCanonicalName());
                    }
                }
            } else if (nodeSelector instanceof FunctionSelector) {
                FunctionSelector functionSelector = (FunctionSelector) nodeSelector;

                if (functionEvaluators.containsKey(functionSelector.getFunction().getClass())) {
                    return functionEvaluators.get(functionSelector.getFunction().getClass()).newInstance().evaluate(nodeSelector, elementGroup, variable, evaluatorConfiguration);
                } else {
                    throw new IllegalStateException("No Function evaluator found for " + functionSelector.getClass().getCanonicalName());
                }
            } else {
                throw new IllegalStateException(nodeSelector.getClass() + " is not supported.");
            }
        } catch (Exception e) {
            logger.error("{}", e);
            throw new IllegalStateException("Could not instantiate evaluator for " + nodeSelector.getClass());
        }
    }

    public static Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var variable, LDPathEvaluatorConfiguration evaluatorConfiguration) {

        Map<Class<? extends NodeTest>, Class<TestEvaluator>> testEvaluators = evaluatorConfiguration.getTestEvaluators();

        try {
            if (testEvaluators.containsKey(nodeTest.getClass())) {
                return testEvaluators.get(nodeTest.getClass()).newInstance().evaluate(nodeTest, elementGroup, variable, evaluatorConfiguration);
            } else {
                throw new IllegalStateException("No NodeTest evaluator found for " + nodeTest.getClass().getCanonicalName());
            }
        } catch (InstantiationException e) {
            logger.error("{}", e);
            throw new IllegalStateException("Could not instantiate evaluator for NodeTest " + nodeTest.getClass());
        } catch (IllegalAccessException e) {
            logger.error("{}", e);
            throw new IllegalStateException("Could not instantiate evaluator for NodeTest, because of missing access " + nodeTest.getClass());
        }
    }
}
