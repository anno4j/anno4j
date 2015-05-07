package com.github.anno4j.model.impl.target;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to http://www.w3.org/ns/oa#SpecificResource
 *
 * A resource identifies part of another Source resource, a particular representation of a resource, a resource with styling hints for renders, or any combination of these.
 *
 * The Specific Resource takes the role of oa:hasBody or oa:hasTarget in an oa:Annotation instead of the Source resource.
 *
 * There MUST be exactly 1 oa:hasSource relationship associated with a Specific Resource.
 *
 * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource.
 *
 * There MAY be 0 or 1 oa:hasState relationship for each Specific Resource.
 *
 * If the Specific Resource has an HTTP URI, then the exact segment of the Source resource that it identifies, and only the segment, MUST be returned when the URI is dereferenced. For example, if the segment of interest is a region of an image and the Specific Resource has an HTTP URI, then dereferencing it MUST return the selected region of the image as it was at the time when the annotation was created. Typically this would be a burden to support, and thus the Specific Resource SHOULD be identified by a globally unique URI, such as a UUID URN. If it is not considered important to allow other Annotations or systems to refer to the Specific Resource, then a blank node MAY be used instead.
 */
@Iri(OADM.SPECIFIC_RESOURCE)
public class SpecificResource extends Target {

    @Iri(OADM.HAS_SOURCE)   private RDFObject source;
    @Iri(OADM.HAS_SELECTOR) private Selector selector;

    public SpecificResource() {};

    public SpecificResource(RDFObject source, Selector selector) {
        this.source = source;
        this.selector = selector;
    }

    public RDFObject getSource() {
        return source;
    }

    public void setSource(RDFObject source) {
        this.source = source;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }
}
