package com.github.anno4j.similarity.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.similarity.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;

/**
 * Created by Manu on 12/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY_ALGORITHM)
public interface SimilarityAlgorithmRDF extends ResourceObject {

    @Iri(ANNO4JREC.HAS_ALGORITHM_NAME)
    void setAlgorithmName(String name);

    @Iri(ANNO4JREC.HAS_ALGORITHM_NAME)
    String getAlgorithmName();

    @Iri(ANNO4JREC.HAS_ALGORITHM_ID)
    void setAlgorithmID(String algorithmID);

    @Iri(ANNO4JREC.HAS_ALGORITHM_ID)
    String getAlgorithmID();
}
