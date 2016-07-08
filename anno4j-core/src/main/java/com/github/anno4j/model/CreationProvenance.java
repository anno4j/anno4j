package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.DCTERMS;
import org.openrdf.annotations.Iri;

/**
 * Interface for the Body and Target class, introducing provenance information: created and creator properties
 */
public interface CreationProvenance extends ResourceObject {

    @Iri(DCTERMS.CREATOR)
    Agent getCreator();

    @Iri(DCTERMS.CREATOR)
    void setCreator(Agent agent);

    @Iri(DCTERMS.CREATED)
    String getCreated();

    @Iri(DCTERMS.CREATED)
    void setCreated(String created);

    void setCreated(int year, int month, int day, int hours, int minutes, int seconds);

    @Iri(DCTERMS.MODIFIED)
    String getModified();

    @Iri(DCTERMS.MODIFIED)
    void setModified(String modification);

    void setModified(int year, int month, int day, int hours, int minutes, int seconds);
}
