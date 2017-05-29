package com.github.anno4j.model.impl.multiplicity;

import java.util.HashSet;

import org.openrdf.repository.object.RDFObject;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

@Partial
public abstract class ChoiceSupport extends ResourceObjectSupport implements Choice {

    @Override
    public void addItem(RDFObject item) {
        if(item == null){
            return;
        }

        HashSet<RDFObject> items = new HashSet<>();

        if(this.getItems() != null) {
            items.addAll(this.getItems());
        }

        items.add(item);
        this.setItems(items);
    }
    
}
