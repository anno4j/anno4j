package com.github.anno4j.similarity.model;

import com.github.anno4j.annotations.Partial;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.RDFObject;

/**
 * Created by Manu on 26/10/15.
 */
@Partial
public abstract class StatementSupport implements Statement, RDFObject {

    public void setPredicateAsString(String predicateAsString) {
        this.setPredicate(new URIImpl(predicateAsString));
    }
}
