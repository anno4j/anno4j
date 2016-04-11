package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.recommendation.computation.SimilarityAlgorithm;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class represents a suite to generate similarity annotations. Several algorithms can be registered and then used
 * according to two supported RDF objects, representing the subject and the object.
 */
public class RecommendationService {

    /**
     * A map of registered algorithms for this RecommendationService
     */
    private Map<String, SimilarityAlgorithm> algorithms;

    private Anno4j anno4j;

    public RecommendationService() throws RepositoryConfigException, RepositoryException {
        this.algorithms = new HashMap<String, SimilarityAlgorithm>();
        this.anno4j = new Anno4j();
    }

    public RecommendationService(Anno4j anno4j) {
        this.algorithms = new HashMap<String, SimilarityAlgorithm>();
        this.anno4j = anno4j;
    }

    public RecommendationService(HashMap<String, SimilarityAlgorithm> algorithms) throws RepositoryConfigException, RepositoryException {
        this.algorithms = algorithms;
        this.anno4j = new Anno4j();
    }

    public RecommendationService(HashMap<String, SimilarityAlgorithm> algorithms, Anno4j anno4j) {
        this.algorithms = algorithms;
        this.anno4j = anno4j;
    }

    /**
     * Method to register a new algorithm.
     *
     * @param key       The key for the new algorithm.
     * @param algorithm The instance of the algorithm.
     */
    public void addAlgorithm(String key, SimilarityAlgorithm algorithm) {
        this.algorithms.put(key, algorithm);
    }

    /**
     * Method to remove an algorithm from the registered map.
     *
     * @param key The key
     */
    public void removeAlgorithm(String key) {
        this.algorithms.remove(key);
    }

    public void useSingleAlgorithm(String algorithm) throws IllegalAccessException, MalformedQueryException, RepositoryException, ParseException, InstantiationException, QueryEvaluationException {
        Iterator iterator = this.algorithms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            if(pair.getKey().equals(algorithm)) {
                ((SimilarityAlgorithm) pair.getValue()).calculateSimilarities();
            }
        }
    }

    public void useAllAlgorithms() throws IllegalAccessException, MalformedQueryException, RepositoryException, ParseException, InstantiationException, QueryEvaluationException {
        Iterator iterator = this.algorithms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            ((SimilarityAlgorithm) pair.getValue()).calculateSimilarities();
        }
    }

    /**
     * Gets A map of registered algorithms for this RecommendationService.
     *
     * @return Value of A map of registered algorithms for this RecommendationService.
     */
    public Map<String, SimilarityAlgorithm> getAlgorithms() {
        return algorithms;
    }

    /**
     * Sets new A map of registered algorithms for this RecommendationService.
     *
     * @param algorithms New value of A map of registered algorithms for this RecommendationService.
     */
    public void setAlgorithms(Map<String, SimilarityAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }
}
