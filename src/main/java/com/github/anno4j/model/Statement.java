package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.ontologies.RDF;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

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
public class Statement extends ResourceObject {

    @Iri(RDF.SUBJECT)   private ResourceObject subject;
    @Iri(RDF.PREDICATE) private Resource predicate;
    @Iri(RDF.OBJECT)    private ResourceObject object;

    /**
     * Default Constructor
     */
    public Statement() {};

    /**
     * Constructor also setting the fields subject, predicate, and object.
     * @param subject   The subject of the statement.
     * @param predicate The predicate of the statement.
     * @param object    The object of the statement.
     */
    public Statement(ResourceObject subject, Resource predicate, ResourceObject object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     * Method to set the predicate of this Statement by a textual representation.
     * @param predicateAsString Textual representation of the resource to set.
     */
    public void setPredicateAsString(String predicateAsString) {
        this.setPredicate(new URIImpl(predicateAsString));
    }

    @Override
    public String toString() {
        return "Statement{\n" +
                "resource='" + this.getResource() + "'\n" +
                "subject='" + this.getSubject().toString() + "'\n" +
                "predicate='" + this.getPredicate().toString() + "'\n" +
                "object='" + this.getObject().toString() + "'\n" +
                "}";
    }

    /**
     * Gets subject.
     *
     * @return Value of subject.
     */
    public ResourceObject getSubject() {
        return subject;
    }

    /**
     * Sets new predicate.
     *
     * @param predicate New value of predicate.
     */
    public void setPredicate(Resource predicate) {
        this.predicate = predicate;
    }

    /**
     * Sets new subject.
     *
     * @param subject New value of subject.
     */
    public void setSubject(ResourceObject subject) {
        this.subject = subject;
    }

    /**
     * Gets predicate.
     *
     * @return Value of predicate.
     */
    public Resource getPredicate() {
        return predicate;
    }

    /**
     * Gets object.
     *
     * @return Value of object.
     */
    public ResourceObject getObject() {
        return object;
    }

    /**
     * Sets new object.
     *
     * @param object New value of object.
     */
    public void setObject(ResourceObject object) {
        this.object = object;
    }
}
