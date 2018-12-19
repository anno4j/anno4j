package com.github.anno4j.querying.objectqueries;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
import org.openrdf.rio.RDFFormat;

import java.util.LinkedList;
import java.util.List;

public class ObjectQueryService {

//    private Anno4j anno4j;

    private ObjectConnection connection;

    private ObjectQueryServiceConfiguration configuration;

    public ObjectQueryService(ObjectConnection connection) throws RepositoryConfigException, RepositoryException {
        this.configuration = new ObjectQueryServiceConfiguration();
//        this.anno4j = anno4j;
        this.connection = connection;
    }

    public <T extends ResourceObject> T createObject(Class<T> clazz) throws RepositoryException, IllegalAccessException, InstantiationException {
        T object = this.configuration.getAnno4j().createObject(clazz);
        this.configuration.getElements().add((ResourceObject) object);

        return object;
    }

    public void setPivot(ResourceObject object) {
        this.configuration.setPivot(object);
    }

    public List<ResourceObject> execute() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ResourceObject pivot = this.configuration.getPivot();

        String sparql = prepareSparqlWithPivot(pivot);

//        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        ObjectQuery query = connection.prepareObjectQuery(sparql);
        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<ResourceObject> objects = new LinkedList<>();
        for(RDFObject rdf : result.asList()) {
            objects.add((ResourceObject) rdf);
        }

        return objects;
    }

    private String prepareSparqlWithPivot(ResourceObject pivot) {
        String sparql = "SELECT ?x0\n" +
                 "WHERE { \n" +
                 pivot.getTriples(RDFFormat.NTRIPLES) +
                 "}";

        int counter = 0;
        for(ResourceObject object : this.configuration.getElements()) {
            sparql = sparql.replace("<" + object.getResourceAsString() + ">", "?x" + counter);

            counter++;
        }

        return sparql;
    }
}
