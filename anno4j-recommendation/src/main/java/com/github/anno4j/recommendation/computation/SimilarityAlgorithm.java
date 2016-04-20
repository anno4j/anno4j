package com.github.anno4j.recommendation.computation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.model.Similarity;
import com.github.anno4j.recommendation.model.SimilarityAlgorithmRDF;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;

import java.util.List;

/**
 * Abstract class for an algorithm that calculates the similarity between two given annotations by supporting their respective body.
 * A subclass of this abstract class requires the implementation of the calculateSimilarity(...) method.
 * The respective results will be stored in the associated Anno4j instance.
 */
public abstract class SimilarityAlgorithm {

    private Anno4j anno4j;

    private String name;
    private String id;

    private Class clazz1;
    private Class clazz2;

    /**
     * Constructor for a SimilarityAlgorithm, setting the following fields:
     *
     * @param anno4j    Associated Anno4j instance, which will contain calculated results.
     * @param name      Name of the algorithm.
     * @param id        ID of the algorithm.
     * @param clazz1    First java body class.
     * @param clazz2    Second java body class.
     */
    public SimilarityAlgorithm(Anno4j anno4j, String name, String id, Class clazz1, Class clazz2) {
        this.anno4j = anno4j;
        this.name = name;
        this.id = id;
        this.clazz1 = clazz1;
        this.clazz2 = clazz2;
    }

    /**
     * Method to compute all the similarities between the pairs of annotations that contain the supported body classes clazz1 and clazz2.
     *
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ParseException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void compute() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {

        // Create an ObjectFactory to get the associated URIs of the body classes
        ObjectFactory factory = this.anno4j.getObjectRepository().getConnection().getObjectFactory();

        URI clazz1URI = factory.getNameOf(this.clazz1);
        URI clazz2URI = factory.getNameOf(this.clazz2);

        // Query the given Anno4j instance for all Annotations having the first body class
        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + clazz1URI + ">]");

        List<Annotation> annotations1 = qs.execute();

        // Query the given Anno4j instance for all Annotations having the first body class
        qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + clazz2URI + ">]");

        List<Annotation> annotations2 = qs.execute();

        // Compute similarity between every pair of found annotations
        for(Annotation anno1 : annotations1) {
            for(Annotation anno2 : annotations2) {
                // Calculated similarity between the two given annotations
                double similarityValue = calculateSimilarity(anno1, anno2);

                // Create provenance
                Similarity similarity = createSimilarityProvenance(clazz1URI, clazz2URI);

                // Create similarity annotation
                Annotation similarityAnnotation = createSimilarityAnnotation(anno1, anno2, similarityValue, similarity);

                this.anno4j.persist(similarity);
                this.anno4j.persist(similarityAnnotation);
            }
        }
    }

    /**
     * Abstract method that calculates the similarity between two given annotations.
     *
     * @param anno1 First annotation for a similarity pair.
     * @param anno2 Second annotation for a similarity pair.
     * @return      Similarity between the two given annotations as a double value.
     */
    protected abstract double calculateSimilarity(Annotation anno1, Annotation anno2);

    /**
     * Method to create the reification node that expresses the similarity between two annotations, supported as subject and object.
     *
     * @param subject           First annotation for a similarity pair.
     * @param object            Second annotation for a similarity pair.
     * @param similarityValue   Double value of the similarity.
     * @param similarity        The RDF similarity java class.
     * @return                  An annotation, containing the SimilarityStatement as body.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Annotation createSimilarityAnnotation(Annotation subject, Annotation object, double similarityValue, Similarity similarity) throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation similarityAnnotation = anno4j.createObject(Annotation.class);

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSource(subject);
        similarityAnnotation.addTarget(specificResource);

        SimilarityStatement statement = anno4j.createObject(SimilarityStatement.class);
        statement.setSubject(subject);
        statement.setObject(object);
        statement.setSimilarityValue(similarityValue);
        statement.setSimilarity(similarity);
        similarityAnnotation.setBody(statement);

        return similarityAnnotation;
    }

    /**
     * Method creating the provenance information of this algorithm. Stores the information as RDF. A SimilarityStatement
     * refers to a Similarity Object as provenance information.
     *
     * @param bodyIRI1                  IRI of the first body class.
     * @param bodyIRI2                  IRI of the second body class.
     * @return                          Similarity object that contains all provenance information.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Similarity createSimilarityProvenance(URI bodyIRI1, URI bodyIRI2) throws RepositoryException, IllegalAccessException, InstantiationException {
        Similarity similarity = this.anno4j.createObject(Similarity.class);

        similarity.addBodyURI(bodyIRI1);
        similarity.addBodyURI(bodyIRI2);

        SimilarityAlgorithmRDF algo = this.anno4j.createObject(SimilarityAlgorithmRDF.class);
        algo.setAlgorithmName(this.name);
        algo.setAlgorithmID(this.id);

        similarity.setAlgorithm(algo);

        return similarity;
    }

    /**
     * Gets id.
     *
     * @return Value of id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets new id.
     *
     * @param id New value of id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return Value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new name.
     *
     * @param name New value of name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
