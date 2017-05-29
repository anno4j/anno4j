package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SCHEMA;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://schema.org/Audience
 *
 * Intended audience for an item, i.e. the group for whom the item was created.
 *
 * This interface should serve as the base class for implementing Audiences, e.g. an schema:EducationalAudience.
 */
@Iri(SCHEMA.AUDIENCE_CLASS)
public interface Audience extends ResourceObject {
}
