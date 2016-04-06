package com.github.anno4j.recommendation.model;

import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

/**
 * Created by Manu on 06/04/16.
 */
public abstract class SimilarityMeasureSupport extends ResourceObjectSupport implements SimilarityMeasure {

    public void addOperator(SimilarityOperator operator) {
        if(this.getOperators().isEmpty()) {
            this.setOperators(new HashSet<SimilarityOperator>());
        }

        this.getOperators().add(operator);
    }
}
