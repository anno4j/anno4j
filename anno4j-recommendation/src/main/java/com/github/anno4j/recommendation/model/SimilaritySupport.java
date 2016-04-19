package com.github.anno4j.recommendation.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

/**
 * Created by Manu on 05/04/16.
 */
@Partial
public abstract class SimilaritySupport extends ResourceObjectSupport implements Similarity {

    @Override
    public void addBody(Body body) {
        if(this.getBodies().isEmpty()) {
            this.setBodies(new HashSet<Body>());
        }

        this.getBodies().add(body);
    }
}
