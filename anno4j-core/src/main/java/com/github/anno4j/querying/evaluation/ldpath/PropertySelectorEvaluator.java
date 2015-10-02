package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.PropertySelector;
import org.apache.marmotta.ldpath.model.selectors.WildcardSelector;

@Evaluator(PropertySelector.class)
public class PropertySelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        PropertySelector propertySelector = (PropertySelector) nodeSelector;
        if (propertySelector instanceof WildcardSelector) {
            throw new IllegalStateException(propertySelector.getClass() + " is not supported.");
        }

        Var id = Var.alloc(VarIDGenerator.createID());
        elementGroup.addTriplePattern(new Triple(var.asNode(), NodeFactory.createURI(propertySelector.getProperty().toString()), id.asNode()));

        return id;
    }
}
