package com.github.anno4j.model.impl.multiplicity;

import java.util.Collection;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.model.namespaces.OADM;

@Iri(OADM.CHOICE)
public interface Choice {

    @Iri(OADM.ITEM)
    void setItems(Collection<RDFObject> items);
    
    @Iri(OADM.ITEM)
    Collection<RDFObject> getItems();
    
    void addItem(RDFObject item);

    @Iri(OADM.DEFAULT)
    void setDefault(RDFObject object);

    @Iri(OADM.DEFAULT)
    RDFObject getDefault();

    
    
}
