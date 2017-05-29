package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.BuildableOntologyClazz;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import java.util.Set;

/**
 * Represents a rdfs:Class and extends it with additional information about incoming and
 * outgoing properties, as well as subclass relationships.
 * This additional information is not persisted by Anno4j but serves solely for Java class generation.
 */
@Iri(RDFS.CLAZZ)
public interface BuildableRDFSClazz extends RDFSClazz, BuildableOntologyClazz {

    /**
     * Returns those properties of which this class is part of the domain.
     * @return The properties with this class as part of their domain.
     */
    Set<RDFSProperty> getOutgoingProperties() throws RepositoryException;


    /**
     * Returns those properties of which this class is part of the range.
     * @return The properties with this class as part of their range.
     */
    Set<RDFSProperty> getIncomingProperties() throws RepositoryException;

    /**
     * Returns the subclasses of this class, i.e. those classes that occur as the subject in
     * rdfs:subClassOf statements with this class as object.
     * @return The subclasses of this class.
     */
    Set<RDFSClazz> getSubclazzes() throws RepositoryException;

    /**
     * Checks whether the given resource is a superclass of this class, i.e. there is a statement
     * with predicate rdfs:subClassOf, the resource as object and this class as subject.
     * Also searches transitively for a subclass relationship.
     * @param resource The resource to check. No check is done whether this actually has rdf:type rdfs:Class
     * @return Returns true iff the resource represents a direct or indirect superclass of this class.
     */
    boolean hasParent(String resource) throws RepositoryException;

    /**
     * Checks whether the given class is a superclass of this class, i.e. there is a statement
     * with predicate rdfs:subClassOf, the classes resource as object and this class as subject.
     * Also searches transitively for a subclass relationship.
     * @param clazz The class to check.
     * @return Returns true iff the class is a direct or indirect superclass of this class.
     */
    boolean hasParent(RDFSClazz clazz) throws RepositoryException;

    /**
     * Returns whether this class is a rdfs:Literal.
     * @return Returns true iff this class is rdfs:Literal or a subclass of it.
     */
    boolean isLiteral() throws RepositoryException;

    /**
     * Returns whether this class is a rdfs:Datatype.
     * @return Returns true iff this class is rdfs:Datatype or a subclass of it.
     */
    boolean isDatatype() throws RepositoryException;

    /**
     * Checks whether this clazz or one of its transitive superclass closure
     * is the domain of a certain property.
     * @param property The property to check for.
     * @return Returns true if the property is a outgoing property of the class
     * or of its transitive superclass closure.
     */
    boolean hasPropertyTransitive(RDFSProperty property) throws RepositoryException;
}
