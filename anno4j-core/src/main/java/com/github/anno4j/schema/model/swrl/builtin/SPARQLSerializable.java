package com.github.anno4j.schema.model.swrl.builtin;

/**
 * Interface implemented by {@link SWRLBuiltin} implementations that
 * can serialize the built-in to SPARQL.
 */
public interface SPARQLSerializable {

    /**
     * Returns the built-ins condition as a SPARQL filter expression.
     * The returned expression can be used within a conjunction, i.e. is legal SPARQL syntax within two "&&".
     * @return The SPARQL filter representation.
     */
    String asSPARQLFilterExpression();
}
