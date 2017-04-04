package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.hp.hpl.jena.reasoner.ValidityReport;

import java.io.InputStream;
import java.util.Collection;

/**
 * Builds an ontology model from the RDF data provided.
 * The classes and properties can be queried after a call
 * to {@link #build()}.
 */
public interface OntologyModelBuilder {

    /**
     * Signalizes an error while building the ontology model from a set of RDF statements.
     */
    class RDFSModelBuildingException extends Exception {
        /**
         * {@inheritDoc}
         */
        public RDFSModelBuildingException() {
        }

        /**
         * {@inheritDoc}
         */
        public RDFSModelBuildingException(String message) {
            super(message);
        }
    }

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param rdfInput An input stream to RDF/XML data.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     */
     void addRDF(InputStream rdfInput, String base);

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param url      An URL to RDF data in RDF/XML format.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     */
    void addRDF(String url, String base);

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param rdfInput An input stream to the RDF data. Its format is defined by the <code>format</code> parameter.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     * @param format   The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     *                 or <code>null</code> for the default format.
     */
    void addRDF(InputStream rdfInput, String base, String format);
    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param url      An URL to RDF data in the specified format.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     * @param format   The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     *                 or <code>null</code> for the default format.
     */
    void addRDF(String url, String base, String format);

    /**
     * Adds RDF statements to the underlying model.
     *
     * @param url URL to a RDF/XML file containing the RDF data to be added.
     */
    void addRDF(String url);

    /**
     * Returns the extended resource objects of RDFS classes that were found during
     * the last call to {@link #build()}.
     *
     * @return Returns the RDFS classes in the model built.
     */
    Collection<ExtendedRDFSClazz> getClazzes();

    /**
     * Returns the extended resource objects of RDFS properties that were found during
     * the last call to {@link #build()}.
     *
     * @return Returns the RDFS properties in the model built.
     */
    Collection<ExtendedRDFSProperty> getProperties();

    /**
     * Returns a validity report for the model build during the last call of {@link #build()}.
     *
     * @return The validity report for the model. Use {@link ValidityReport#isValid()} to
     * check if the model built is valid.
     * @throws IllegalStateException Thrown if the model was not previously built.
     */
    ValidityReport validate();

    /**
     * Builds an ontology model for the RDF data added before using <code>addRDF</code> methods.
     * After a call to this method, the classes and properties in the model can be queried
     * using {@link #getClazzes()} and {@link #getProperties()} respectively.
     *
     * @throws RDFSModelBuildingException Thrown if an error occurs during building the model.
     */
    void build() throws RDFSModelBuildingException;
}
