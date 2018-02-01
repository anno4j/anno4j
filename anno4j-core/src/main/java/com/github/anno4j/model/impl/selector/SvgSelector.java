package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

/**
 * Conforms to http://www.w3.org/TR/annotation-model/#svg-selector
 *
 * [subClass of oa:Selector] The class for a Selector which defines a shape using the SVG standard.
 */
@Iri(OADM.SVG_SELECTOR)
public interface SvgSelector extends Selector {
    /**
     * Gets Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @return Value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    @Iri(RDF.VALUE)
    String getValue();

    /**
     * Sets new Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @param value New value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     *              <p/>
     *              rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    @Iri(RDF.VALUE)
    void setValue(String value);
    /**
     *
     * Gets http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     *
     * @return Value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     */
    @Iri(DCTERMS.CONFORMS_TO)
    ResourceObject getConformsTo() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException;
}
