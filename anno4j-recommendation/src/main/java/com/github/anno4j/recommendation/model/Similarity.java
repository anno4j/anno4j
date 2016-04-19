package com.github.anno4j.recommendation.model;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.recommendation.computation.*;
import com.github.anno4j.recommendation.computation.SimilarityAlgorithm;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Created by Manu on 05/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY_MEASURE)
public interface Similarity extends ResourceObject {

    @Iri(ANNO4JREC.HAS_SIMILARITY_CLASS)
    Set<Body> getBodies();

    @Iri(ANNO4JREC.HAS_SIMILARITY_CLASS)
    void setBodies(Set<Body> bodies);

    void addBody(Body body);

    @Iri(ANNO4JREC.HAS_ALGORITHM)
    SimilarityAlgorithm getAlgorithm();

    @Iri(ANNO4JREC.HAS_ALGORITHM)
    void setAlgorithm(SimilarityAlgorithm algorithm);
}
