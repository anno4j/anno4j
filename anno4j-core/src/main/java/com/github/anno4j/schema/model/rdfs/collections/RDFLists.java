package com.github.anno4j.schema.model.rdfs.collections;

import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Provides utility functions for working with {@link RDFList}.
 */
public class RDFLists {

    /**
     * Returns the {@code rdf:nil} list. The object is created if necessary.
     * @param connection The connection from which to retrieve the object.
     * @return Returns the list object corresponding to {@code rdf:nil}.
     * @throws RepositoryException Thrown if an error occurs while accessing the repository.
     */
    private static RDFList getNilList(ObjectConnection connection) throws RepositoryException {
        // Try to find the rdf:nil list:
        RDFList nil;
        try {
            nil = connection.findObject(RDFList.class, new URIImpl(RDF.NIL));
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
        // If it couldn't be found, create it:
        if(nil == null) {
            nil = connection.createObject(RDFList.class, new URIImpl(RDF.NIL));
        }
        return nil;
    }

    /**
     * Converts the given values to a {@link RDFList}.
     * All nodes of the created RDF list will be blank nodes.
     * @param values The list of values to convert.
     * @param connection A connection to the repository where the list will be created.
     * @return Returns a new RDF list containing the specified elements.
     * @throws RepositoryException Thrown if an error occurs while creating the RDF list.
     */
    public static <T> RDFList asRDFList(ObjectConnection connection, T... values) throws RepositoryException {
        return asRDFList(Arrays.asList(values), connection);
    }

    /**
     * Converts a Java list to a {@link RDFList}. If the given list is empty then
     * {@code rdf:nil} will be returned.
     * All nodes of the created RDF list will be blank nodes.
     * @param list The Java list to convert.
     * @param connection A connection to the repository where the list will be created.
     * @return Returns a new RDF list corresponding to the given java list.
     * @throws RepositoryException Thrown if an error occurs while creating the RDF list.
     */
    public static RDFList asRDFList(List<?> list, ObjectConnection connection) throws RepositoryException {
        return asRDFList(list, connection, null);
    }

    /**
     * Converts a Java list to a {@link RDFList}. If the given list is empty then
     * {@code rdf:nil} will be returned.
     * @param list The Java list to convert.
     * @param connection A connection to the repository where the list will be created.
     * @param rootResource If this parameter is not null then the first node of the RDF list will
     *                     receive this URI.
     * @return Returns a new RDF list corresponding to the given java list.
     * @throws RepositoryException Thrown if an error occurs while creating the RDF list.
     */
    public static RDFList asRDFList(List<?> list, ObjectConnection connection, Resource rootResource) throws RepositoryException {
        if (!list.isEmpty()) {
            Iterator<?> iterator = list.iterator();

            // Create the first node of the list:
            RDFList current = connection.createObject(RDFList.class, rootResource);
            current.setFirst(iterator.next());
            RDFList front = current;

            // Create further nodes:
            while (iterator.hasNext()) {
                RDFList rest = connection.createObject(RDFList.class);
                rest.setFirst(iterator.next());
                current.setRest(rest);

                current = rest;
            }
            // Terminate the list with rdf:nil:
            current.setRest(getNilList(connection));

            return front;
        } else {
            return getNilList(connection);
        }
    }
}
