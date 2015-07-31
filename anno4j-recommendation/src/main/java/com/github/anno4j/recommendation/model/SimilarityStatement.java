package com.github.anno4j.recommendation.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

/**
 * Extends a statement, automatically setting the predicate.
 */
@Iri(RDF.STATEMENT)
public class SimilarityStatement extends Statement {

    /**
     * The value of similarity.
     */
    @Iri(ANNO4JREC.SIMILARITY)
    private double similarity;

    public SimilarityStatement() {};

    public SimilarityStatement(ResourceObject subject, ResourceObject object, double similarity) {
        this.setSubject(subject);
        this.setPredicateAsString(ANNO4JREC.SIMILARITY);
        this.setObject(object);
        this.similarity = similarity;
    }

    /**
     * Gets The value of similarity.
     *
     * @return Value of The value of similarity.
     */
    public double getSimilarity() {
        return similarity;
    }

    /**
     * Sets new The value of similarity.
     *
     * @param similarity New value of The value of similarity.
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
