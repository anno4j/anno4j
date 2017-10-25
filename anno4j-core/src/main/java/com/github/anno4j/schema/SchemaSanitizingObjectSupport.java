package com.github.anno4j.schema;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.traits.Refreshable;


/**
 * Provides functionality to its inheriting support classes to make the repository
 * compliant to the schema that is defined in the repository using RDFS/OWL.
 * When the {@link #sanitizeSchema(Resource)} method is called the resources values
 * are made compliant to the schema, e.g. the inverse relation is inserted for a property
 * that is declared symmetric.
 * Currently the following schema features are handled by this support class:
 * <ul>
 *     <li>{@code rdfs:subPropertyOf}: Values set for a subproperty are also set for superproperties.
 *     Values not set for superproperties are removed from subproperties.</li>
 *     <li>{@code owl:SymmetricProperty}: If Y is the value of a property of this resource then this resource is
 *     also a value for Y (of the respective property).</li>
 *     <li>{@code owl:TransitiveProperty}: If this resource is in relation to Y and Y to Z by a certain property
 *     then Z is also set as a value of this resources property.</li>
 *     <li>{@code owl:inverseOf}: The inverse relation is set for all inverse properties.</li>
 * </ul>
 */
@Partial
public abstract class SchemaSanitizingObjectSupport extends ResourceObjectSupport implements SchemaSanitizingObject {

