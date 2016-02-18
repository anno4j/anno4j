package com.github.anno4j;

import com.github.anno4j.annotations.Evaluator;
import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.TestEvaluator;
import org.apache.commons.lang3.ClassUtils;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;


/**
 * Read and write API for W3C Web Annotation Data Model (http://www.w3.org/TR/annotation-model/) and W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/).
 * <p/>
 * <br/><br/>Anno4j can be configured by using the specific setter-methodes (e.g. setIdGenerator, setRepository). A default configuration (in-memory SPARQL endpoint) will be used if no configuration is set.
 * <p/>
 * <br/><br/> Usage: Anno4j implements a singelton pattern. The getInstance() methode can be called to get a Anno4j object.
 */
public class Anno4j {

    /**
     * Logger of this class.
     */
    private final Logger logger = LoggerFactory.getLogger(Anno4j.class);
    private IDGenerator idGenerator;

    /**
     * Configured openrdf/sesame repository for connecting a local/remote SPARQL endpoint.
     */
    private Repository repository;

    /**
     * Wrapper of the repository field for alibaba, will be updated if a new repository is set.
     */
    private ObjectRepository objectRepository;

    /**
     * Wrapper to store the evaluators for the different LDPath components
     */
    private LDPathEvaluatorConfiguration evaluatorConfiguration = new LDPathEvaluatorConfiguration();

    /**
     * Stores alls partial implementations of the defined interfaces, such as the ResourceObject or the
     * Annotation interface.
     */
    private Set<Class<?>> partialClasses;


