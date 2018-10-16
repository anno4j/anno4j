package com.github.anno4j.io;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ObjectParser class to parse annotations from different serializations, e.g.
 * JSONLD, Turtle, ... A parsed Annotation object is ONLY PERSISTED in the LOCAL Anno4j instance.
 */
public class ObjectParser {

    /**
     * The Anno4j object that receives the parsed triples.
     */
    private Anno4j anno4j;

    /**
     * Basic constructor, which sets up all the necessary repositories.
     *
     * @throws RepositoryException Thrown if an error occurred accessing the created repository.
     * @throws RepositoryConfigException Thrown if an error occurred configuring the new repository.
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
     *
     * @throws RepositoryException      Thrown if no connection to the object repository could be made.
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
     * Does also call the clear() method, which removes all statements from the local Anno4j instance.
     *
     * @throws RepositoryException Thrown if an error occurs while accessing the repository.
     */
    public void shutdown() throws RepositoryException, UpdateExecutionException {
        // Add anno4j shutdown method here.
        this.clear();
        this.anno4j.getObjectRepository().getConnection().close();
        this.anno4j.getRepository().getConnection().close();
    }

    /**
     * Returns all instances of the given type (and its subtypes) that are present in this parsers Anno4j instance.
     * @param type An {@link org.openrdf.annotations.Iri}-annotated type that all returned objects must have.
     * @param <T> The type of the returned objects.
     * @return Returns all instances present in the Anno4j connected triplestore having the given {@code type}.
     * Doesn't contain duplicates.
     * @throws RepositoryException Thrown if an error occurs accessing the connected triplestore.
     */
    private <T extends ResourceObject> List<T> getInstancesOfType(Class<? extends T> type) throws RepositoryException {
        List<T> instances = new ArrayList<>();
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        try {
            instances.addAll(connection.getObjects(type).asSet());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }

        return instances;
    }

    /**
     * Parses the given RDF-document that is in the specified {@code format} and stores all triples in the
     * repository of the local Anno4j instance.
     * @param content The RDF-serialization to read from. This must be in the format specified by {@code format}.
     * @param documentURL The base URL used for namespaces.
     * @param format The format of the given RDF-serialization.
     * @throws RepositoryException Thrown if an error occurred accessing the connected triplestore of the underlying
     * Anno4j instance.
     * @throws RDFParseException Thrown if an error occurs while parsing the RDF-document.
     */
    private void readRDF(String content, URL documentURL, RDFFormat format) throws RDFParseException, RepositoryException {
        // Parse the document using RIO parser. All read statements will be inserted into the Anno4j connected triplestore:
        RDFParser parser = Rio.createParser(format);
        try {
            StatementSailHandler handler = new StatementSailHandler(this.anno4j.getRepository().getConnection());
            parser.setRDFHandler(handler);
            byte[] bytes = content.getBytes("UTF-8");
            try (InputStream stream = new ByteArrayInputStream(bytes)) {
                parser.parse(stream, documentURL.toString());
            }
        } catch (RDFHandlerException | IOException e) {
            throw new RDFParseException(e);
        }
    }

    /**
     * Used to parse {@link Annotation}-objects from a the RDF-document supplied.
     * All annotations found are returned as a list.
     * For performance reasons, the local memorystore can be cleared by defining the boolean parameter as true.
     * <br>
     * <b>Important:</b> This method is deprecated and will be removed in future versions.
     * Use the method {@link #parse(Class, String, URL, RDFFormat, boolean)} instead.
     *
     * @param content     The String representation of the textcontent.
     * @param documentURL The basic URL used for namespaces.
     * @param format      The format of the given serialization. Needs to be
     *                    supported of an instance of RDFFormat.
     * @param clear       Determines, if the local Anno4j instance should be cleared or not in the beginning.
     * @return The annotations contained in the given RDF-document or an empty list if an error occurred.
     * The result is not guaranteed to be duplicate free.
     */
    @Deprecated
    public List<Annotation> parse(String content, URL documentURL, RDFFormat format, boolean clear) {
        // Clear all triples in the triplestore if requested:
        if (clear) {
            try {
                clear();
            } catch (RepositoryException | UpdateExecutionException e) {
                e.printStackTrace();
            }
        }

        // Read the RDF-document and store triples in the repository of the anno4j instance:
        try {
            readRDF(content, documentURL, format);
        } catch (RDFParseException | RepositoryException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        // Get the annotations
        List<Annotation> annotations = new LinkedList<>();
        try {
            annotations = anno4j.getObjectRepository().getConnection().getObjects(Annotation.class).asList();
        } catch (QueryEvaluationException | RepositoryException e) {
            e.printStackTrace();
        }

        return annotations;
    }

    /**
     * Parses the given RDF-document and returns all resources of the specified type.
     * In contrary to {@link #parse(String, URL, RDFFormat, boolean)} the returned objects are duplicate free.
     *
     * @param type An {@link org.openrdf.annotations.Iri}-annotated type that all returned objects must have.
     * @param content The RDF-serialization to read from. This must be in the format specified by {@code format}.
     * @param documentURL The base URL used for namespaces.
     * @param format The format of the given RDF-serialization.
     * @param clear Determines, if the local Anno4j instance should be cleared or not in the beginning.
     *              This can be set to increase performance.
     * @param <T> The type of the returned objects.
     * @return Returns all resources having the given type as their {@code rdf:type}. This result will also contain
     * all objects parsed since the underlying Anno4j instance was last cleared.
     * @throws RepositoryException Thrown if an error occurred accessing the connected triplestore of the underlying
     * Anno4j instance.
     * @throws RDFParseException Thrown if an error occurs while parsing the RDF-document.
     */
    public <T extends ResourceObject> List<T> parse(Class<? extends T> type, String content, URL documentURL, RDFFormat format, boolean clear) throws RepositoryException, RDFParseException {
        // Clear all triples in the triplestore if requested:
        if (clear) {
            try {
                clear();
            } catch (UpdateExecutionException e) {
                throw new RepositoryException(e);
            }
        }

        // Read the RDF-document and store triples in the repository of the anno4j instance:
        readRDF(content, documentURL, format);

        // Get the instances of the requested type:
        List<T> instances = getInstancesOfType(type);

        return instances;
    }
}
