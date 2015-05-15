package com.github.anno4j.querying;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.ontologies.*;
import org.apache.marmotta.ldpath.api.backend.NodeBackend;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.selectors.*;
import org.apache.marmotta.ldpath.model.tests.IsATest;
import org.apache.marmotta.ldpath.parser.LdPathParser;
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The QueryService allows to query the MICO triple stores by using criterias. Furthermore
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
     * All user defined criterias
     */
    private ArrayList<Criteria> criterias = new ArrayList<Criteria>();

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
        criterias.add(new Criteria(BODY_PREFIX + ldpath, value, comparison));
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
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Annotation object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setAnnotationCriteria(String ldpath, Number value, Comparison comparison) {
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
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.SelectorImpl.* objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Selector object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSelectorCriteria(String ldpath, Number value, Comparison comparison) {
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
     * @param ldpath     Syntax similar to XPath. Beginning from the Source object
     * @param value      The constraint value
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, Number value, Comparison comparison) {
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
     * @param ldpath Syntax similar to XPath. Beginning from the Source object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService setSourceCriteria(String ldpath, Number value) {
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
    public <T> List<T> execute() throws ParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection con = objectRepository.getConnection();
        ObjectQuery query = con.prepareObjectQuery(createQuery());

        return (List<T>) query.evaluate(this.type).asList();
    }

    /**
     * Converts itself to a executable SPARQL query.
     *
     * @return the SPARQL query as string
     */
    private String createQuery() throws ParseException {

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
        query
                .append(System.getProperty("line.separator"))
                .append("SELECT ?annotation ")
                .append(System.getProperty("line.separator"))
                .append("WHERE {")
                .append(System.getProperty("line.separator"))
                .append("?annotation a oa:Annotation .")
                .append(System.getProperty("line.separator"));

        SesameValueBackend backend = new SesameValueBackend();

        // Creating the actual statements
        for (Criteria criteria : criterias) {

            query.append("{ ").append(System.getProperty("line.separator"));

            LdPathParser parser = new LdPathParser(backend, new StringReader(criteria.getLdpath()));

            String variableName = resolveLDPath(parser.parseSelector(prefixes), backend, query, "annotation");

            evalComparison(query, criteria, variableName);

            query.append("}").append(System.getProperty("line.separator"));
        }

        query.append("}");

        logger.info("Created query:\n" + query.toString());

        return query.toString();
    }

    /**
     * Evaluates the comparison method defined in the Criteria object.
     *
     * @param query        StringBuilder for the SPARQL query
     * @param criteria     The current Criteria Object
     * @param variableName The latest created variable name
     */
    private void evalComparison(StringBuilder query, Criteria criteria, String variableName) {
        if (Comparison.EQ.equals(criteria.getComparison())) {
            query
                    .append("FILTER regex( ?")
                    .append(variableName)
                    .append((criteria.isNaN()) ? ", \"" : ", ") // Adding quotes if the given value is not a number
                    .append(criteria.getConstraint())
                    .append((criteria.isNaN()) ? "\" ) ." : " ) .") // Adding quotes if the given value is not a number
                    .append(System.getProperty("line.separator"));
        } else {
            if (!criteria.isNaN()) {
                query
                        .append("FILTER ( ?")
                        .append(variableName)
                        .append(" ")
                        .append(criteria.getComparison().getSparqlOperator())
                        .append(" ")
                        .append(criteria.getConstraint())
                        .append(" ) .")
                        .append(System.getProperty("line.separator"));
            } else {
                throw new IllegalStateException(criteria.getComparison() + " only allowed on Numbers.");
            }
        }
    }

    /**
     * Function to resolve the LDPath. Recursively splits the LDPath expression into the
     * separate parts and evaluate them. More specifically it creates the SPARQL query portions
     * for each considered part.
     *
     * @param nodeSelector The current NodeSelector of the LDPath
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String resolveLDPath(NodeSelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        if (nodeSelector instanceof PropertySelector) {
            return evalPropertySelector((PropertySelector) nodeSelector, backend, query, variableName);
        } else if (nodeSelector instanceof PathSelector) {
            return evalPathSelector((PathSelector) nodeSelector, backend, query, variableName);
        } else if (nodeSelector instanceof TestingSelector) {
            return evalTestingSelector((TestingSelector) nodeSelector, backend, query, variableName);
        } else if (nodeSelector instanceof GroupedSelector) {
            return evalGroupedSelector((GroupedSelector) nodeSelector, backend, query, variableName);
        } else if (nodeSelector instanceof UnionSelector) {
            return evalUnionSelector((UnionSelector) nodeSelector, backend, query, variableName);
        } else {
            throw new IllegalStateException(nodeSelector.getClass() + " is not supported.");
        }
    }

    /**
     * Evaluates an UnionSelector. More precisely, it creates the UNION part of the SPARQL query.
     *
     * @param nodeSelector The UnionSelector to evaluate
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String evalUnionSelector(UnionSelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        UnionSelector sel = nodeSelector;

        query
                .append("{")
                .append(System.getProperty("line.separator"));

        String leftVarName = resolveLDPath(sel.getLeft(), backend, query, variableName);

        query
                .append("} UNION {")
                .append(System.getProperty("line.separator"));

        String rightVarName = resolveLDPath(sel.getRight(), backend, query, variableName);

        query
                .append("}")
                .append(System.getProperty("line.separator"));

        replaceAll(query, Pattern.compile("\\?" + rightVarName), "?" + leftVarName);

        return leftVarName;
    }

    /**
     * Evaluates the complex GroupedSelector.
     *
     * @param nodeSelector The GroupedSelector to evaluate
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String evalGroupedSelector(GroupedSelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        GroupedSelector sel = nodeSelector;
        return resolveLDPath(sel.getContent(), backend, query, variableName);
    }

    /**
     * Evaluates the TestingSelector.
     *
     * @param nodeSelector The TestingSelector to evaluate
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String evalTestingSelector(TestingSelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        TestingSelector sel = nodeSelector;

        NodeTest nodeTest = sel.getTest();
        if (nodeTest instanceof IsATest) {
            String delVarName = resolveLDPath(sel.getDelegate(), backend, query, variableName);

            IsATest isATest = (IsATest) nodeTest;
            query
                    .append("?")
                    .append(delVarName)
                    .append(" ")
                    .append(isATest.getPathExpression(backend).replaceFirst("is-", ""))
                    .append(" .")
                    .append(System.getProperty("line.separator"));

            return delVarName;
        } else {
            throw new IllegalStateException(nodeTest.getClass() + " is not supported.");
        }
    }

    /**
     * Evaluates the PropertySelector and creates the corresponding SPARQL statement.
     *
     * @param nodeSelector The PropertySelector to evaluate
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String evalPropertySelector(PropertySelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        PropertySelector sel = nodeSelector;
        ++varIndex;

        query
                .append("?")
                .append(variableName)
                .append(" ")
                .append(sel.getPathExpression(backend))
                .append(" ?var")
                .append(varIndex)
                .append(" .")
                .append(System.getProperty("line.separator"));

        return "var" + varIndex;
    }

    /**
     * @param nodeSelector
     * @param backend      The NodeBackend
     * @param query        StringBuilder for creating the actual query parts
     * @param variableName The latest created variable name
     * @return the latest variable name
     */
    private String evalPathSelector(PathSelector nodeSelector, NodeBackend backend, StringBuilder query, String variableName) {
        PathSelector sel = nodeSelector;
        String leftVarName = resolveLDPath(sel.getLeft(), backend, query, variableName);
        return resolveLDPath(sel.getRight(), backend, query, leftVarName);
    }

    /**
     * String replace function on a StringBuilder.
     *
     * @param sb          The StringBuilder
     * @param pattern     The pattern
     * @param replacement The replacement String
     */
    private static void replaceAll(StringBuilder sb, Pattern pattern, String replacement) {
        Matcher m = pattern.matcher(sb);
        while (m.find()) {
            sb.replace(m.start(), m.end(), replacement);
        }
    }
}