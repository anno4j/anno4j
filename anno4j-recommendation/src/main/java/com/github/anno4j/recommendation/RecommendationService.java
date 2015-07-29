package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.target.SpecificResource;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.openrdf.repository.RepositoryException;

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

    /**
     * Basic constructor.
     */
    public RecommendationService() {
        this.algorithms = new HashMap<String, SimilarityAlgorithm>();
    };

    /**
     * Constructor also setting the algorithms.
     * @param algorithms    The map of algorithms.
     */
    public RecommendationService(HashMap<String, SimilarityAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    public void generateSimilarity(Annotation subject, Annotation object, String algorithmName) {
        SimilarityAlgorithm algorithm = this.algorithms.get(algorithmName);

        double similarity = algorithm.calculateSimilarity(subject, object);

        Annotation anno = createSimilarityAnnotation(subject, object, similarity);

        try {
            Anno4j.getInstance().createPersistenceService().persistAnnotation(anno);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void generateAllSimilarities(Annotation subject, Annotation object) {
        Iterator iterator = this.algorithms.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            generateSimilarity(subject, object, (String) pair.getKey());
        }
    }

    private Annotation createSimilarityAnnotation(Annotation subject, Annotation object, double similarity) {
        Annotation similarityAnnotation = new Annotation();

        SpecificResource specificResource = new SpecificResource();
        specificResource.setSource(subject);
        similarityAnnotation.setTarget(specificResource);

        SimilarityStatement statement = new SimilarityStatement(subject, object, similarity);
        similarityAnnotation.setBody(statement);

        return similarityAnnotation;
    }

    /**
     * Method to register a new algorithm.
     * @param key       The key for the new algorithm.
     * @param algorithm The instance of the algorithm.
     */
    public void addAlgorithm(String key, SimilarityAlgorithm algorithm) {
        this.algorithms.put(key, algorithm);
    }

    /**
     * Method to remove an algorithm from the registered map.
     * @param key The key
     */
    public void removeAlgorithm(String key) {
        this.algorithms.remove(key);
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
