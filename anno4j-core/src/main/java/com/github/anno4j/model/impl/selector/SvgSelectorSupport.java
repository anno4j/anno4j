package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.SelectorSupport;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.annotations.Partial;

/**
 * Created by schlegel on 05/10/15.
 */
@Partial
public abstract class SvgSelectorSupport extends SelectorSupport implements SvgSelector {
    @Override
    public String getConformsTo() {
        return FragmentSpecification.SVG.toString();
    }
}
