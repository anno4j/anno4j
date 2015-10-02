package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.annotation.Evaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.ReversePropertySelector;

/**
 * Evaluates the ReversePropertySelector and creates the corresponding SPARQL statement.
 */
@Evaluator(ReversePropertySelector.class)
public class ReversePropertySelectorEvaluator implements QueryEvaluator {

    @Override
    public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        ReversePropertySelector reversePropertySelector = (ReversePropertySelector) nodeSelector;
        Var id = Var.alloc(VarIDGenerator.createID());
        ElementPathBlock epb = new ElementPathBlock();
        epb.addTriple(new TriplePath(var.asNode(), new P_Inverse(new P_Link(NodeFactory.createURI(reversePropertySelector.getProperty().toString()))), id.asNode()));
        ElementGroup group = new ElementGroup();
        group.addElement(epb);
        elementGroup.addElement(group);
        return id;
    }
}
