package com.github.anno4j.querying.extensions;

import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.ldpath.SelfSelectionEvaluator;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;


@Evaluator(GetSelector.class)
public class GetSelectorFunctionEvaluator implements QueryEvaluator {
    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        Var evaluate = new SelfSelectionEvaluator().evaluate(nodeSelector, elementGroup, var, evaluatorConfiguration);
        Var target = Var.alloc("target");
        Var selector = Var.alloc("selector");

        elementGroup.addTriplePattern(new Triple(evaluate.asNode(), new ResourceImpl(OADM.HAS_TARGET).asNode(), target));
        elementGroup.addTriplePattern(new Triple(target.asNode(), new ResourceImpl(OADM.HAS_SELECTOR).asNode(), selector));
        return selector;
    }
}
