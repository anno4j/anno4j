package com.github.anno4j.recommendation.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Created by Manu on 05/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY)
public interface Similarity extends ResourceObject {

    @Iri(ANNO4JREC.HAS_MEASURE)
    Set<SimilarityMeasure> getMeasures();

    @Iri(ANNO4JREC.HAS_MEASURE)
    void setMeasures(Set<SimilarityMeasure> measures);

    void addSimilarityMeasure(SimilarityMeasure measure);
}
