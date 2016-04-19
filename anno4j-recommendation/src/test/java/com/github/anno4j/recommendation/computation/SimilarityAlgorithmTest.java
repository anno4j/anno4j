package com.github.anno4j.recommendation.computation;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.RecommendationTestSetup;
import com.github.anno4j.recommendation.impl.TestBody1;
import com.github.anno4j.recommendation.impl.TestBody2;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Manu on 08/04/16.
 */
public class SimilarityAlgorithmTest extends RecommendationTestSetup {

    private final static String NS = "http://somepage.com#";
    private final static String BODY_URI1 = NS + "TestBody1";
    private final static String BODY_URI2 = NS + "TestBody2";

    @Test
    public void testSimpleSimilarityAlgorithm() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, IllegalAccessException, InstantiationException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, BODY_URI1, BODY_URI2);

        algo.calculateSimilarities();

        assertEquals(4, algo.getCounter());
    }

    @Test
    public void testSimilarityStatementCreation() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException, ParseException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, BODY_URI1, BODY_URI2);

        algo.calculateSimilarities();

        this.queryService.addCriteria("oa:hasBody[is-a arec:SimilarityStatement]");

        List<Annotation> similarityAnnotations = this.queryService.execute();
        assertEquals(4, similarityAnnotations.size());

        boolean foundZeroEquality = false;
        for(Annotation anno : similarityAnnotations) {
            if(((SimilarityStatement) anno.getBody()).getSimilarityValue() == 0.0) {
                foundZeroEquality = true;
                break;
            }
        }

        assertTrue(foundZeroEquality);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody2 body = this.anno4j.createObject(TestBody2.class);

            anno.setBody(body);

            this.anno4j.persist(anno);
        }

        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody1 body = this.anno4j.createObject(TestBody1.class);

            if(i == 0) {
                body.setValue("test");
            }

            anno.setBody(body);

            this.anno4j.persist(anno);
        }

    }
}