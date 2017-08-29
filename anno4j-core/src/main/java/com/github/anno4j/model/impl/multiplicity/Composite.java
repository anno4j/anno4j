package com.github.anno4j.model.impl.multiplicity;

import java.util.Set;

import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.model.namespaces.OADM;

@Iri(OADM.COMPOSITE)
public interface Composite extends ResourceObject {

    @Iri(OADM.ITEM)
    void setItems(Set<RDFObject> items);
    
    @Iri(OADM.ITEM)
    Set<RDFObject> getItems();
    
    void addItem(RDFObject item);
    
}
