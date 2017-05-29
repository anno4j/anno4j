package com.github.anno4j.querying.extension;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;

import java.util.Map;

public interface QueryEvaluator {
    Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration);
}
