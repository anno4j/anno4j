package com.github.anno4j.similarity.recommendation;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.similarity.SimilarityTestSetup;
import com.github.anno4j.similarity.impl.SimpleSimilarityAlgorithm;
import com.github.anno4j.similarity.impl.TestBody1;
import com.github.anno4j.similarity.impl.TestBody2;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Manu on 11/04/16.
 */
public class RecommendationServiceTest extends SimilarityTestSetup {

    private final static String ALGORITHM_NAME = "algo";
    private final static String ALGORITHM_ID = "id";

    @Test
    public void testFindSimilarAnnotations() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, IllegalAccessException, InstantiationException {
        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, ALGORITHM_NAME, ALGORITHM_ID, TestBody1.class, TestBody2.class);

        algo.compute();

        List<Annotation> annotations = this.queryService.execute(Annotation.class);

        RecommendationService rs = new RecommendationService(this.anno4j);

        List<Annotation> similarAnnotations = rs.findSimilarAnnotations(annotations.get(0));

        assertEquals(2, similarAnnotations.size());
    }

    @Override
    protected void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {

        this.createBaseLineSimilarityAnnotations();
    }
}