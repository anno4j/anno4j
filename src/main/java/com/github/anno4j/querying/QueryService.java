package com.github.anno4j.querying;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.ontologies.*;
import com.github.anno4j.querying.evaluation.EvalQuery;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.ObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The QueryService allows to query the MICO triple stores by using criteria. Furthermore
 * this is provided by simple classes. This is why the user does not need to write SPARQL queries
 * by himself.
 *
 * @param <T>
 * @author Andreas Eisenkolb
 */
public class QueryService<T extends Annotation> {

    private final Logger logger = LoggerFactory.getLogger(QueryService.class);

    /**
     * The type of the result set.
     */
    private Class<T> type;

    /**
     * The repository needed for the actual querying
     */
    private ObjectRepository objectRepository;

    /**
     * LDPath for the shortcut method setBodyCriteria
     */
    private final String BODY_PREFIX = "oa:hasBody/";

    /**
     * LDPath for the shortcut method setTargetCriteria
     */
    private final String TARGET_PREFIX = "oa:hasTarget/";

    /**
     * LDPath for the shortcut method setSourceCriteria
     */
    private final String SOURCE_PREFIX = TARGET_PREFIX + "oa:hasSource/";

    /**
     * LDPath for the shortcut method setSelectorCriteria
     */
    private final String SELECTOR_PREFIX = TARGET_PREFIX + "oa:hasSelector/";

    /**
     * All user defined name spaces
     */
    private Map<String, String> prefixes = new HashMap<String, String>();

    /**
     * All user defined criteria
     */
    private ArrayList<Criteria> criteria = new ArrayList<Criteria>();

    /**
     * Specifies the ordering of the result set
     */
    private Order order = null;

    /**
     * Limit value of the query
     */
    private Integer limit = null;

    /**
     * Offset value for the query
     */
    private Integer offset = null;

    /**
     * Required to have an ongoing variable name when creating the SPARQL query
     */
    private int varIndex = 0;

    public QueryService(Class<T> type, ObjectRepository objectRepository) {
        this.type = type;
        this.objectRepository = objectRepository;
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
        criteria.add(new Criteria(BODY_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Body object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setBodyCriteria(String ldpath, Number value, Comparison comparison) {
        criteria.add(new Criteria(BODY_PREFIX + ldpath, value, comparison));
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
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects. Compared to the
     * other <i>setBodyCriteria</i> function, this function does not need a <b>Comparison</b> statement. Hence,
     * the Comparison.EQ statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Body object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setBodyCriteria(String ldpath, Number value) {
        return setBodyCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.BodyImpl.* objects.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Body object
     * @return itself to allow chaining.
     */
    public QueryService setBodyCriteria(String ldpath) {
        criteria.add(new Criteria(BODY_PREFIX + ldpath, Comparison.EQ));
        return this;
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
        criteria.add(new Criteria(ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Annotation object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setAnnotationCriteria(String ldpath, Number value, Comparison comparison) {
        criteria.add(new Criteria(ldpath, value, comparison));
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
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects. Compared to the
     * other <i>setAnnotationCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setAnnotationCriteria(String ldpath, Number value) {
        return setAnnotationCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @return itself to allow chaining.
     */
    public QueryService setAnnotationCriteria(String ldpath) {
        criteria.add(new Criteria(ldpath, Comparison.EQ));
        return this;
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
        criteria.add(new Criteria(SELECTOR_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Selector object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSelectorCriteria(String ldpath, Number value, Comparison comparison) {
        criteria.add(new Criteria(SELECTOR_PREFIX + ldpath, value, comparison));
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
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects. Compared to the
     * other <i>setSelectorCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Selector object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSelectorCriteria(String ldpath, Number value) {
        return setSelectorCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Selector object
     * @return itself to allow chaining.
     */
    public QueryService setSelectorCriteria(String ldpath) {
        criteria.add(new Criteria(SELECTOR_PREFIX + ldpath, Comparison.EQ));
        return this;
    }

    /**
     * @param ldpath     Syntax similar to XPath. Beginning from the Source object
     * @param value      The constraint value
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, String value, Comparison comparison) {
        criteria.add(new Criteria(SOURCE_PREFIX + ldpath, value, comparison));
        return this;
    }

    /**
     * @param ldpath     Syntax similar to XPath. Beginning from the Source object
     * @param value      The constraint value
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, Number value, Comparison comparison) {
        criteria.add(new Criteria(SOURCE_PREFIX + ldpath, value, comparison));
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
     * @param ldpath Syntax similar to XPath. Beginning from the Source object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, Number value) {
        return setSourceCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * @param ldpath Syntax similar to XPath. Beginning from the Source object
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath) {
        criteria.add(new Criteria(SOURCE_PREFIX + ldpath, Comparison.EQ));
        return this;
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
     * criteria specified by the user.
     *
     * @param <T>
     * @return the result set
     */
    public <T> List<T> execute() throws ParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection con = objectRepository.getConnection();
        String sparql = EvalQuery.evaluate(criteria, prefixes);

        logger.info("Created query:\n" + prettyPrint(sparql));

        ObjectQuery query = con.prepareObjectQuery(sparql);
        return (List<T>) query.evaluate(this.type).asList();
    }

    /**
     * Reformats the SPARQL query for logging purpose
     *
     * @param sparql The generated SPARQL query
     *
     * @return Formatted query
     */
    public String prettyPrint(String sparql) {
        return OpAsQuery.asQuery(Algebra.compile(QueryFactory.create(sparql))).serialize();
    }
}