package com.example.model.org.example;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;

/**
 * Support class for {@link Pizza} */
@Partial
public abstract class PizzaSupport extends SchemaSanitizingObjectSupport implements Pizza {
}
