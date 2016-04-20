package com.github.anno4j.recommendation.computation;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.recommendation.RecommendationTestSetup;
import com.github.anno4j.recommendation.impl.TestBody1;
import com.github.anno4j.recommendation.impl.TestBody2;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import com.github.anno4j.recommendation.impl.TestBody3;
import com.github.anno4j.recommendation.model.Similarity;
import com.github.anno4j.recommendation.model.SimilarityAlgorithmRDF;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Manu on 08/04/16.
 */
public class SimilarityImplTest extends RecommendationTestSetup {

    private final static String NS = "http://somepage.com#";
    private final static String BODY_URI1 = NS + "TestBody1";
    private final static String BODY_URI2 = NS + "TestBody2";

    private final static String ALGORITHM_NAME = "algo";
    private final static String ALGORITHM_ID = "id";

    @Test
    public void testRoleMapper() throws RepositoryException {

        ObjectFactory fac = this.anno4j.getObjectRepository().getConnection().getObjectFactory();

        URI uri = fac.getNameOf(TestBody1.class);

        assertEquals(BODY_URI1, uri.toString());
    }

    @Test
    public void testSimpleSimilarityAlgorithm() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, IllegalAccessException, InstantiationException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody2.class);

        algo.calculateSimilarities();

        assertEquals(4, algo.getCounter());
    }

    @Test
    public void testSimilarityStatementCreation() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException, ParseException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody2.class);

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

    @Test
    public void testEmptyBodyAlgorithm() throws IllegalAccessException, MalformedQueryException, RepositoryException, ParseException, InstantiationException, QueryEvaluationException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody3.class);

        algo.calculateSimilarities();

        // There should be no statements created, as there are no annotations with TestBody3
        List<SimilarityStatement> result = this.queryService.execute(SimilarityStatement.class);

        assertEquals(0, result.size());
    }

    @Test
    public void testDoublyStartedAlgorithm() throws IllegalAccessException, MalformedQueryException, RepositoryException, ParseException, InstantiationException, QueryEvaluationException {

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody2.class);

        algo.calculateSimilarities();
        algo.calculateSimilarities();

        List<SimilarityStatement> result = this.queryService.execute(SimilarityStatement.class);

        assertEquals(8, result.size());
    }

    @Test
    public void testProvenance() throws IllegalAccessException, MalformedQueryException, RepositoryException, ParseException, InstantiationException, QueryEvaluationException {
        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody2.class);

        algo.calculateSimilarities();

        List<SimilarityStatement> result = this.queryService.execute(SimilarityStatement.class);

        assertEquals(4, result.size());

        SimilarityStatement statement = result.get(0);

        Similarity similarity = statement.getSimilarity();
        Set<URI> uris = similarity.getBodies();

        ObjectFactory fac = this.anno4j.getObjectRepository().getConnection().getObjectFactory();
        URI uri1 = fac.getNameOf(TestBody1.class);
        URI uri2 = fac.getNameOf(TestBody2.class);

        assertTrue(uris.contains(uri1));
        assertTrue(uris.contains(uri2));

        SimilarityAlgorithmRDF algorithm = similarity.getAlgorithm();
        assertEquals(ALGORITHM_NAME, algorithm.getAlgorithmName());
        assertEquals(ALGORITHM_ID, algorithm.getAlgorithmID());
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