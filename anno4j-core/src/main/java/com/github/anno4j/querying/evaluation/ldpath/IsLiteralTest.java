package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.graph.Node;
import org.apache.marmotta.ldpath.api.backend.RDFBackend;
import org.apache.marmotta.ldpath.api.functions.TestFunction;

import java.util.Collection;

/**
 *
 */
public class IsLiteralTest<Node> extends TestFunction<Node> {
    @Override
    public String getLocalName() {
        return "isLiteral";
    }

    @Override
    public Boolean apply(RDFBackend<Node> backend, Node context, Collection<Node>... args) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getSignature() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
