package com.github.anno4j.recommendation.impl;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.SimilarityAlgorithm;

/**
 * A simple similarity algorithm, implementing {@link com.github.anno4j.recommendation.SimilarityAlgorithm}
 */
public class SimpleSimilarityAlgorithm extends SimilarityAlgorithm {

    @Override
    public double calculateSimilarity(Annotation anno1, Annotation anno2) {
        return 1;
    }
}
