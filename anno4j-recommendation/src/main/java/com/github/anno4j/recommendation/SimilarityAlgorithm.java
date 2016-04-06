package com.github.anno4j.recommendation;

import com.github.anno4j.model.Annotation;

/**
 * Interface for an algorithm that calculates the similarity between two given annotations.
 */
public abstract class SimilarityAlgorithm {

    

    public abstract double calculateSimilarity(Annotation anno1, Annotation anno2);
}
