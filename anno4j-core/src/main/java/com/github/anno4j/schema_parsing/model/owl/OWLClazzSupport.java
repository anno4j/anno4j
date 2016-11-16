package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Manu on 16/11/16.
 */
@Partial
public abstract class OWLClazzSupport extends OWLSchemaResourceSupport implements OWLClazz {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubClazz(OWLClazz subClazz) {
        Set<OWLClazz> subClazzes = new HashSet<>();

        if(this.getSubClazzes() != null) {
            subClazzes.addAll(this.getSubClazzes());
        }

        subClazzes.add(subClazz);
        this.setSubClazzes(subClazzes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRestriction(OWLRestriction restriction) {
        Set<OWLRestriction> restrictions = new HashSet<>();

        if(this.getRestrictions() != null) {
            restrictions.addAll(this.getRestrictions());
        }

        restrictions.add(restriction);
        this.setRestrictions(restrictions);
    }




//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void addSubClazz(String subClazz) {
//        Set<String> subClazzes = new HashSet<String>();
//
//        if(this.getSubClazzes() != null ) {
//            subClazzes.addAll(this.getSubClazzes());
//        }
//
//        subClazzes.add(subClazz);
//        this.setSubClazzes(subClazzes);
//
//    }
}
