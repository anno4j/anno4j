package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;

import java.util.Set;

/**
 * Refers to http://www.w3.org/2002/07/owl#Class
 * A class defines a group of individuals that belong together because they share some properties.
 */
@Iri(OWL.CLAZZ)
public interface OWLClazz extends OWLSchemaResource {

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subclassof
     * The property rdfs:subClassOf is an instance of rdf:Property that is used to state that all the instances of one class are instances of another.
     */
    @Iri(RDFS.SUB_CLASS_OF)
    void setSubClazzes(Set<OWLClazz> subClazzes);

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subclassof
     * The property rdfs:subClassOf is an instance of rdf:Property that is used to state that all the instances of one class are instances of another.
     */
    @Sparql("SELECT ?subclass WHERE { $this <"+ RDFS.SUB_CLASS_OF + "> ?subclass . ?subclass <"+ RDF.TYPE +"> <"+ OWL.CLAZZ +"> }")
    @Iri(RDFS.SUB_CLASS_OF)
    Set<OWLClazz> getSubClazzes();

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subclassof
     * The property rdfs:subClassOf is an instance of rdf:Property that is used to state that all the instances of one class are instances of another.
     */
    void addSubClazz(OWLClazz subClazz);

    @Iri(RDFS.SUB_CLASS_OF)
    void setRestrictions(Set<OWLRestriction> restrictions);

    @Sparql("SELECT ?restriction WHERE { $this <"+ RDFS.SUB_CLASS_OF + "> ?restriction . ?restriction <"+ RDF.TYPE +"> <"+ OWL.RESTRICTION +"> }")
    @Iri(RDFS.SUB_CLASS_OF)
    Set<OWLRestriction> getRestrictions();

    void addRestriction(OWLRestriction restriction);
}
