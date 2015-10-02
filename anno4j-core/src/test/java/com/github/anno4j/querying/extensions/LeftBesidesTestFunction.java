package com.github.anno4j.querying.extensions;

import org.apache.marmotta.ldpath.api.backend.RDFBackend;
import org.apache.marmotta.ldpath.api.functions.TestFunction;

import java.util.Collection;

public class LeftBesidesTestFunction<Node> extends TestFunction<Node> {

    @Override
    public String getLocalName() {
        return "leftBesides";
    }

    @Override
    public Boolean apply(RDFBackend<Node> rdfBackend, Node node, Collection<Node>... collections) throws IllegalArgumentException {
        return true;
    }

    @Override
    public String getSignature() {
        return "fn:"+getLocalName()+"(Node a, Node b) :: Boolean";
    }

    @Override
    public String getDescription() {
        return "Tests if a node is left beside another node.";
    }
}
