package com.github.anno4j.recommendation.computation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.model.Similarity;
import com.github.anno4j.recommendation.model.SimilarityMeasure;
import com.github.anno4j.recommendation.model.SimilarityStatement;
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

    public void calculateSimilarities() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + this.bodyIRI1 + ">]");

        List<Annotation> annotations1 = qs.execute();

        qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + this.bodyIRI2 + ">]");

        List<Annotation> annotations2 = qs.execute();

        for(Annotation anno1 : annotations1) {
            for(Annotation anno2 : annotations2) {
                double similarity = calculateSimilarity(anno1, anno2);

                Annotation similarityAnnotation = createSimilarityAnnotation(anno1, anno2, similarity);
                this.anno4j.persist(similarityAnnotation);

                // Create provenance
            }
        }

    }

    protected abstract double calculateSimilarity(Annotation anno1, Annotation anno2);

    private Annotation createSimilarityAnnotation(Annotation subject, Annotation object, double similarity) throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation similarityAnnotation = anno4j.createObject(Annotation.class);

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSource(subject);
        similarityAnnotation.addTarget(specificResource);

        SimilarityStatement statement = anno4j.createObject(SimilarityStatement.class);
        statement.setSubject(subject);
        statement.setObject(object);
        statement.setValue(similarity);
        similarityAnnotation.setBody(statement);

        return similarityAnnotation;
    }
}
