package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.persistence.PersistenceService;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Suite to test the {@link com.github.anno4j.recommendation.RecommendationService}
 */
public class RecommendationServiceTest {

    public final static String SOME_PAGE = "http://example.org/";

    QueryService<Annotation> queryService;
    PersistenceService persistenceService;

    @Before
    public void setUp() throws Exception {
        this.queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        this.queryService.addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);
        this.persistenceService = Anno4j.getInstance().createPersistenceService();
    }

    @After
    public void tearDown() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
    }

    @Test
    public void testRecommendationService() throws QueryEvaluationException, RepositoryException, ParseException, MalformedQueryException {
        // Create a new RecommendationService
        RecommendationService recommendationService = new RecommendationService();

        // Check if there are no algorithms registered yet
        assertEquals(0, recommendationService.getAlgorithms().size());

        // Register a new algorithm
        final String algorithmName = "algo1";
        recommendationService.addAlgorithm(algorithmName, new SimpleSimilarityAlgorithm());

        // Check for one registered algorithm
        assertEquals(1, recommendationService.getAlgorithms().size());

        // Create two random annotations, which will be used as being similar
        Annotation anno1 = new Annotation();
        Annotation anno2 = new Annotation();

        // Check if the current repository has no SimilarityStatements yet
        List<Annotation> result = this.queryService.setBodyCriteria("[is-a rdf:Statement]").execute();
        assertEquals(0, result.size());

        // Generate the similarity between the two annotations, using the algorithm with name algo1
        recommendationService.generateSimilarity(anno1, anno2, algorithmName);

        // Query for the SimilarityObjects again
        result = this.queryService.execute();
        assertEquals(1, result.size());

        // Check if the similarity value is correct (1, as the SimpleAlgorithm always returns 1)
        Annotation annotation = result.get(0);
        SimilarityStatement statement = (SimilarityStatement) annotation.getBody();
        assertEquals(new Double(1.0), (Double) statement.getSimilarity());
    }

    @Test
    public void testRecommendationServiceWithAllAlgorithms() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        // Create a new RecommendationService
        RecommendationService recommendationService = new RecommendationService();

        // Check if there are no algorithms registered yet
        assertEquals(0, recommendationService.getAlgorithms().size());

        // Register a new algorithm
        final String algorithmName = "algo1";
        final String algorithmName2 = "algo2";
        recommendationService.addAlgorithm(algorithmName, new SimpleSimilarityAlgorithm());
        recommendationService.addAlgorithm(algorithmName2, new SimpleSimilarityAlgorithm());

        // Check for one registered algorithm
        assertEquals(2, recommendationService.getAlgorithms().size());

        // Create two random annotations, which will be used as being similar
        Annotation anno1 = new Annotation();
        Annotation anno2 = new Annotation();

        // Check if the current repository has no SimilarityStatements yet
        List<Annotation> result = this.queryService.setBodyCriteria("[is-a rdf:Statement]").execute();
        assertEquals(0, result.size());

        // Generate the similarity between the two annotations, using the algorithm with name algo1
        recommendationService.generateAllSimilarities(anno1, anno2);

        // Query for the SimilarityObjects again
        result = this.queryService.execute();
        assertEquals(2, result.size());

        // Check if the similarity value is correct (1, as the SimpleAlgorithm always returns 1)
        Annotation annotation = result.get(0);
        SimilarityStatement statement = (SimilarityStatement) annotation.getBody();
        assertEquals(new Double(1.0), (Double) statement.getSimilarity());

        // Check if the second similarity value is correct (1, as the SimpleAlgorithm always returns 1)
        Annotation annotation2 = result.get(1);
        SimilarityStatement statement2 = (SimilarityStatement) annotation2.getBody();
        assertEquals(new Double(1.0), (Double) statement2.getSimilarity());
    }
}
