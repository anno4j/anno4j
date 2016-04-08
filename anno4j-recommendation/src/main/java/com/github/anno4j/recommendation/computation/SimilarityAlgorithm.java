package com.github.anno4j.recommendation.computation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.model.Similarity;
import com.github.anno4j.recommendation.model.SimilarityMeasure;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.util.LinkedList;
import java.util.List;

/**
 * Interface for an algorithm that calculates the similarity between two given annotations.
 */
public abstract class SimilarityAlgorithm {

    private Anno4j anno4j;

    private String bodyIRI1;
    private String bodyIRI2;

    public SimilarityAlgorithm(Anno4j anno4j, String bodyIRI1, String bodyIRI2) {
        this.anno4j = anno4j;
        this.bodyIRI1 = bodyIRI1;
        this.bodyIRI2 = bodyIRI2;
    }

    public void calculateAllSimilarities() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + this.bodyIRI1 + ">]");

        List<Annotation> annotations1 = qs.execute();

        qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + this.bodyIRI2 + ">]");

        List<Annotation> annotations2 = qs.execute();

        for(Annotation anno1 : annotations1) {
            System.out.println(anno1.getTriples(RDFFormat.JSONLD));
            for(Annotation anno2 : annotations2) {
                calculateSimilarity(anno1, anno2);
            }
        }

    }

    protected abstract double calculateSimilarity(Annotation anno1, Annotation anno2);
}
