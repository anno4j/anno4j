package com.github.anno4j.model.impl.multiplicity;

import java.util.Collection;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.model.namespaces.OADM;

@Iri(OADM.COMPOSITE)
public interface Composite {

    @Iri(OADM.ITEM)
    void setItems(Collection<RDFObject> items);
    
    @Iri(OADM.ITEM)
    Collection<RDFObject> getItems();
    
    void addItem(RDFObject item);
    
}
