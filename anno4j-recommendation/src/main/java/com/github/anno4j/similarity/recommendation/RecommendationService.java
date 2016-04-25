package com.github.anno4j.similarity.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.similarity.ontologies.ANNO4JREC;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.LinkedList;
import java.util.List;

/**
 * Class represents a suite to generate similarity annotations. Several algorithms can be registered and then used
 * according to two supported RDF objects, representing the subject and the object.
 */
public class RecommendationService {

    private Anno4j anno4j;

    public RecommendationService(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

    public List<Annotation> findSimilarAnnotations(Annotation annotation) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> similarAnnotations = new LinkedList<Annotation>();

        QueryService qs = this.anno4j.createQueryService();
        qs.addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);

        qs.addCriteria("^rdf:object[is-a arec:SimilarityStatement]/rdf:subject", annotation.getResourceAsString());

        similarAnnotations.addAll(qs.execute(Annotation.class));

        qs = this.anno4j.createQueryService();
        qs.addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);

        qs.addCriteria("^rdf:subject[is-a arec:SimilarityStatement]/rdf:object", annotation.getResourceAsString());

        similarAnnotations.addAll(qs.execute(Annotation.class));

        return similarAnnotations;
    }

}
