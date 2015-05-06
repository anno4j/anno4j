package com.github.anno4j.querying;

import org.openrdf.repository.object.RDFObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The QueryObject allows to query the MICO triple stores by using criterias. Furthermore
 * this is provided by simple classes. This is why the user does not need to write SPARQL queries
 * by himself.
 *
 * @param <T>
 * @author Andreas Eisenkolb
 */
public class QueryObject<T extends RDFObject> {

    private final String BODY_PREFIX = "oa:hasBody/";

    private final String TARGET_PREFIX = "oa:hasTarget/";

    private final String SOURCE_PREFIX = TARGET_PREFIX + "oa:hasSource/";

    private final String SELECTOR_PREFIX = TARGET_PREFIX + "oa:hasSelector/";

    private Map<String, String> prefixes = new HashMap<String, String>();

    private ArrayList<Criteria> criterias = new ArrayList<Criteria>();

    private Order order = null;

    private Integer limit = -1;

    private Integer offset = 0;

    public QueryObject() {
        prefixes.put("oa:", "http://www.w3.org/ns/oa#");
        prefixes.put("cnt:", "http://www.w3.org/2011/content#");
        prefixes.put("dc:", "http://purl.org/dc/elements/1.1/");
        prefixes.put("dcterms", "http://purl.org/dc/terms/");
        prefixes.put("dctypes", "http://purl.org/dc/dcmitype/");
        prefixes.put("foaf", "http://xmlns.com/foaf/0.1/");
        prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        prefixes.put("prov", "http://www.w3.org/ns/prov#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("skos", "http://www.w3.org/2004/02/skos/core#");
        prefixes.put("trig", "http://www.w3.org/2004/03/trix/rdfg-1/");
        prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Body object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setBodyCriteria(String ldpath, String value, Comparison comparison) {
        criterias.add(new Criteria(BODY_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects. Compared to the
     * other <i>setBodyCriteria</i> function, this function does not need a <b>Comparison</b> statement. Hence,
     * the Comparison.EQ statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Body object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setBodyCriteria(String ldpath, String value) {
        return setBodyCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Annotation object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setAnnotationCriteria(String ldpath, String value, Comparison comparison) {
        criterias.add(new Criteria(ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects. Compared to the
     * other <i>setAnnotationCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setAnnotationCriteria(String ldpath, String value) {
        return setAnnotationCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Selector object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setSelectorCriteria(String ldpath, String value, Comparison comparison) {
        criterias.add(new Criteria(SELECTOR_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects. Compared to the
     * other <i>setSelectorCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Selector object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setSelectorCriteria(String ldpath, String value) {
        return setSelectorCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * @param ldpath     Syntax similar to XPath. Beginning from the Source object
     * @param value      The constraint value
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @return itself to allow chaining.
     */
    public QueryObject setSourceCriteria(String ldpath, String value, Comparison comparison) {
        criterias.add(new Criteria(SOURCE_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * @param ldpath Syntax similar to XPath. Beginning from the Source object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryObject setSourceCriteria(String ldpath, String value) {
        return setSourceCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting shortcut names for URI prefixes.
     *
     * @param label The label of the namespace, e.g. foaf
     * @param url   The URL
     * @return itself to allow chaining.
     */
    public QueryObject addPrefix(String label, String url) {
        this.prefixes.put(label, url);
        return this;
    }

    /**
     * Setting multiple names for URI prefixes.
     *
     * @param prefixes HashMap with multiple namespaces.
     * @return itself to allow chaining.
     */
    public QueryObject addPrefixes(HashMap<String, String> prefixes) {
        this.prefixes.putAll(prefixes);
        return this;
    }

    /**
     * Defines the ordering of the result set.
     *
     * @param order Defines the order of the result set.
     * @return itself to allow chaining.
     */
    public QueryObject orderBy(Order order) {
        this.order = order;
        return this;
    }

    /**
     * Setting the limit value.
     *
     * @param limit The limit value.
     * @return itself to allow chaining.
     */
    public QueryObject limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Setting the offset value.
     *
     * @param offset The offset value.
     * @return itself to allow chaining.
     */
    public QueryObject offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Executes the generated query and returns the result.
     *
     * @param <T>
     * @return itself to allow chaining.
     */
    public <T> List<T> execute() {

        return null;
    }
}
