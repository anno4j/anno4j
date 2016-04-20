package com.github.anno4j.recommendation.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.HashSet;

/**
 * Created by Manu on 05/04/16.
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

    public void addBodyURI(URI bodyURI) {
        if(this.getBodies().isEmpty()) {
            this.setBodies(new HashSet<URI>());
        }

        this.getBodies().add(bodyURI);
    }
}
