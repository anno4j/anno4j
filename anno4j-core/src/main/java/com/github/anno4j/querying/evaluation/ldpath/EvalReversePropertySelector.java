package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.path.P_Inverse;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import org.apache.marmotta.ldpath.model.selectors.ReversePropertySelector;

public class EvalReversePropertySelector {

    /**
     * Evaluates the ReversePropertySelector and creates the corresponding SPARQL statement.
     *
     * @param reversePropertySelector The ReversePropertySelector to evaluate
     * @param elementGroup            ElementGroup containing the actual query parts
     * @param variable                The latest created variable
     * @return the latest referenced variable name
     */
    public static Var evaluate(ReversePropertySelector reversePropertySelector, ElementGroup elementGroup, Var variable) {
        Var id = Var.alloc(VarIDGenerator.createID());
        ElementPathBlock epb = new ElementPathBlock();
        epb.addTriple(new TriplePath(variable.asNode(), new P_Inverse(new P_Link(NodeFactory.createURI(reversePropertySelector.getProperty().toString()))), id.asNode()));
        ElementGroup group = new ElementGroup();
        group.addElement(epb);
        elementGroup.addElement(group);
        return id;
    }
}
