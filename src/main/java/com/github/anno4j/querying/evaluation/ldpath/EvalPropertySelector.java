package com.github.anno4j.querying.evaluation.ldpath;

import com.github.anno4j.querying.evaluation.VarIDGenerator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.model.selectors.PropertySelector;
import org.apache.marmotta.ldpath.model.selectors.WildcardSelector;


public class EvalPropertySelector {

    /**
     * Evaluates the PropertySelector and creates the corresponding SPARQL statement.
     *
     * @param propertySelector The PropertySelector to evaluate
     * @param elementGroup     ElementGroup containing the actual query parts
     * @param variable         The latest created variable
     * @return the latest referenced variable name
     */
    public static Var evaluate(PropertySelector propertySelector, ElementGroup elementGroup, Var variable) {

        if (propertySelector instanceof WildcardSelector) {
            throw new IllegalStateException(propertySelector.getClass() + " is not supported.");
        }

        Var id = Var.alloc(VarIDGenerator.createID());
        elementGroup.addTriplePattern(new Triple(variable.asNode(), NodeFactory.createURI(propertySelector.getProperty().toString()), id.asNode()));

        return id;
    }
}
