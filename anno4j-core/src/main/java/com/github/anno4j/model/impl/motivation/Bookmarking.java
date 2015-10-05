package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#bookmarking
 *
 * The motivation that represents the creation of a bookmark to the target resources or recorded point or points within one or more resources. For example, an Annotation that bookmarks the point in a text where the reader finished reading. Bookmark Annotations may or may not have a Body resource.
 */
@Iri(OADM.MOTIVATION_BOOKMARKING)
public interface Bookmarking extends Motivation {

}
