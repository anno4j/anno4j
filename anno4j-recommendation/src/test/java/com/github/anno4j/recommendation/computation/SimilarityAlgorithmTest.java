package com.github.anno4j.recommendation.computation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;

/**
 * Created by Manu on 08/04/16.
 */
public class SimilarityAlgorithmTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    private final static String NS = "http://somepage.com#";
    private final static String BODY_URI1 = NS + "Body1";
    private final static String BODY_URI2 = NS + "Body2";

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
    public void testSimpleSimilarityAlgorithm() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, IllegalAccessException, InstantiationException {
        createTestAnnotations();

        SimpleSimilarityAlgorithm algo = new SimpleSimilarityAlgorithm(this.anno4j, BODY_URI1, BODY_URI2);

        algo.calculateAllSimilarities();

        assertEquals(4, algo.getCounter());
    }

    private void createTestAnnotations() throws RepositoryException, InstantiationException, IllegalAccessException {
        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            Body1 body = this.anno4j.createObject(Body1.class);

            anno.setBody(body);

            this.anno4j.persist(anno);
        }

        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            Body2 body = this.anno4j.createObject(Body2.class);

            anno.setBody(body);

            this.anno4j.persist(anno);
        }
    }

    @Iri(BODY_URI1)
    public interface Body1 extends Body {
    }

    @Iri(BODY_URI2)
    public interface Body2 extends Body {
    }
}