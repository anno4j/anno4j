package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;

/**
 * Conforms to http://www.w3.org/ns/oa#Selector
 *
 * A resource which describes the segment of interest in a representation of a Source resource, indicated with oa:hasSelector from the Specific Resource.
 * This class is not used directly in Annotations, only its subclasses are.
 * The nature of the Selector will be dependent on the type of the representation for which the segment is conveyed. The specific type of selector should be indicated using a subclass of oa:Selector.
 * The Specifier's description MAY be conveyed as an external or embedded resource (cnt:Content), or as RDF properties within the graph. The description SHOULD use existing standards whenever possible. If the Specifier has an HTTP URI, then its description, and only its description, MUST be returned when the URI is dereferenced.
 */
public interface Selector extends ResourceObject {

}
