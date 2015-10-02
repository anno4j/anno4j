package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrMore1;
import com.hp.hpl.jena.sparql.path.P_ZeroOrOne;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.RecursivePathSelector;

public class EvalRecursivePathSelector {
    public static Var evaluate(RecursivePathSelector recursivePathSelector, ElementGroup elementGroup, Var variable) {
        Var id = Var.alloc(VarIDGenerator.createID());
        ElementPathBlock epb = new ElementPathBlock();
        String pathExpression = recursivePathSelector.getPathExpression(new SesameValueBackend());
        /**
         * initial pathExpression contains:
         *      (<http://www.w3.org/ns/oa#hasBody> / <http://www.example.com/schema#recursiveBodyValue>)*
         *
         * Because P_ZeroOrMore and so one, creates the same expression we have to strip the redundant chars.
         */
        String strippedPathExpression = pathExpression.substring(2, pathExpression.length() -3 );

        TriplePath triplePath = null;
        if (pathExpression.contains("*")) {
            triplePath = new TriplePath(variable.asNode(), new P_ZeroOrMore1(new P_Link(NodeFactory.createURI(strippedPathExpression))), id.asNode());
        } else if (pathExpression.contains("+")) {
            triplePath = new TriplePath(variable.asNode(), new P_OneOrMore1(new P_Link(NodeFactory.createURI(strippedPathExpression))), id.asNode());
        } else {
            throw new IllegalStateException("Only ZeroOrMorePath(*), OneOrMorePath(+) path selectors are currently supported.");
        }

        epb.addTriple(triplePath);
        ElementGroup group = new ElementGroup();
        group.addElement(epb);
        elementGroup.addElement(group);
        return id;
    }
}
