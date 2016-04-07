package com.github.anno4j.recommendation.impl;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.computation.SimilarityAlgorithm;

/**
 * A simple similarity algorithm, implementing {@link SimilarityAlgorithm}
 */
public class SimpleSimilarityAlgorithm extends SimilarityAlgorithm {

    @Override
    public double calculateSimilarity(Annotation anno1, Annotation anno2) {
        return 1;
    }
}
