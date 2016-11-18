package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.SKOS;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

import java.util.Set;

/**
 * Superclass for the nodes and relationship associated with OWL parsing.
 * Does not support a URI, so this class will only pass its methods to subclasses.
 */
public interface OWLSchemaResource extends ResourceObject {

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    @Iri(RDFS.LABEL)
    void setLabels(Set<String> labels);

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    @Iri(RDFS.LABEL)
    Set<String> getLabels();

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    void addLabel(String label);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    @Iri(RDFS.COMMENT)
    void setComment(String comment);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    @Iri(RDFS.COMMENT)
    String getComment();

    /**
     * Refers to http://www.w3.org/2004/02/skos/core#notation
     * A notation is a string of characters such as "T58.5" or "303.4833" used to uniquely identify a concept within
     * the scope of a given concept scheme.

     A notation is different from a lexical label in that a notation is not normally recognizable as a word or sequence
     of words in any natural language.
     */
    @Iri(SKOS.NOTATION)
    void setNotation(String notation);

    /**
     * Refers to http://www.w3.org/2004/02/skos/core#notation
     * A notation is a string of characters such as "T58.5" or "303.4833" used to uniquely identify a concept within
     * the scope of a given concept scheme.

     A notation is different from a lexical label in that a notation is not normally recognizable as a word or sequence
     of words in any natural language.
     */
    @Iri(SKOS.NOTATION)
    String getNotation();
}
