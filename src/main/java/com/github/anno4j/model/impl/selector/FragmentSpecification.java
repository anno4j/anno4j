package com.github.anno4j.model.impl.selector;

/**
 * Enum contains the specifications that a FragmentSelector can be conform to.
 * Possible specifications include HTML/XHTML, PDF, Plain Text, XML, RDF/XML, W3C Media Fragments, and SVG.
 *
 * @see com.github.anno4j.model.impl.selector.FragmentSelector
 */
public enum FragmentSpecification {
    HTML_XHTML          ("http://tools.ietf.org/rfc/rfc3236"),
    PDF                 ("http://tools.ietf.org/rfc/rfc3778"),
    PLAIN_TEXT          ("http://tools.ietf.org/rfc/rfc5147"),
    XML                 ("http://tools.ietf.org/rfc/rfc3023"),
    RDF_XML             ("http://www.ietf.org/rfc/rfc3870"),
    W3C_MEDIA_FRAGMENTS ("http://www.w3.org/TR/media-frags/"),
    SVG                 ("http://www.w3.org/TR/SVG/")
    ;

    private final String specification;

    FragmentSpecification(String specification) {
        this.specification = specification;
    }

    @Override
    public String toString() {
        return this.specification;
    }
}
