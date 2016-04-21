package com.github.anno4j.similarity.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.HashSet;

/**
 * Support class for a Similarity node, implementing abstract methods.
 */
@Partial
public abstract class SimilaritySupport extends ResourceObjectSupport implements Similarity {

    @Override
    public void addBodyURIAsString(String body) {
        if(this.getBodies().isEmpty()) {
            this.setBodies(new HashSet<URI>());
        }

        this.getBodies().add(new URIImpl(body));
    }

    @Override
    public void addBodyURI(URI bodyURI) {
        if(this.getBodies().isEmpty()) {
            this.setBodies(new HashSet<URI>());
        }

        this.getBodies().add(bodyURI);
    }
}
