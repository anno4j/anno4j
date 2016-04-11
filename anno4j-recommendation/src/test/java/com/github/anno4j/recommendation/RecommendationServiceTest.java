package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.impl.TestBody1;
import com.github.anno4j.recommendation.impl.TestBody2;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.*;

/**
 * Created by Manu on 11/04/16.
 */
public class RecommendationServiceTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    private final static String ALGORITM_NAME = "algo1";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testAlgorithmRegistration() {
        RecommendationService rs = new RecommendationService(this.anno4j);

        assertEquals(0, rs.getAlgorithms().size());

        // TODO get resource of a class?

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, "http://somepage.com#TestBody1", "http://somepage.com#TestBody2");

        rs.addAlgorithm(ALGORITM_NAME, algo);

        assertEquals(1, rs.getAlgorithms().size());

        rs.removeAlgorithm(ALGORITM_NAME);

        assertEquals(0, rs.getAlgorithms().size());
    }

    @Test
    public void testAlgorithmRunning() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        RecommendationService rs = new RecommendationService(this.anno4j);

        // TODO get resource of a class?

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, "http://somepage.com#TestBody1", "http://somepage.com#TestBody2");

        rs.addAlgorithm(ALGORITM_NAME, algo);

        createTestAnnotations();

        rs.useSingleAlgorithm(ALGORITM_NAME);

        QueryService qs = this.anno4j.createQueryService();
        qs.addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);
        qs.addCriteria("oa:hasBody[is-a arec:SimilarityStatement]");

        assertEquals(4, qs.execute().size());

        rs.useAllAlgorithms();

        assertEquals(8, qs.execute().size());
    }

    private void createTestAnnotations() throws RepositoryException, InstantiationException, IllegalAccessException {
        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody1 body = this.anno4j.createObject(TestBody1.class);

            if(i == 0) {
                body.setValue("test");
            }

            anno.setBody(body);

            this.anno4j.persist(anno);
        }

        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody2 body = this.anno4j.createObject(TestBody2.class);

            anno.setBody(body);

            this.anno4j.persist(anno);
        }
    }
}