package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema_parsing.building.BuildableOntologyClazz;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Represents a rdfs:Class and extends it with additional information about incoming and
 * outgoing properties, as well as subclass relationships.
 * This additional information is not persisted by Anno4j but serves solely for Java class generation.
 */
@Iri(RDFS.CLAZZ)
public interface ExtendedRDFSClazz extends RDFSClazz, BuildableOntologyClazz {

    /**
     * Returns those properties of which this class is part of the domain.
     * @return The properties with this class as part of their domain.
     */
    Set<ExtendedRDFSProperty> getOutgoingProperties();

    /**
     * Sets those properties of which this class is part of the domain.
     * Also adds this class to the domain of the properties if it is not already part of.
     * @param props The properties with this class as part of their domain.
     */
    void setOutgoingProperties(Set<ExtendedRDFSProperty> props);

    /**
     * Sets those properties of which this class is part of the domain.
     * @param props The properties with this class as part of their domain.
     * @param updateInverse Whether to add this class to the domain entries of the properties.
     */
    void setOutgoingProperties(Set<ExtendedRDFSProperty> props, boolean updateInverse);

    /**
     * Adds a property which has this class as part of its domain.
     * If the property has no entry for this class as part of its domain then it will be added.
     * @param prop The property to add.
     */
    void addOutgoingProperty(ExtendedRDFSProperty prop);

    /**
     * Adds a property which has this class as part of its domain.
     * @param prop The property to add.
     * @param updateInverse Whether to add this class to the domain entries of the property.
     */
    void addOutgoingProperty(ExtendedRDFSProperty prop, boolean updateInverse);

    /**
     * Returns those properties of which this class is part of the range.
     * @return The properties with this class as part of their range.
     */
    Set<ExtendedRDFSProperty> getIncomingProperties();

    /**
     * Sets those properties of which this class is part of the range.
     * Also adds this class to the range of the properties if it is not already part of.
     * @param props The properties with this class as part of their range.
     */
    void setIncomingProperties(Set<ExtendedRDFSProperty> props);

    /**
     * Sets those properties of which this class is part of the range.
     * @param props The properties with this class as part of their range.
     * @param updateInverse Whether to add this class to the range of the properties.
     */
    void setIncomingProperties(Set<ExtendedRDFSProperty> props, boolean updateInverse);

    /**
     * Adds a property which has this class as part of its range.
     * If the property has no entry for this class as part of its range then it will be added.
     * @param prop The property to add.
     */
    void addIncomingProperty(ExtendedRDFSProperty prop);

    /**
     * Adds a property which has this class as part of its range.
     * @param prop The property to add.
     * @param updateInverse Whether to add this class to the range entries of the property.
     */
    void addIncomingProperty(ExtendedRDFSProperty prop, boolean updateInverse);

    /**
     * Returns the superclasses of this class, i.e. those classes that occur as the object in
     * rdfs:subClassOf statements with this class as subject.
     * @return The superclasses of this class.
     */
    Set<ExtendedRDFSClazz> getSuperclazzes();

    /**
     * Sets the superclasses of this class, i.e. those classes that occur as the object in
     * rdfs:subClassOf statements with this class as subject.
     * @param superclazzes The classes to set as superclasses of this class.
     */
    void setSuperclazzes(Set<ExtendedRDFSClazz> superclazzes);

    /**
     * Adds a clazz as a superclass of this class, i.e. a class that occurs as the object in
     * a rdfs:subClassOf statement with this class as the subject.
     * @param superClazz The class to add.
     */
    void addSuperclazz(ExtendedRDFSClazz superClazz);

    /**
     * Checks whether the given resource is a superclass of this class, i.e. there is a statement
     * with predicate rdfs:subClassOf, the resource as object and this class as subject.
     * Also searches transitively for a subclass relationship.
     * @param resource The resource to check. No check is done whether this actually has rdf:type rdfs:Class
     * @return Returns true iff the resource represents a direct or indirect superclass of this class.
     */
    boolean hasParent(String resource);

    /**
     * Checks whether the given class is a superclass of this class, i.e. there is a statement
     * with predicate rdfs:subClassOf, the classes resource as object and this class as subject.
     * Also searches transitively for a subclass relationship.
     * @param clazz The class to check.
     * @return Returns true iff the class is a direct or indirect superclass of this class.
     */
    boolean hasParent(ExtendedRDFSClazz clazz);

    /**
     * Returns whether this class is a rdfs:Literal.
     * @return Returns true iff this class is rdfs:Literal or a subclass of it.
     */
    boolean isLiteral();

    /**
     * Returns whether this class is a rdfs:Datatype.
     * @return Returns true iff this class is rdfs:Datatype or a subclass of it.
     */
    boolean isDatatype();

    /**
     * Checks whether this clazz or one of its transitive superclass closure
     * is the domain of a certain property.
     * @param property The property to check for.
     * @return Returns true if the property is a outgoing property of the class
     * or of its transitive superclass closure.
     */
    boolean hasPropertyTransitive(ExtendedRDFSProperty property);
}
