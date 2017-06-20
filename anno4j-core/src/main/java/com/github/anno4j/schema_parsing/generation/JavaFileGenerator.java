package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import org.openrdf.repository.RepositoryException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Generates Anno4j resource object and support object <code>.java</code> files
 * from an ontology.
 */
public interface JavaFileGenerator {

    /**
     * Exception signalizing an error during Java file generation.
     */
    class JavaFileGenerationException extends Exception {

        /**
         * {@inheritDoc}
         */
        public JavaFileGenerationException() {
        }

        /**
         * {@inheritDoc}
         */
        public JavaFileGenerationException(Throwable cause) {
            super(cause);
        }

        /**
         * {@inheritDoc}
         */
        public JavaFileGenerationException(String message) {
            super(message);
        }
    }

    /**
     * Exception signalizing that the ontology to build was found invalid.
     */
    class InvalidOntologyException extends JavaFileGenerationException {

        /**
         * {@inheritDoc}
         */
        public InvalidOntologyException() {
        }

        /**
         * {@inheritDoc}
         */
        public InvalidOntologyException(String message) {
            super(message);
        }
    }

    /**
     * Adds RDF statements containing ontology information.
     * This method and the other <code>addRDF</code> methods may be called
     * arbitrarily often (but at least once) before a subsequent call to
     * {@link #generateJavaFiles(OntGenerationConfig, File)}.
     *
     * @param rdfInput An input stream to RDF/XML data.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     */
    void addRDF(InputStream rdfInput, String base);

    /**
     * Adds RDF statements containing ontology information.
     * This method and the other <code>addRDF</code> methods may be called
     * arbitrarily often (but at least once) before a subsequent call to
     * {@link #generateJavaFiles(OntGenerationConfig, File)}.
     *
     * @param url      An URL to RDF data in RDF/XML format.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     */
    void addRDF(String url, String base);

    /**
     * Adds RDF statements containing ontology information.
     * This method and the other <code>addRDF</code> methods may be called
     * arbitrarily often (but at least once) before a subsequent call to
     * {@link #generateJavaFiles(OntGenerationConfig, File)}.
     *
     * @param rdfInput An input stream to the RDF data. Its format is defined by the <code>format</code> parameter.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     * @param format   The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     *                 or <code>null</code> for the default format.
     */
    void addRDF(InputStream rdfInput, String base, String format);

    /**
     * Adds RDF statements containing ontology information.
     * This method and the other <code>addRDF</code> methods may be called
     * arbitrarily often (but at least once) before a subsequent call to
     * {@link #generateJavaFiles(OntGenerationConfig, File)}.
     *
     * @param url      An URL to RDF data in the specified format.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     * @param format   The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     *                 or <code>null</code> for the default format.
     */
    void addRDF(String url, String base, String format);

    /**
     * Generates <code>.java</code> files of resource objects (see {@link com.github.anno4j.model.impl.ResourceObject})
     * and support classes (see {@link com.github.anno4j.model.impl.ResourceObjectSupport}) for
     * the classes defined in a ontology.
     * Information about this ontology must be previously added with the <code>addRDF</code> methods.
     *
     * @param config The configuration object defining how java classes are generated from an ontology.
     *               For example a preference for the language of generated JavaDoc can be set.
     * @param outputDirectory The directory where the generated <code>.java</code> files
     *                        (or their package directories respectively) are stored.
     *                        Will be created if it does not exist. If the directory is not empty
     *                        then existing files may be overwritten.
     * @throws JavaFileGenerationException Thrown if an error occurred during file generation.
     * @throws IOException Thrown if an error occurs while writing the generated files.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    void generateJavaFiles(OntGenerationConfig config, File outputDirectory) throws JavaFileGenerationException, IOException, RepositoryException;
}
