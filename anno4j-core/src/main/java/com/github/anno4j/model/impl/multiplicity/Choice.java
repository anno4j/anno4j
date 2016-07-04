package com.github.anno4j.model.impl.multiplicity;

import java.util.Set;

import com.github.anno4j.model.Body;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.AS;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.model.namespaces.OADM;

@Iri(OADM.CHOICE)
public interface Choice extends ResourceObject, Body, Target {

    @Iri(AS.ITEMS)
    void setItems(Set<RDFObject> items);
    
    @Iri(AS.ITEMS)
    Set<RDFObject> getItems();
    
    void addItem(RDFObject item);
}
