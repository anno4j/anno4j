package com.github.anno4j.querying;

import org.openrdf.repository.object.RDFObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The QueryObject allows to query the MICO triple stores by using criterias. Furthermore
 * this is provided by simple classes. This is why the user does not need to write SPARQL queries
 * by himself.
 *
 * @param <T>
 *
 * @author Andreas Eisenkolb
 */
public class QueryObject<T extends RDFObject>  {


    private Map<String, String> prefixes = new HashMap<String, String>();

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects.
     *
     * @param ldpath
     * @param comparison
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setBodyCriteria(String ldpath, String value, Comparison comparison) {

        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects. Compared to the
     * other <i>setBodyCriteria</i> function, this function does not need a <b>Comparison</b> statement. Hence, the
     * Comparison.EQ statement ("=") will be used automatically.
     *
     * @param ldpath
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setBodyCriteria(String ldpath, String value) {
        return setBodyCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath
     * @param comparison
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setAnnotationCriteria(String ldpath, String value, Comparison comparison) {

        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects. Compared to the
     * other <i>setAnnotationCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setAnnotationCriteria(String ldpath, String value) {
        return setAnnotationCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects.
     *
     * @param ldpath
     * @param comparison
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setSelectorCriteria(String ldpath, String value, Comparison comparison) {

        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects. Compared to the
     * other <i>setSelectorCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath
     * @param value
     *
     * @return itself to allow chaining.
     */
    public QueryObject setSelectorCriteria(String ldpath, String value) {
        return setSelectorCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting shortcut names for URI prefixes.
     *
     * @param label
     * @param url
     *
     * @return itself to allow chaining.
     */
    public QueryObject addPrefix(String label, String url) {
        this.prefixes.put(label, url);
        return this;
    }

    /**
     * Setting multiple names for URI prefixes.
     *
     * @param prefixes
     *
     * @return itself to allow chaining.
     */
    public QueryObject addPrefixes(Map<String, String> prefixes) {
        this.prefixes.putAll(prefixes);
        return this;
    }

    /**
     * Executes the generated query and returns the result
     *
     * @param <T>
     *
     * @return itself to allow chaining.
     */
    public <T> List<T> execute() {

        return null;
    }

}
