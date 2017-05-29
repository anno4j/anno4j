package com.github.anno4j.model.impl.state;

import com.github.anno4j.model.State;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#HttpRequestState.
 *
 * The HttpRequestState class is used to record the HTTP request headers that a client should use to request the
 * correct representation from the resource.
 */
@Iri(OADM.HTTP_REQUEST_STATE)
public interface HttpRequestState extends State {

    /**
     * Sets the value for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     *
     * @param value The value to set for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     */
    @Iri(RDF.VALUE)
    void setValue(String value);

    /**
     * Gets the value currently defined for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value relationship.
     *
     * @return  The value currently defined for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value relationship.
     */
    @Iri(RDF.VALUE)
    String getValue();
}
