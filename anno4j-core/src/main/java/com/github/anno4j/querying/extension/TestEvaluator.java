package com.github.anno4j.querying.extension;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.tests.NodeTest;

public interface TestEvaluator extends QueryEvaluator {
    Expr evaluate(NodeTest nodeTest, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration);
}
