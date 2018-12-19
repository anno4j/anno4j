package com.github.anno4j.querying.objectqueries.model;

import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.annotations.Iri;

@Iri("http://example.com/Cat")
public interface Cat extends ResourceObject {

    @Iri("http://example.com/hasName")
    void setName(String name);

    @Iri("http://example.com/hasName")
    String getName();

    @Iri("http://example.com/hasOwner")
    void setOwner(Owner owner);

    @Iri("http://example.com/hasOwner")
    Owner getOwner();
}
