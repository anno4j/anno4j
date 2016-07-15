package com.github.anno4j.model.impl.style;

import com.github.anno4j.model.Style;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#CssStyle.
 *
 * A resource which describes styles for resources participating in the Annotation using CSS.
 */
@Iri(OADM.CSS_STYLE)
public interface CssStylesheet extends Style {
}
