package com.github.anno4j.io;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * ObjectParser class to parse annotations from different serializations, e.g.
 * JSONLD, Turtle, ... A parsed Annotation object is ONLY PERSISTED in the LOCAL Anno4j instance.
 */
public class ObjectParser {

    private Anno4j anno4j;

    /**
     * Basic constructor, which sets up all the necessary repositories.
     *
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public ObjectParser() throws RepositoryException, RepositoryConfigException {
        this.anno4j = new Anno4j();
        URIImpl[] motivations = new URIImpl[]{
                new URIImpl(OADM.MOTIVATION_BOOKMARKING),
                new URIImpl(OADM.MOTIVATION_CLASSIFYING),
                new URIImpl(OADM.MOTIVATION_COMMENTING),
                new URIImpl(OADM.MOTIVATION_DESCRIBING),
                new URIImpl(OADM.MOTIVATION_EDITING),
                new URIImpl(OADM.MOTIVATION_HIGHLIGHTING),
                new URIImpl(OADM.MOTIVATION_IDENTIFYING),
                new URIImpl(OADM.MOTIVATION_LINKING),
                new URIImpl(OADM.MOTIVATION_MODERATING),
                new URIImpl(OADM.MOTIVATION_QUESTIONING),
                new URIImpl(OADM.MOTIVATION_REPLYING),
                new URIImpl(OADM.MOTIVATION_TAGGING)
        };

        URIImpl obj = new URIImpl(OADM.MOTIVATION);
        URIImpl pre = new URIImpl(RDF.TYPE);

        for (URIImpl sub : motivations) {
            StatementImpl statement = new StatementImpl(sub, pre, obj);
            anno4j.getRepository().getConnection().add(statement);
        }
    }

    /**
     * Clears the Anno4j underlying triplestore.
     * This is required in order to prevent a drop in throughput while parsing.
     * @throws RepositoryException Thrown if no connection to the object repository could be made.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the clearing query.
     */
    private void clear() throws RepositoryException, UpdateExecutionException {
        String deleteUpdate = "DELETE {?s ?p ?o}\n" +
                "WHERE {?s ?p ?o}";

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update;
        try {
            update = connection.prepareUpdate(deleteUpdate);
        } catch (MalformedQueryException e) {
            e.printStackTrace();
            return;
        }

        update.execute();
    }

    /**
     * Shutdown method, closing all repositories and corresponding connection
     * objects.
     *
     * @throws RepositoryException
     */
    public void shutdown() throws RepositoryException {
//        Add anno4j shutdown method here.
        this.anno4j.getObjectRepository().getConnection().close();
        this.anno4j.getRepository().getConnection().close();
    }

    /**
     * Method to return all annotations that have been parsed with this
     * ObjectParser.
     *
     * @return A list of annotations, created by parsing (different)
     * serializations of annotations.
     */
    private List<Annotation> getAnnotations() {

        List<Annotation> results = new LinkedList<>();

        try {
            results = anno4j.getObjectRepository().getConnection().getObjects(Annotation.class).asList();
        } catch (QueryEvaluationException | RepositoryException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Used to parse a given text content, supported in a given serialization
     * format.
     * The Annotations are then returned as a list.
     * For performance reasons, the local memorystore in then cleared.
     *
     * @param content The String representation of the textcontent.
     * @param documentURL The basic URL used for namespaces.
     * @param format The format of the given serialization. Needs to be
     * supported of an instance of RDFFormat.
     * @return A list of annotations
     */
    public List<Annotation> parse(String content, URL documentURL, RDFFormat format) {
        RDFParser parser = Rio.createParser(format);
        try {
            StatementSailHandler handler = new StatementSailHandler(this.anno4j.getRepository().getConnection());
            parser.setRDFHandler(handler);
            byte[] bytes = content.getBytes("UTF-8");
            try (InputStream stream = new ByteArrayInputStream(bytes)) {
                parser.parse(stream, documentURL.toString());
            }
        } catch (RDFHandlerException | RDFParseException | IOException | RepositoryException e) {
            e.printStackTrace();
        }

        List<Annotation> annotations = getAnnotations();

        // Clear all triples in the triplestore:
        try {
            clear();
        } catch (RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        return annotations;
    }
}
