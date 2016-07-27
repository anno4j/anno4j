package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

/**
 * Factory to produce the instances used for text direction.
 */
public class TextDirectionFactory {

    public static ResourceObject getLeftToRight(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, new URIImpl(OADM.LEFT_TO_RIGHT_DIRECTION));
    }

    public static ResourceObject getRightToLeft(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, new URIImpl(OADM.RIGHT_TO_LEFT_DIRECTION));
    }

    public static ResourceObject getAuto(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(ResourceObject.class, new URIImpl(OADM.AUTO_DIRECTION));
    }
}
