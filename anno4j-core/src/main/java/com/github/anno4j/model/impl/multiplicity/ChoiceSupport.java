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
        if(this.getItems() == null){
            this.setItems(new HashSet<RDFObject>());
        }
        this.getItems().add(item);
    }
    
}
