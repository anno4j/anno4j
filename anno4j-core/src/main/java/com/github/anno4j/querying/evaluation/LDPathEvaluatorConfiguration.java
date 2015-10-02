package com.github.anno4j.querying.evaluation;

import com.github.anno4j.querying.extension.QueryEvaluator;
import com.hp.hpl.jena.graph.Node;
import org.apache.marmotta.ldpath.api.functions.NodeFunction;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;

import java.util.Map;


public class LDPathEvaluatorConfiguration {

    private Map<Class<? extends TestFunction>, Class<QueryEvaluator>> testFunctionEvaluators;

    private Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> defaultEvaluators;

    private Map<Class<? extends NodeTest>, Class<QueryEvaluator>> testEvaluators;

    private Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> functionEvaluators;

    public Map<Class<? extends TestFunction>, Class<QueryEvaluator>> getTestFunctionEvaluators() {
        return testFunctionEvaluators;
    }

    public void setTestFunctionEvaluators(Map<Class<? extends TestFunction>, Class<QueryEvaluator>> testFunctionEvaluators) {
        this.testFunctionEvaluators = testFunctionEvaluators;
    }

    public Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> getDefaultEvaluators() {
        return defaultEvaluators;
    }

    public void setDefaultEvaluators(Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> defaultEvaluators) {
        this.defaultEvaluators = defaultEvaluators;
    }

    public Map<Class<? extends NodeTest>, Class<QueryEvaluator>> getTestEvaluators() {
        return testEvaluators;
    }

    public void setTestEvaluators(Map<Class<? extends NodeTest>, Class<QueryEvaluator>> testEvaluators) {
        this.testEvaluators = testEvaluators;
    }

    public Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> getFunctionEvaluators() {
        return functionEvaluators;
    }

    public void setFunctionEvaluators(Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> functionEvaluators) {
        this.functionEvaluators = functionEvaluators;
    }
}
