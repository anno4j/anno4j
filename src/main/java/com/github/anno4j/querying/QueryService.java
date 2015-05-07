package com.github.anno4j.querying;

import com.github.anno4j.model.ontologies.*;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.PathSelector;
import org.apache.marmotta.ldpath.parser.LdPathParser;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.object.RDFObject;

import java.io.StringReader;
import java.util.*;

/**
 * The QueryObject allows to query the MICO triple stores by using criterias. Furthermore
 * this is provided by simple classes. This is why the user does not need to write SPARQL queries
 * by himself.
 *
 * @param <T>
 * @author Andreas Eisenkolb
 */
public class QueryService<T extends RDFObject> {

    private final String BODY_PREFIX = OADM.PREFIX + OADM.HAS_BODY + "/";

    private final String TARGET_PREFIX = OADM.PREFIX + OADM.HAS_TARGET + "/";

    private final String SOURCE_PREFIX = TARGET_PREFIX + OADM.PREFIX + OADM.HAS_SOURCE + "/";

    private final String SELECTOR_PREFIX = TARGET_PREFIX + OADM.PREFIX + OADM.HAS_SELECTOR + "/";

    private Map<String, String> prefixes = new HashMap<String, String>();

    private ArrayList<Criteria> criterias = new ArrayList<Criteria>();

    private Order order = null;

    private Integer limit = null;

    private Integer offset = null;

    public QueryService() {
        // Setting some standard name spaces
        addPrefix(OADM.PREFIX, OADM.NS);
        addPrefix(CNT.PREFIX, CNT.NS);
        addPrefix(DC.PREFIX, DC.NS);
        addPrefix(DCTERMS.PREFIX, DCTERMS.NS);
        addPrefix(DCTYPES.PREFIX, DCTYPES.NS);
        addPrefix(FOAF.PREFIX, FOAF.NS);
        addPrefix(PROV.PREFIX, PROV.NS);
        addPrefix(RDF.PREFIX, RDF.NS);
        addPrefix(OWL.PREFIX, OWL.NAMESPACE);
        addPrefix(RDFS.PREFIX, RDFS.NAMESPACE);
        addPrefix(SKOS.PREFIX, SKOS.NAMESPACE);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Body object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setBodyCriteria(String ldpath, String value, Comparison comparison) {
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
    public QueryService setBodyCriteria(String ldpath, String value) {
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
    public QueryService setAnnotationCriteria(String ldpath, String value, Comparison comparison) {
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
    public QueryService setAnnotationCriteria(String ldpath, String value) {
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
    public QueryService setSelectorCriteria(String ldpath, String value, Comparison comparison) {
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
    public QueryService setSelectorCriteria(String ldpath, String value) {
        return setSelectorCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * @param ldpath     Syntax similar to XPath. Beginning from the Source object
     * @param value      The constraint value
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, String value, Comparison comparison) {
        criterias.add(new Criteria(SOURCE_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * @param ldpath Syntax similar to XPath. Beginning from the Source object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, String value) {
        return setSourceCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting shortcut names for URI prefixes.
     *
     * @param label The label of the namespace, e.g. foaf
     * @param url   The URL
     * @return itself to allow chaining.
     */
    public QueryService addPrefix(String label, String url) {
        this.prefixes.put(label, url);
        return this;
    }

    /**
     * Setting multiple names for URI prefixes.
     *
     * @param prefixes HashMap with multiple namespaces.
     * @return itself to allow chaining.
     */
    public QueryService addPrefixes(HashMap<String, String> prefixes) {
        this.prefixes.putAll(prefixes);
        return this;
    }

    /**
     * Defines the ordering of the result set.
     *
     * @param order Defines the order of the result set.
     * @return itself to allow chaining.
     */
    public QueryService orderBy(Order order) {
        this.order = order;
        return this;
    }

    /**
     * Setting the limit value.
     *
     * @param limit The limit value.
     * @return itself to allow chaining.
     */
    public QueryService limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Setting the offset value.
     *
     * @param offset The offset value.
     * @return itself to allow chaining.
     */
    public QueryService offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Creates and executes the SPARQL query according to the
     * criterias specified by the user.
     *
     * @param <T>
     * @return the result set
     */
    public <T> List<T> execute() {

        String query = createQuery();

        return null;
    }

    /**
     * Converts itself to a executable SPARQL query.
     *
     * @return the SPARQL query as string
     */
    private String createQuery() {

        StringBuilder query = new StringBuilder();

        /**
         * Adding the prefixes to the SPARQL query (format: PREFIX Label: <IRI>)
         */
        for (String key : prefixes.keySet()) {
            query
                    .append("PREFIX ")
                    .append(key)
                    .append(": <")
                    .append(prefixes.get(key))
                    .append("> ")
                    .append(System.getProperty("line.separator"));
        }

        // For readability: Adding an empty line between the prefix and the statement part
        query.append(System.getProperty("line.separator"));

        System.out.println( query.toString());

        // Creating the actual statements

        for (Criteria criteria : criterias) {
            LdPathParser parser = new LdPathParser(new SesameValueBackend(), new StringReader(criteria.getLdpath()));
            try {

                NodeSelector selector = (PathSelector) parser.parseSelector(prefixes);
                System.out.println(selector.getPathExpression(new SesameValueBackend()));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return query.toString();
    }
}
