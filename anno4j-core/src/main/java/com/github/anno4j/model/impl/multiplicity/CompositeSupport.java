package com.github.anno4j.model.impl.multiplicity;

import java.util.Collection;
import java.util.HashSet;

import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

@Partial
public abstract class CompositeSupport extends ResourceObjectSupport implements Composite {

    @Override
    public void addItem(RDFObject item) {
        if(item == null){
            return;
        }
        Collection<RDFObject> items = getItems();
        if(items == null){
            items = new HashSet<>();
            setItems(items);
        }
        items.add(item);
    }
    
}
