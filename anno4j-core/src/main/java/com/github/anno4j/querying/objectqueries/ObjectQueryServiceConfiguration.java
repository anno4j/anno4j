package com.github.anno4j.querying.objectqueries;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.LinkedList;
import java.util.List;

public class ObjectQueryServiceConfiguration {

    // Inception-like storing of objects.. because we have interfaces.. maybe get some better way some time :)
    private Anno4j anno4j;

    private ResourceObject pivot;

    private List<ResourceObject> elements;

    public ObjectQueryServiceConfiguration() throws RepositoryConfigException, RepositoryException {
        this.anno4j = new Anno4j(false);
        this.elements = new LinkedList<ResourceObject>();
    }

    /**
     * Sets new anno4j.
     *
     * @param anno4j New value of anno4j.
     */
    public void setAnno4j(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

    /**
     * Gets anno4j.
     *
     * @return Value of anno4j.
     */
    public Anno4j getAnno4j() {
        return anno4j;
    }

    /**
     * Gets elements.
     *
     * @return Value of elements.
     */
    public List<ResourceObject> getElements() {
        return elements;
    }

    /**
     * Sets new elements.
     *
     * @param elements New value of elements.
     */
    public void setElements(List<ResourceObject> elements) {
        this.elements = elements;
    }

    /**
     * Gets pivot.
     *
     * @return Value of pivot.
     */
    public ResourceObject getPivot() {
        return pivot;
    }

    /**
     * Sets new pivot.
     *
     * @param pivot New value of pivot.
     */
    public void setPivot(ResourceObject pivot) {
        this.pivot = pivot;
    }
}
