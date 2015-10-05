package com.github.anno4j.model.impl;

import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.RDFObject;

/**
 * Class to implement RDF in order to create a baseline for every object that we use in Anno4j.
 */
@Iri(RDFS.RESOURCE)
public interface ResourceObject extends RDFObject {
    String getNTriples();

    void setResource(Resource resource);

    void setResourceAsString(String resourceAsString);

    String getResourceAsString();
}
