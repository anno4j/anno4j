package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;

/**
 * Class represents a suite to generate similarity annotations. Several algorithms can be registered and then used
 * according to two supported RDF objects, representing the subject and the object.
 */
public class RecommendationService {

    private Anno4j anno4j;

    public RecommendationService(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

}
