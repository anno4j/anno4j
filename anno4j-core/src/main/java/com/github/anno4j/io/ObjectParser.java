package com.github.anno4j.io;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
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

        return getAnnotations();
    }
}
