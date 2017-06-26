package com.github.anno4j.schema;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.Anno4jNS;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;

/**
 * Extends the Anno4j base interface {@link com.github.anno4j.model.impl.ResourceObject}
 * providing functionality for sanitizing the repository according to schema information
 * (see {@link SchemaSanitizingObjectSupport}).
 *
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
@Iri(Anno4jNS.RESOURCE)
public interface SchemaSanitizingObject extends ResourceObject {

    /**
     * Updates the repository in order to comply to the schema information present in it.
     * Note that updates are always performed locally, i.e. only for this resource.
     * @param propertyUri The IRI of the  property which values have changed. This must be the only property that
     *                    changed so that sub-/superproperties receive correct values.
     * @return Returns false on error. true is returned if sanitizing was performed successfully.
     */
    boolean sanitizeSchema(Resource propertyUri);

    /**
     * Updates the repository in order to comply to the schema information present in it.
     * Note that updates are always performed locally, i.e. only for this resource.
     * @param propertyUri The URI of the  property which values have changed. This must be the only property that
     *                    changed so that sub-/superproperties receive correct values.
     * @return Returns false on error. true is returned if sanitizing was performed successfully.
     */
    boolean sanitizeSchema(String propertyUri);
}
