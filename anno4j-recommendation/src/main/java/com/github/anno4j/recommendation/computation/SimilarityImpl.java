package com.github.anno4j.recommendation.computation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.model.Similarity;
import com.github.anno4j.recommendation.model.SimilarityAlgorithm;
import com.github.anno4j.recommendation.model.SimilarityStatement;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.managers.RoleMapper;

import java.util.List;

/**
 * Interface for an algorithm that calculates the similarity between two given annotations.
 */
public abstract class SimilarityImpl {

    private Anno4j anno4j;

    private String name;
    private String id;

    private Class clazz1;
    private Class clazz2;

    public SimilarityImpl(Anno4j anno4j, String name, String id, Class clazz1, Class clazz2) {
        this.anno4j = anno4j;
        this.name = name;
        this.id = id;
        this.clazz1 = clazz1;
        this.clazz2 = clazz2;
    }

    public void calculateSimilarities() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {

        ObjectFactory factory = this.anno4j.getObjectRepository().getConnection().getObjectFactory();

        URI clazz1URI = factory.getNameOf(this.clazz1);
        URI clazz2URI = factory.getNameOf(this.clazz2);

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + clazz1URI + ">]");

        List<Annotation> annotations1 = qs.execute();

        qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody[is-a <" + clazz2URI + ">]");

        List<Annotation> annotations2 = qs.execute();

        for(Annotation anno1 : annotations1) {
            for(Annotation anno2 : annotations2) {
                double similarityValue = calculateSimilarity(anno1, anno2);

                // Create similarity annotation
                Annotation similarityAnnotation = createSimilarityAnnotation(anno1, anno2, similarityValue);
                this.anno4j.persist(similarityAnnotation);

                // Create provenance
                Similarity similarity = createSimilarityProvenance(clazz1URI, clazz2URI);
                this.anno4j.persist(similarity);
            }
        }

    }

    protected abstract double calculateSimilarity(Annotation anno1, Annotation anno2);

    private Annotation createSimilarityAnnotation(Annotation subject, Annotation object, double similarity) throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation similarityAnnotation = anno4j.createObject(Annotation.class);

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSource(subject);
        similarityAnnotation.addTarget(specificResource);

        SimilarityStatement statement = anno4j.createObject(SimilarityStatement.class);
        statement.setSubject(subject);
        statement.setObject(object);
        statement.setSimilarityValue(similarity);
        similarityAnnotation.setBody(statement);

        return similarityAnnotation;
    }

    private Similarity createSimilarityProvenance(URI bodyIRI1, URI bodyIRI2) throws RepositoryException, IllegalAccessException, InstantiationException {
        Similarity similarity = this.anno4j.createObject(Similarity.class);

        similarity.addBodyURI(bodyIRI1);
        similarity.addBodyURI(bodyIRI2);

        SimilarityAlgorithm algo = this.anno4j.createObject(SimilarityAlgorithm.class);

        similarity.setAlgorithm(algo);

        return similarity;
    }
}
