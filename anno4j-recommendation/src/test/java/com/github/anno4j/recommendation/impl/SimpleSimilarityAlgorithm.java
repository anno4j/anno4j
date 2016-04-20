package com.github.anno4j.recommendation.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.computation.SimilarityAlgorithm;

/**
 * A simple similarity algorithm, implementing {@link SimilarityAlgorithm}
 */
public class SimpleSimilarityAlgorithm extends SimilarityAlgorithm {

    private int counter;

    public SimpleSimilarityAlgorithm(Anno4j anno4j, String name, String id, Class clazz1, Class clazz2) {
        super(anno4j, name, id, clazz1, clazz2);
        this.counter = 0;
    }

    @Override
    public double calculateSimilarity(Annotation anno1, Annotation anno2) {
        this.counter++;

        TestBody1 body = (TestBody1) anno1.getBody();

        if(body.getValue() != null && body.getValue().equals("test")) {
            return 0.0;
        }

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
