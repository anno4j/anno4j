package com.github.anno4j.similarity.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.similarity.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;

import java.util.Set;

/**
 * Represents an RDF node for a Similarity, which is used as provenance holder for a SimilarityAlgorithm.
 * Links to the body classes, that are utilised in the similarity calculation and a SimilarityAlgorithm node.
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
