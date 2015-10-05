package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.persistence.annotation.Partial;

/**
 * Created by schlegel on 05/10/15.
 */
@Partial
public abstract class SvgSelectorSupport implements SvgSelector {
    @Override
    public String getConformsTo() {
        return FragmentSpecification.SVG.toString();
    }
}
