package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.target.SpecificResource;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    /**
     * Basic constructor.
     */
    public RecommendationService(Anno4j anno4j) {
        this.algorithms = new HashMap<String, SimilarityAlgorithm>();
        this.anno4j = anno4j;
    }

    /**
     * Constructor also setting the algorithms.
     *
     * @param algorithms The map of algorithms.
     */
    public RecommendationService(HashMap<String, SimilarityAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    public void generateSimilarity(Annotation subject, Annotation object, String algorithmName) {
        SimilarityAlgorithm algorithm = this.algorithms.get(algorithmName);

        double similarity = algorithm.calculateSimilarity(subject, object);

        try {
            Annotation anno = createSimilarityAnnotation(subject, object, similarity);
            this.anno4j.getObjectRepository().getConnection().addObject(anno);
        } catch (RepositoryException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateAllSimilarities(Annotation subject, Annotation object) {
        Iterator iterator = this.algorithms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            generateSimilarity(subject, object, (String) pair.getKey());
        }
    }

    private Annotation createSimilarityAnnotation(Annotation subject, Annotation object, double similarity) throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation similarityAnnotation = anno4j.createObject(Annotation.class);

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSource(subject);
        similarityAnnotation.addTarget(specificResource);

        SimilarityStatement statement = anno4j.createObject(SimilarityStatement.class);
        statement.setSubject(subject);
        statement.setObject(object);
        statement.setSimilarity(similarity);
        similarityAnnotation.setBody(statement);

        return similarityAnnotation;
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
