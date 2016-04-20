package com.github.anno4j.recommendation.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;

import java.util.Set;

/**
 * Created by Manu on 05/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY)
public interface Similarity extends ResourceObject {

    @Iri(ANNO4JREC.HAS_SIMILARITY_CLASS)
    Set<URI> getBodies();

    @Iri(ANNO4JREC.HAS_SIMILARITY_CLASS)
    void setBodies(Set<URI> bodies);

    void addBodyURI(URI bodyURI);

    void addBodyURIAsString(String body);

    @Iri(ANNO4JREC.HAS_ALGORITHM)
    SimilarityAlgorithmRDF getAlgorithm();

    @Iri(ANNO4JREC.HAS_ALGORITHM)
    void setAlgorithm(SimilarityAlgorithmRDF algorithm);
}
