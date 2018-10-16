package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

/**
 * According to the latest WADM specification, the specifications that Selectors conform to should be implemented
 * as own RDF resources. Therefore, this factory class can instantiate those (singleton) nodes when needed.
 */
public class SelectorFactory {

    public static ResourceObject getHtmlXhtmlSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.HTML_XHTML.toString()));
    }

    public static ResourceObject getPdfSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.PDF.toString()));
    }

    public static ResourceObject getPlainTextSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.PLAIN_TEXT.toString()));
    }

    public static ResourceObject getXmlSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.XML.toString()));
    }

    public static ResourceObject getRdfXmlSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.RDF_XML.toString()));
    }

    public static ResourceObject getMediaFragmentsSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.W3C_MEDIA_FRAGMENTS.toString()));
    }

    public static ResourceObject getSvgSpecification(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, (Resource) new URIImpl(FragmentSpecification.SVG.toString()));
    }
}
