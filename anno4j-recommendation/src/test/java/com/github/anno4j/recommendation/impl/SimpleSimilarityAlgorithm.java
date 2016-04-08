package com.github.anno4j.recommendation.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.computation.SimilarityAlgorithm;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple similarity algorithm, implementing {@link SimilarityAlgorithm}
 */
public class SimpleSimilarityAlgorithm extends SimilarityAlgorithm {

    private int counter;

    public SimpleSimilarityAlgorithm(Anno4j anno4j, String bodyIRI1, String bodyIRI2) {
        super(anno4j, bodyIRI1, bodyIRI2);
        this.counter = 0;
    }

    @Override
    public double calculateSimilarity(Annotation anno1, Annotation anno2) {
        this.counter++;
        return 1.0;
    }

    /**
     * Gets counter.
     *
     * @return Value of counter.
     */
    public int getCounter() {
        return counter;
    }
}
