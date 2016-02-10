package com.github.anno4j.querying;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.*;
import com.github.anno4j.querying.evaluation.EvalQuery;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.QueryExtension;
import com.hp.hpl.jena.query.Query;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.model.Constants;
import org.apache.marmotta.ldpath.parser.Configuration;
import org.apache.marmotta.ldpath.parser.DefaultConfiguration;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.URI;
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
 * The QueryService allows to query triple stores by using criteria. Furthermore
 * this is provided by simple classes. This is why the user does not need to write SPARQL queries
 * by himself.
 *
 * @author Andreas Eisenkolb (andreas.eisenkolb@gmail.com)
 */
public class QueryService {

    private final Logger logger = LoggerFactory.getLogger(QueryService.class);
    private final URI graph;

    /**
     * The repository needed for the actual querying
     */
    private ObjectRepository objectRepository;

    /**
     * Bundles the:
     *
     * <ul>
     *     <li>prefixes</li>
     *     <li>criteria</li>
     *     <li>configuration</li>
     * </ul>
     *
     * into a single object, so it can be passed to the
     * EvalQuery object for further processing.
     */
    private QueryServiceConfiguration queryServiceDTO;

    /**
     * Limit value of the query
     */
    private Integer limit = null;

    /**
     * Offset value for the query
     */
    private Integer offset = null;

    /**
     * Object to apply optimization strategies to SPARQL queries
     */
    private QueryOptimizer queryOptimizer = null;

    public <T> QueryService(ObjectRepository objectRepository, LDPathEvaluatorConfiguration evaluatorConfiguration) {
        this(objectRepository, evaluatorConfiguration, null);
    }

    public <T> QueryService(ObjectRepository objectRepository, LDPathEvaluatorConfiguration evaluatorConfiguration, URI graph) {
        queryServiceDTO = new QueryServiceConfiguration();
        this.objectRepository = objectRepository;

        // Setting some common name spaces
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

        this.queryOptimizer = QueryOptimizer.getInstance();
        this.graph = graph;
        queryServiceDTO.setEvaluatorConfiguration(evaluatorConfiguration);
        queryServiceDTO.setConfiguration(createLDPathConfiguration());
    }

    private Configuration createLDPathConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration();

        for (Map.Entry<Class<? extends TestFunction>, Class<QueryEvaluator>> entry : queryServiceDTO.getEvaluatorConfiguration().getTestFunctionEvaluators().entrySet()) {
            try {
                TestFunction newInstance = entry.getKey().newInstance();
                config.addTestFunction(Constants.NS_LMF_FUNCS + newInstance.getLocalName(), newInstance);
                logger.debug("Registering TestFunction " + entry.getKey().getCanonicalName());
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate TestFunction: " + entry.getKey().getCanonicalName());
            }
        }

        for (Map.Entry<Class<? extends SelectorFunction>, Class<QueryEvaluator>> entry : queryServiceDTO.getEvaluatorConfiguration().getFunctionEvaluators().entrySet()) {
            try {
                SelectorFunction newInstance = entry.getKey().newInstance();
                config.addFunction(Constants.NS_LMF_FUNCS + newInstance.getPathExpression(new SesameValueBackend()), newInstance);
                logger.debug("Registering Function " + entry.getKey().getCanonicalName());
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate Function: " + entry.getKey().getCanonicalName());
            }
        }

