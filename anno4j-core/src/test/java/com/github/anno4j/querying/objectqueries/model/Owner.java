package com.github.anno4j.querying.objectqueries.model;

import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.annotations.Iri;

@Iri("http://example.com/Owner")
public interface Owner extends ResourceObject {

    @Iri("http://example.com/hasName")
    void setName(String name);

    @Iri("http://example.com/hasName")
    String getName();
}
