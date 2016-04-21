package com.github.anno4j.similarity.model;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;

/**
 * Refers to http://www.w3.org/TR/rdf-schema/#ch_statement
 * rdf:Statement is an instance of rdfs:Class. It is intended to represent the class of RDF statements.
 * An RDF statement is the statement made by a token of an RDF triple. The subject of an RDF statement is the instance
 * of rdfs:Resource identified by the subject of the triple. The predicate of an RDF statement is the instance of
 * rdf:Property identified by the predicate of the triple. The object of an RDF statement is the instance of
 * rdfs:Resource identified by the object of the triple. rdf:Statement is in the domain of the properties rdf:predicate,
 * rdf:subject and rdf:object. Different individual rdf:Statement instances may have the same values for their
 * rdf:predicate, rdf:subject and rdf:object properties.
 */
@Iri(RDF.STATEMENT)
public interface Statement extends Body {

    /**
     * Method to set the predicate of this Statement by a textual representation.
     * @param predicateAsString Textual representation of the resource to set.
     */
    public void setPredicateAsString(String predicateAsString);

    @Override
    public String toString();

    /**
     * Sets new subject.
     *
     * @param subject New value of subject.
     */
    @Iri(RDF.SUBJECT)
    public void setSubject(ResourceObject subject);

    /**
     * Gets subject.
     *
     * @return Value of subject.
     */
    @Iri(RDF.SUBJECT)
    public ResourceObject getSubject();

    /**
     * Sets new predicate.
     *
     * @param predicate New value of predicate.
     */
    @Iri(RDF.PREDICATE)
    public void setPredicate(Resource predicate);

    /**
     * Gets predicate.
     *
     * @return Value of predicate.
     */
    @Iri(RDF.PREDICATE)
    public Resource getPredicate();

    /**
     * Sets new object.
     *
     * @param object New value of object.
     */
    @Iri(RDF.OBJECT)
    public void setObject(ResourceObject object);

    /**
     * Gets object.
     *
     * @return Value of object.
     */
    @Iri(RDF.OBJECT)
    public ResourceObject getObject();
}
