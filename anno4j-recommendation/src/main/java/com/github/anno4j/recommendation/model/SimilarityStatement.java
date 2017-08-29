package com.github.anno4j.recommendation.model;

import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

/**
 * Extends a statement, automatically setting the predicate.
 */
@Iri(ANNO4JREC.SIMILARITY_STATEMENT)
public interface SimilarityStatement extends Statement {

    /**
     * Gets The value of similarity.
     *
     * @return Value of The value of similarity.
     */
    @Iri(ANNO4JREC.SIMILARITY)
    public double getSimilarity();

    /**
     * Sets new The value of similarity.
     *
     * @param similarity New value of The value of similarity.
     */
    @Iri(ANNO4JREC.SIMILARITY)
    public void setSimilarity(double similarity);
}
