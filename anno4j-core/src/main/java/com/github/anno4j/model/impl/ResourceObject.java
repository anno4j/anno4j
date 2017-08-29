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

    String getTriples(RDFFormat format);

    void setResource(Resource resource) throws MalformedQueryException, RepositoryException, UpdateExecutionException;

    void setResourceAsString(String resourceAsString) throws RepositoryException, MalformedQueryException, UpdateExecutionException;

    String getResourceAsString();

    void delete();
}