    /**
     * Complements values of superproperties according to values set for subproperties.
     * @throws RepositoryException Thrown if an error occurs while updating the repository.
     */
    private void sanitizeSuperProperties() throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            connection.prepareUpdate(QueryLanguage.SPARQL,
                    "INSERT {" +
                            "   <" + getResourceAsString() + "> ?super ?o . " +
                            "} WHERE {" +
                            "   <" + getResourceAsString() + "> ?p ?o . " +
                            "   ?p rdfs:subPropertyOf+ ?super . " +
                            "   MINUS {" +
                            "       <" + getResourceAsString() + "> ?super ?o2 ." +
                            "       {" +
                            "          ?o2 owl:sameAs+ ?o . " +
                            "       }" +
                            "       UNION " +
                            "       {" +
                            "          ?o owl:sameAs+ ?o2 . " +
                            "       }" +
                            "   }" +
                            "}"
            ).execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Removes orphan values of subproperties, i.e. those values that are set by a subproperty
     * but not for the given superproperty.
     * The values of properties that do not participate in the {@code rdfs:subPropertyOf} relationship
     * of the given property {@code superPropertyIri} are not affected.
     * @throws RepositoryException Thrown if an error occurs while updating the repository.
     */
    private void sanitizeSubProperties(String superPropertyIri) throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            connection.prepareUpdate(QueryLanguage.SPARQL,
                    "DELETE {" +
                            "   <" + getResourceAsString() + "> ?sub ?o . " +
                            "} WHERE {" + // Select those values that are linked by a subproperty:
                            "   <" + getResourceAsString() + "> ?sub ?o . " +
                            "   ?sub rdfs:subPropertyOf+ <" + superPropertyIri + "> . " +
                            "   MINUS {" +
                            "       {" + // Don't remove those values that are also linked by superproperties...
                            "           <" + getResourceAsString() + "> <" + superPropertyIri + "> ?o ." +
                            "       } " +
                            "       UNION " +
                            "       {" + // or that are equivalent to such a value:
                            "          <" + getResourceAsString() + "> <" + superPropertyIri + "> ?v . " +
                            "          { ?v owl:sameAs+ ?o . } UNION { ?o owl:sameAs+ ?v . }" +
                            "       }" +
                            "   }" +
                            "}"
            ).execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Complements symmetric properties ({@code owl:SymmetricProperty}) in order to satisfy
     * symmetry.
     * @throws RepositoryException Thrown if an error occurs while updating the repository.
     */
    private void sanitizeSymmetry() throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            connection.prepareUpdate(QueryLanguage.SPARQL,
                    "INSERT {" +
                            "   ?o ?p <" + getResourceAsString() + "> . " +
                            "} WHERE {\n" +
                            "   <" + getResourceAsString() + "> ?p ?o . " +
                            "   ?p a owl:SymmetricProperty . " +
                            "}"
            ).execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Complements transitive edges for transitive properties ({@code owl:TransitiveProperty}).
     * @throws RepositoryException Thrown if an error occurs while updating the repository.
     */
    private void sanitizeTransitivity() throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            connection.prepareUpdate(QueryLanguage.SPARQL,
                    "INSERT {" +
                            "   <" + getResourceAsString() + "> ?p ?z . " +
                            "} WHERE {" +
                            "   <" + getResourceAsString() + "> ?p ?y . " +
                            "   ?y ?p ?z . " +
                            "   ?p a owl:TransitiveProperty . " +
                            "}"
            ).execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Sets the resource as the value of the inverse property for any value of a property of this
     * resource that has an inverse property.
     * @throws RepositoryException Thrown if an error occurs while updating the repository.
     */
    private void sanitizeInverseProperties() throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            connection.prepareUpdate(QueryLanguage.SPARQL,
                    "INSERT {" +
                            "   ?o ?inverse <" + getResourceAsString() + "> . " +
                            "} WHERE {" +
                            "   <" + getResourceAsString() + "> ?p ?o . " +
                            "   ?p owl:inverseOf ?inverse . " +
                            "}"
            ).execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Removes a value from the property given and all its subproperties.
     * If the schema sanitizing capabilities of this support class are used
     * this method must be used in order to prevent that the removal is undone by {@link #sanitizeSchema(String)} later.
     * <b>Important:</b> It may be necessary to refresh cached values for this property afterwards with
     * {@link Refreshable#refresh()}. Otherwise getter-methods may return old values.<br>
     * Example:<br>
     * <code>removeValue("http://example.org/foo", myValue);</code><br>
     * <code>((PropertySet) getFoo()).refresh();</code>
     * @param propertyIri The IRI of the property from which the value should be removed.
     * @param value The value that should be removed.
     * @return Returns false iff an error occurred.
     */
    protected boolean removeValue(String propertyIri, Object value) {
        ObjectConnection connection = getObjectConnection();
        try {
            // For non-literal values:
            if(value instanceof ResourceObject) {
                // Remove the value from subproperties:
                connection.prepareUpdate(QueryLanguage.SPARQL,
                        "DELETE {" +
                                "   <" + getResourceAsString() + "> ?p <" + ((ResourceObject) value).getResourceAsString() + "> . " +
                                "} WHERE {" +
                                "   ?p rdfs:subPropertyOf+ <" + propertyIri + "> . " +
                                "}"
                ).execute();

                // Remove the value from the given property itself:
                connection.prepareUpdate(QueryLanguage.SPARQL,
                        "DELETE DATA {" +
                                "   <" + getResourceAsString() + "> <" + propertyIri + "> <" + ((ResourceObject) value).getResourceAsString() + "> . " +
                                "}"
                ).execute();

            } else { // if the value is a literal:

                // Get a SPARQL filter suitable representation of the value:
                String valueString;
                if(value instanceof Number) {
                    valueString = value.toString();
                } else {
                    valueString = "'" + value.toString() + "'";
                }

                // Remove the value from subproperties:
                connection.prepareUpdate(QueryLanguage.SPARQL,
                        "DELETE {" +
                                "   <" + getResourceAsString() + "> ?p ?v . " +
                                "} WHERE {" +
                                "   <" + getResourceAsString() + "> ?p ?v . " +
                                "   ?p rdfs:subPropertyOf+ <" + propertyIri + "> . " +
                                "   FILTER(?v = " + valueString + ")" +
                                "}"
                ).execute();

                // Remove the value from the given property itself:
                connection.prepareUpdate(QueryLanguage.SPARQL,
                        "DELETE {" +
                                "   <" + getResourceAsString() + "> <" + propertyIri + "> ?v . " +
                                "} WHERE {" +
                                "   <" + getResourceAsString() + "> <" + propertyIri + "> ?v ." +
                                "   FILTER(?v = " + valueString + ")" +
                                "}"
                ).execute();
            }

            return true;

        } catch (MalformedQueryException | UpdateExecutionException | RepositoryException e) {
            return false;
        }
    }

    /**
     * Updates the repository in order to comply to the schema information present in it.
     * Note that updates are always performed locally, i.e. only for this resource.
     * @param propertyUri The IRI of the  property which values have changed. This must be the only property that
     *                    changed so that sub-/superproperties receive correct values.
     * @return Returns false on error. true is returned if sanitizing was performed successfully.
     */
    @Override
    public boolean sanitizeSchema(Resource propertyUri) {
        try {
            sanitizeSubProperties(propertyUri.toString());
            sanitizeSuperProperties();
            sanitizeSymmetry();
            sanitizeTransitivity();
            sanitizeInverseProperties();
        } catch (RepositoryException e) {
            return false;
        }
        return true;
    }

    /**
     * Updates the repository in order to comply to the schema information present in it.
     * Note that updates are always performed locally, i.e. only for this resource.
     *
     * @param propertyUri The URI of the  property which values have changed. This must be the only property that
     *                    changed so that sub-/superproperties receive correct values.
     * @return Returns false on error. true is returned if sanitizing was performed successfully.
     */
    @Override
    public boolean sanitizeSchema(String propertyUri) {
        return sanitizeSchema(new URIImpl(propertyUri));
    }
}
