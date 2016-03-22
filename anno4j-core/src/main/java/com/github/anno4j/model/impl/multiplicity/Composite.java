package com.github.anno4j.model.impl.multiplicity;

import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.model.namespaces.OADM;

@Iri(OADM.COMPOSITE)
public interface Composite {

    @Iri(OADM.ITEM)
    void setItems(Set<RDFObject> items);
    
    @Iri(OADM.ITEM)
    Set<RDFObject> getItems();
    
    void addItem(RDFObject item);
    
}
