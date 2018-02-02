package com.github.anno4j.model.impl;

import com.github.anno4j.model.namespaces.Anno4jNS;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.rio.RDFFormat;

/**
 * Class to implement RDF in order to create a baseline for every object that we use in Anno4j.
 */
@Iri(Anno4jNS.RESOURCE)
public interface ResourceObject extends RDFObject {

    /**
     * Returns a textual representation of the resource in a supported serialisation format including all triples
     * having the given resource as subject. This method is equivalent to {@link #getTriples(RDFFormat, int, Resource...)}
     * with a maximum path length of one exporting from the default graph.
     * @see #getTriples(RDFFormat, int, Resource...)
     * @param format The format the result should have.
     * @return A textual representation if this object in the format.
     */
    String getTriples(RDFFormat format);

    /**
     * Returns a textual representation of the resource in a supported serialisation format including all triples
     * within a certain distance. The graph(s) of the connected repository are traversed starting at the given resource
     * including all triples in the result being part of a path with a maximum length of {@code maxPathLength} (if positive).
     * The result includes inferred triples if available.
     * @param format The format the result should have.
     * @param maxPathLength Maximum length of paths to include in the result. If this argument is negative the result will contain
     *                      the connected component containing the given resource.
     * @param contexts The context(s) to get data from. A value of {@code null} corresponds to the default graph.
     * @return Returns a textual representation of the RDF data in the requested format.
     * @throws RepositoryException Thrown if an error occurs while accessing the repository or exporting triples from it.
     * @throws IllegalArgumentException Thrown if {@code maxPathLength} is zero.
     */
    String getTriples(RDFFormat format, int maxPathLength, Resource... contexts) throws RepositoryException;

    void setResource(Resource resource) throws MalformedQueryException, RepositoryException, UpdateExecutionException;

    void setResourceAsString(String resourceAsString) throws RepositoryException, MalformedQueryException, UpdateExecutionException;

    String getResourceAsString();

    void delete();
}
