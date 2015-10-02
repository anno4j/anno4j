package com.github.anno4j.querying.extension;

import org.apache.marmotta.ldpath.api.backend.RDFBackend;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;

import javax.xml.soap.Node;
import java.util.Collection;


public class GetSelectorFunction extends SelectorFunction<Node> {

    @Override
    protected String getLocalName() {
        return "getSelector";
    }

    @Override
    public Collection<Node> apply(RDFBackend<Node> backend, Node context, Collection<Node>... args) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getSignature() {
        return "fn:getSelector(Annotation) : Selector";
    }

    @Override
    public String getDescription() {
        return "Selects the Selector of a given annotation object.";
    }
}
