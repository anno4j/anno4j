package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to https://www.w3.org/TR/rdf-schema/#ch_class
 * This is the class of resources that are RDF classes. rdfs:Class is an instance of rdfs:Class.
 */
@Iri(RDFS.CLAZZ)
public interface RDFSClazz extends RDFSSchemaResource {

    @Iri(RDFS.SUB_CLAZZ_OF)
    void setSubClazzes(Set<String> subClazzes);

    @Iri(RDFS.SUB_CLAZZ_OF)
    Set<String> getSubClazzes();

    void addSubClazz(String subClazz);
}
