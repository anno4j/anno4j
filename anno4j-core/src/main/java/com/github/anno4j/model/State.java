package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * A State describes the intended state of a resource as applied to the particular Annotation, and thus provides the
 * information needed to retrieve the correct representation of that resource.
 */
@Iri(OADM.STATE)
public interface State extends ResourceObject {

}
