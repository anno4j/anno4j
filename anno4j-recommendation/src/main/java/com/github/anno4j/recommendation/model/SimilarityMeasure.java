package com.github.anno4j.recommendation.model;

import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Created by Manu on 05/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY_MEASURE)
public interface SimilarityMeasure {

    @Iri(ANNO4JREC.HAS_OPERATOR)
    void setOperators(Set<SimilarityOperator> operators);

    @Iri(ANNO4JREC.HAS_OPERATOR)
    Set<SimilarityOperator> getOperators();

    void addOperator(SimilarityOperator operator);

    // TODO Predicate missing
}
