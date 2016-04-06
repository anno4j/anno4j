package com.github.anno4j.recommendation.model;

import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;

/**
 * Created by Manu on 06/04/16.
 */
@Iri(ANNO4JREC.SIMILARITY_OPERATOR)
public interface SimilarityOperator {

    @Iri(ANNO4JREC.HAS_CLASS)
    void setClass(String clazz);

    @Iri(ANNO4JREC.HAS_CLASS)
    String getClazz();

    @Iri(ANNO4JREC.HAS_FIELD)
    void setField(Resource resource);

    @Iri(ANNO4JREC.HAS_FIELD)
    Resource getField();
}
