package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to https://www.w3.org/TR/rdf-schema/#ch_property
 * rdf:Property is the class of RDF properties. rdf:Property is an instance of rdfs:Class.
 */
@Iri(RDF.PROPERTY)
public interface RDFSProperty extends RDFSSchemaResource {

    @Iri(RDFS.RANGE)
    void setRange(ResourceObject range);

    @Iri(RDFS.RANGE)
    ResourceObject getRange();

    @Iri(RDFS.DOMAIN)
    void setDomain(ResourceObject domain);

    @Iri(RDFS.DOMAIN)
    ResourceObject getDomain();

    @Iri(RDFS.SUB_PROPERTY_OF)
    void setSubProperties(Set<ResourceObject> subProperties);

    @Iri(RDFS.SUB_PROPERTY_OF)
    Set<ResourceObject> getSubProperties();

    void addSubProperty(ResourceObject subProperty);
}
