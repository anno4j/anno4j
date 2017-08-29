package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.Anno4jNS;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Conforms to http://www.w3.org/ns/oa#Selector
 *
 * A resource which describes the segment of interest in a representation of a Source resource, indicated with oa:hasSelector from the Specific Resource.
 * This class is not used directly in Annotations, only its subclasses are.
 * The nature of the Selector will be dependent on the type of the representation for which the segment is conveyed. The specific type of selector should be indicated using a subclass of oa:Selector.
 * The Specifier's description MAY be conveyed as an external or embedded resource (cnt:Content), or as RDF properties within the graph. The description SHOULD use existing standards whenever possible. If the Specifier has an HTTP URI, then its description, and only its description, MUST be returned when the URI is dereferenced.
 */
@Iri(Anno4jNS.SELECTOR)
public interface Selector extends ResourceObject {

    /**
     * Sets the values for the http://www.w3.org/ns/oa#refinedBy relationship.
     *
     * The relationship between a Selector and another Selector or a State and a Selector or State that should be
     * applied to the results of the first to refine the processing of the source resource.
     *
     * @param selectors The Set of selectors to set.
     */
    @Iri(OADM.REFINED_BY)
    void setRefinedSelectors(Set<Selector> selectors);

    /**
     * Gets the values currently set for the http://www.w3.org/ns/oa#refinedBy relationship.
     *
     * The relationship between a Selector and another Selector or a State and a Selector or State that should be
     * applied to the results of the first to refine the processing of the source resource.
     *
     * @return  The Set of currently set Selectors.
     */
    @Iri(OADM.REFINED_BY)
    Set<Selector> getRefinedSelectors();

    /**
     * Adds a single value to the currently set http://www.w3.org/ns/oa#refinedBy relationship.
     *
     * @param selector  The Selector to add.
     */
    void addRefinedSelector(Selector selector);
}