        return config;
    }

    public Map<String, String> getPrefixes() {
        return queryServiceDTO.getPrefixes();
    }

    public ArrayList<Criteria> getCriteria() {
        return queryServiceDTO.getCriteria();
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath     Syntax similar to XPath. Beginning from the Annotation object
     * @param comparison The comparison mode, e.g. Comparison.EQ (=)
     * @param value      The constraint value
     * @return itself to allow chaining.
     */
    public QueryService addCriteria(String ldpath, String value, Comparison comparison) {
        queryServiceDTO.getCriteria().add(new Criteria(ldpath, value, comparison));
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
    public QueryService addCriteria(String ldpath, Number value, Comparison comparison) {
        queryServiceDTO.getCriteria().add(new Criteria(ldpath, value, comparison));
        return this;
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects. Compared to the
     * other <i>addCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService addCriteria(String ldpath, String value) {
        return addCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects. Compared to the
     * other <i>addCriteria</i> function, this function does not need a Comparison statement. Hence, the
     * <b>Comparison.EQ</b> statement ("=") will be used automatically.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @param value  The constraint value
     * @return itself to allow chaining.
     */
    public QueryService addCriteria(String ldpath, Number value) {
        return addCriteria(ldpath, value, Comparison.EQ);
    }

    /**
     * Setting a criteria for filtering eu.mico.platform.persistence.impl.AnnotationImpl objects.
     *
     * @param ldpath Syntax similar to XPath. Beginning from the Annotation object
     * @return itself to allow chaining.
     */
    public QueryService addCriteria(String ldpath) {
        queryServiceDTO.getCriteria().add(new Criteria(ldpath, Comparison.EQ));
        return this;
    }

    /**
     * Adding a criteria object to the QueryService
     *
     * @param criteria The criteria object
     * @return itself to allow chaining.
     */
    public QueryService addCriteria(Criteria criteria) {
        queryServiceDTO.getCriteria().add(criteria);
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
        queryServiceDTO.getPrefixes().put(label, url);
        return this;
    }

    /**
     * Setting multiple names for URI prefixes.
     *
     * @param prefixes HashMap with multiple namespaces.
     * @return itself to allow chaining.
     */
    public QueryService addPrefixes(HashMap<String, String> prefixes) {
        queryServiceDTO.getPrefixes().putAll(prefixes);
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
     * @return the result set of annotations
     */
    public List<Annotation> execute() throws ParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        return this.execute(Annotation.class);
    }

    /**
     * Creates and executes the SPARQL query according to the
     * criteria specified by the user.
     *
     * @param <T> type Type of the expected result.
     * @return the result set
     */
    public <T extends ResourceObject> List<T> execute(Class<T> type) throws ParseException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection con = objectRepository.getConnection();

        if (graph != null) {
            con.setReadContexts(graph);
            con.setInsertContext(graph);
            con.setRemoveContexts(graph);
        }

        URI rootType = objectRepository.getConnection().getObjectFactory().getNameOf(type);
        if (rootType == null) {
            throw new IllegalArgumentException("Can't query for: " + type + " Missing name of type. Is @Iri annotation set?");
        }

        Query sparql = EvalQuery.evaluate(queryServiceDTO, rootType);

        if (limit != null) {
            sparql.setLimit(limit);
        }

        if (offset != null) {
            sparql.setOffset(offset);
        }

        // Print with line numbers
//        sparql.serialize(new IndentedWriter(System.out, true));
//        System.out.println();

        String q = sparql.serialize();
        logger.debug("Initial query:\n" + queryOptimizer.prettyPrint(q));

        // Optimize the join order
        q = queryOptimizer.optimizeJoinOrder(q);
        logger.debug("Query after join order optimization:\n " + q);

        ObjectQuery query = con.prepareObjectQuery(q);

        if (query.getDataset() != null) {
            logger.info("\nGRAPH CONTEXT = " + query.getDataset().getDefaultGraphs() + "\nFINAL QUERY :\n" + q);
        } else {
            logger.info("\nFINAL QUERY :\n" + q);
        }

        return (List<T>) query.evaluate().asList();
    }

    public Configuration getConfiguration() {
        return queryServiceDTO.getConfiguration();
    }

    public LDPathEvaluatorConfiguration getEvaluatorConfiguration() {
        return queryServiceDTO.getEvaluatorConfiguration();
    }

    /**
     * Creating and returning an instance of the passed type. This allows the
     * user to invoke specific methods of the extension class without
     * loosing convenience of the fluid interface, Anno4j provides.
     *
     * @param type the type of the extension.
     * @param <S>  generic, because the type of the extension class can differ.
     * @return an instance of the passed class
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <S extends QueryExtension> S useExtension(Class<S> type) throws IllegalAccessException, InstantiationException {
        QueryExtension q = type.newInstance();
        q.setQueryService(this);
        return (S) q;
    }
}