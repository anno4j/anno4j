package com.github.anno4j.rdfs_parser.mapping;

import com.github.anno4j.rdfs_parser.model.RDFSClazz;

/**
 * Implementations map a RDF datatype to a Java datatype.
 * This can be used to define the Java type used for custom datatypes
 * that may occur in an ontology.
 */
public interface DatatypeMapper {

    /**
     * Returns the Java datatype to which the RDF datatype given should be mapped.
     * Must be a type that is supported by Anno4j, thus the following types are valid:
     * <ul>
     *  <li>Java primitive corresponding classes ({@link Integer}, {@link Double}, ...)</li>
     *  <li>{@link CharSequence}</li>
     *  <li>{@link String}</li>
     *  <li>{@link org.openrdf.repository.object.LangString}</li>
     * </ul>
     * @param type The datatype to be mapped.
     * @return The Java type to use for the RDF type in generated Java files or null if this
     * mapper does not handle the given type.
     */
    Class<?> mapType(RDFSClazz type);
}
