package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#Style.
 *
 * A Style describes the intended styling of a resource as applied to the particular Annotation, and thus provides the
 * information to ensure that rendering is consistent across implementations.
 */
@Iri(OADM.STYLE)
public interface Style extends ResourceObject{
}
