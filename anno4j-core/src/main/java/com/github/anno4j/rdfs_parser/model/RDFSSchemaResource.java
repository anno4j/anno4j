package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Top level (Java) superclass for the RDFSClazz and RDFSProperty interfaces.
 */
public interface RDFSSchemaResource extends ResourceObject {

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    @Iri(RDFS.LABEL)
    void setLabels(Set<CharSequence> labels);

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    @Iri(RDFS.LABEL)
    Set<CharSequence> getLabels();

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    void addLabel(CharSequence label);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    @Iri(RDFS.COMMENT)
    void setComments(Set<CharSequence> comments);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    @Iri(RDFS.COMMENT)
    Set<CharSequence> getComments();

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    void addComment(CharSequence comment);
}
