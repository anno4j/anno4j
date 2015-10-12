package com.github.anno4j.persistence.impl;

import com.github.anno4j.model.Annotation;
import org.apache.commons.io.FileUtils;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.*;
import org.openrdf.sail.memory.MemoryStore;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * ObjectParser class to parse annotations from different serializations, e.g.
 * JSONLD, Turtle, ... A parsed Annotation object is NOT PERSISTED in the
 * corresponding Anno4j.
 */
public class ObjectParser {

    private SailRepository sailRepository;
    private SailRepositoryConnection sailConnection;
    private ObjectRepository objectRepository;
    private ObjectConnection objectConnection;

    /**
     * Basic constructor, which sets up all the necessary repositories.
     *
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public ObjectParser() throws RepositoryException, RepositoryConfigException {
        this.sailRepository = new SailRepository(new MemoryStore());
        this.sailRepository.initialize();
        this.objectRepository = new ObjectRepositoryFactory().createRepository(sailRepository);
        this.sailConnection = sailRepository.getConnection();
        this.objectConnection = objectRepository.getConnection();
    }

    /**
     * Shutdown method, closing all repositories and corresponding connection
     * objects.
     *
     * @throws RepositoryException
     */
    public void shutdown() throws RepositoryException {
        this.sailConnection.close();
        this.objectConnection.close();
        this.objectRepository.shutDown();
        this.sailRepository.shutDown();
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
            results = objectConnection.getObjects(Annotation.class).asList();
        } catch (QueryEvaluationException | RepositoryException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Used to parse a given text content, supported in a given serialization
     * format.
     *
     * @param content The String representation of the textcontent.
     * @param documentURL The basic URL used for namespaces.
     * @param format The format of the given serialization. Needs to be
     * supported of an instance of RDFFormat.
     * @return A list of annotations
     */
    public List<Annotation> parse(String content, URL documentURL, RDFFormat format) {
        File file;
        RDFParser parser = Rio.createParser(format);
        try {

            StatementSailHandler handler = new StatementSailHandler(sailConnection);

            parser.setRDFHandler(handler);
            byte[] bytes = content.getBytes("UTF-8");
            try (InputStream stream = new ByteArrayInputStream(bytes)) {
                parser.parse(stream, documentURL.toString());
            }
        } catch (RDFHandlerException | RDFParseException | IOException e) {
            e.printStackTrace();
        }

        return getAnnotations();
    }
}
