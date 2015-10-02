package com.github.anno4j.querying.extension.annotation;


import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Evaluator {
    Class<?> value();
}
