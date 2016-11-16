package com.github.anno4j.schema_parsing.model.rdfs;

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

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_range
     * rdfs:range is an instance of rdf:Property that is used to state that the values of a property are instances of one or more classes.
     */
    @Iri(RDFS.RANGE)
    void setRange(ResourceObject range);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_range
     * rdfs:range is an instance of rdf:Property that is used to state that the values of a property are instances of one or more classes.
     */
    @Iri(RDFS.RANGE)
    ResourceObject getRange();

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_domain
     * rdfs:domain is an instance of rdf:Property that is used to state that any resource that has a given property is an instance of one or more classes.
     */
    @Iri(RDFS.DOMAIN)
    void setDomain(ResourceObject domain);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_domain
     * rdfs:domain is an instance of rdf:Property that is used to state that any resource that has a given property is an instance of one or more classes.
     */
    @Iri(RDFS.DOMAIN)
    ResourceObject getDomain();

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subpropertyof
     * The property rdfs:subPropertyOf is an instance of rdf:Property that is used to state that all resources related by one property are also related by another.
     */
    @Iri(RDFS.SUB_PROPERTY_OF)
    void setSubProperties(Set<RDFSProperty> subProperties);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subpropertyof
     * The property rdfs:subPropertyOf is an instance of rdf:Property that is used to state that all resources related by one property are also related by another.
     */
    @Iri(RDFS.SUB_PROPERTY_OF)
    Set<RDFSProperty> getSubProperties();

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subpropertyof
     * The property rdfs:subPropertyOf is an instance of rdf:Property that is used to state that all resources related by one property are also related by another.
     */
    void addSubProperty(RDFSProperty subProperty);
}