    public Anno4j() throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()));
    }

    public Anno4j(IDGenerator idGenerator) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), idGenerator);
    }

    public Anno4j(Repository repository) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN());
    }

    public Anno4j(Repository repository, IDGenerator idGenerator) throws RepositoryConfigException, RepositoryException {
        this.idGenerator = idGenerator;

        Set<URL> classpath = new HashSet<>();
        classpath.addAll(ClasspathHelper.forClassLoader());
        classpath.addAll(ClasspathHelper.forJavaClassPath());
        classpath.addAll(ClasspathHelper.forManifest());
        classpath.addAll(ClasspathHelper.forPackage(""));

        scanForPartials(classpath);
        scanForEvaluators(classpath);

        if(!repository.isInitialized()) {
            repository.initialize();
        }

        this.setRepository(repository);
    }

    private void scanForPartials(Set<URL> classpath) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(classpath)
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
        this.partialClasses = reflections.getTypesAnnotatedWith(Partial.class, true);
        
    }
    
    private void scanForEvaluators(Set<URL> classpath) {
   

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(classpath)
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));

        Set<Class<?>> defaultEvaluatorAnnotations = reflections.getTypesAnnotatedWith(Evaluator.class, true);

        Map<Class<? extends TestFunction>, Class<QueryEvaluator>> testFunctionEvaluators = new HashMap<>();
        Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> defaultEvaluators = new HashMap<>();
        Map<Class<? extends NodeTest>, Class<TestEvaluator>> testEvaluators = new HashMap<>();
        Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> functionEvaluators = new HashMap<>();

        for (Class clazz : defaultEvaluatorAnnotations) {
            Evaluator evaluator = (Evaluator) clazz.getAnnotation(Evaluator.class);

            if (ClassUtils.isAssignable(evaluator.value(), TestFunction.class)) {
                logger.debug("Found evaluator {} for TestFunction {}", clazz.getCanonicalName(), evaluator.value().getCanonicalName());
                testFunctionEvaluators.put((Class<? extends TestFunction>) evaluator.value(), clazz);
            } else if (ClassUtils.isAssignable(evaluator.value(), NodeTest.class)) {
                logger.debug("Found evaluator {} for NodeTest {}", clazz.getCanonicalName(), evaluator.value().getCanonicalName());
                testEvaluators.put((Class<? extends NodeTest>) evaluator.value(), clazz);
            } else if (ClassUtils.isAssignable(evaluator.value(), SelectorFunction.class)) {
                logger.debug("Found evaluator {} for NodeFunction {}", clazz.getCanonicalName(), evaluator.value().getCanonicalName());
                functionEvaluators.put((Class<? extends SelectorFunction>) evaluator.value(), clazz);
            } else {
                logger.debug("Found evaluator {} for NodeSelector {}", clazz.getCanonicalName(), evaluator.value().getCanonicalName());
                defaultEvaluators.put((Class<? extends NodeSelector>) evaluator.value(), clazz);
            }
        }

        evaluatorConfiguration.setDefaultEvaluators(defaultEvaluators);
        evaluatorConfiguration.setTestEvaluators(testEvaluators);
        evaluatorConfiguration.setTestFunctionEvaluators(testFunctionEvaluators);
        evaluatorConfiguration.setFunctionEvaluators(functionEvaluators);
    }

    /**
     * Writes the resource object to the configured SPARQL endpoint with a corresponding INSERT query.
     * @param resource resource object to write to the SPARQL endpoint
     * @throws RepositoryException
     */
    public void persist(ResourceObject resource) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();
        connection.addObject(resource);
    }

    /**
     * Writes the resource object to the configured SPARQL endpoint with a corresponding INSERT query.
     * @param resource resource object to write to the SPARQL endpoint
     * @param graph Graph context to query
     * @throws RepositoryException
     */
    public void persist(ResourceObject resource, URI graph) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();

        if(graph != null) {
            connection.setReadContexts(graph);
            connection.setInsertContext(graph);
            connection.setRemoveContexts(graph);
        }

        connection.addObject(resource);
    }


    public <T extends ResourceObject> T findByID (Class<T> type, String id) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();
        T result = null;

        try {
            result = connection.getObject(type, id);
        } catch (RepositoryException e) {
            throw e;
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query", e);
        }

        return result;
    }

    public <T extends ResourceObject> T findByID (Class<T> type, URI id) throws RepositoryException {
        return this.findByID(type, id.toString());
    }

    /**
     * Removes all triples from the given context.
     * @param context context to clear
     * @throws RepositoryException
     */
    public void clearContext(URI context) throws RepositoryException {
        objectRepository.getConnection().clear(context);
    }

    /**
     * Removes all triples from the given context.
     * @param context context to clear
     * @throws RepositoryException
     */
    public void clearContext(String context) throws RepositoryException {
        this.clearContext(new URIImpl(context));
    }

    /**
     * Queries for all instances of the RDF class connected with the given class
     * @param type Class with connected RDF type
     * @return All instances of the given RDF type
     * @throws RepositoryException
     */
    public <T extends ResourceObject> List<T> findAll(Class<T> type) throws RepositoryException {
        try {
            return objectRepository.getConnection().getObjects(type).asList();
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query" , e);
        }
    }

    public <T extends ResourceObject> List<T> findAll(Class<T> type, URI context) throws RepositoryException {
        try {
            ObjectConnection con = objectRepository.getConnection();
            if (context != null) {
                con.setReadContexts(context);
                con.setInsertContext(context);
                con.setRemoveContexts(context);
            }

            return con.getObjects(type).asList();
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query" , e);
        }
    }

    /**
     * Create query service
     *
     * @return query service object for specified type
     */
    public QueryService createQueryService() {
        return new QueryService(objectRepository, evaluatorConfiguration);
    }

    /**
     * Create query service
     *
     * @param graph Graph context to query
     * @return query service object for specified type
     */
    public QueryService createQueryService(URI graph) {
        return new QueryService(objectRepository, evaluatorConfiguration, graph);
    }

    /**
     * Getter for the configured Repository instance (Connector for local/remote SPARQL repository).
     *
     * @return configured Repository instance
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Configures the Repository (Connector for local/remote SPARQL repository) to use in Anno4j.
     *
     * @param repository Repository to use in Anno4j.
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public void setRepository(Repository repository) throws RepositoryException, RepositoryConfigException {
        this.repository = repository;
        // update alibaba wrapper

        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepositoryConfig config = factory.getConfig();

        if(partialClasses != null) {
            for(Class<?> clazz : this.partialClasses){
                config.addBehaviour(clazz);
            }
        }

        this.objectRepository = new ObjectRepositoryFactory().createRepository(config, repository);
        this.objectRepository.setIdGenerator(idGenerator);
    }

    /**
     * Getter for configured ObjectRepository (openrdf/alibaba wrapper for the internal Repository).
     *
     * @return configured ObjectRepository.
     */
    public ObjectRepository getObjectRepository() {
        return objectRepository;
    }

    public <T> T createObject (Class<T> clazz) throws RepositoryException, IllegalAccessException, InstantiationException {
        ObjectConnection con = getObjectRepository().getConnection();
        ObjectFactory objectFactory = con.getObjectFactory();
        return objectFactory.createObject(IDGenerator.BLANK_RESOURCE, clazz);
    }

    public <T> T createObject (Class<T> clazz, URI context) throws RepositoryException, IllegalAccessException, InstantiationException {
        ObjectConnection con = getObjectRepository().getConnection();
        if (context != null) {
            con.setReadContexts(context);
            con.setInsertContext(context);
            con.setRemoveContexts(context);
        }
        ObjectFactory objectFactory = con.getObjectFactory();
        return objectFactory.createObject(IDGenerator.BLANK_RESOURCE, clazz);
    }

    /**
     * Creates object with the same read,write,remove context as the given resource object
     */
    public <T,S extends ResourceObject> T createObject (Class<T> clazz, S resourceObject) throws RepositoryException, IllegalAccessException, InstantiationException {
        ObjectConnection con = getObjectRepository().getConnection();

        if (resourceObject != null) {
            con.setReadContexts(resourceObject.getObjectConnection().getReadContexts());
            con.setInsertContext(resourceObject.getObjectConnection().getInsertContext());
            con.setRemoveContexts(resourceObject.getObjectConnection().getRemoveContexts());
        }

        ObjectFactory objectFactory = con.getObjectFactory();

        return objectFactory.createObject(IDGenerator.BLANK_RESOURCE, clazz);
    }

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.objectRepository.setIdGenerator(idGenerator);
    }
}