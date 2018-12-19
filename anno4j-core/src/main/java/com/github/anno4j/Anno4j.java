package com.github.anno4j;

import com.github.anno4j.annotations.Evaluator;
import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import com.github.anno4j.querying.extension.QueryEvaluator;
import com.github.anno4j.querying.extension.TestEvaluator;
import com.github.anno4j.querying.objectqueries.ObjectQueryService;
import com.github.anno4j.schema.OWLSchemaPersistingManager;
import com.github.anno4j.schema.SchemaPersistingManager;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ClassUtils;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.marmotta.ldpath.api.functions.SelectorFunction;
import org.apache.marmotta.ldpath.api.functions.TestFunction;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.api.tests.NodeTest;
import org.openrdf.annotations.Iri;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.result.Result;
import org.openrdf.sail.memory.MemoryStore;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;


/**
 * Read and write API for W3C Web Annotation Data Model (http://www.w3.org/TR/annotation-model/) and W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/).
 * <p/>
 * <br/><br/>Anno4j can be configured by using the specific setter-methodes (e.g. setIdGenerator, setRepository). A default configuration (in-memory SPARQL endpoint) will be used if no configuration is set.
 * <p/>
 * <br/><br/> Anno4j methods are not thread-safe. Use Anno4j transactions in threaded environment.
 */
@NotThreadSafe
public class Anno4j implements TransactionCommands {

    /**
     * Logger of this class.
     */
    private final Logger logger = LoggerFactory.getLogger(Anno4j.class);
    private final URI defaultContext;
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
     * Stores the {@link org.openrdf.annotations.Iri}-annotated interfaces in the classpath indexed by their IRI-string.
     */
    private Map<String, Class<? extends ResourceObject>> conceptsByIri = new HashMap<>();

    /**
     * Stores all partial implementations of the defined interfaces, such as the ResourceObject or the
     * Annotation interface.
     */
    private Set<Class<?>> partialClasses;

    /**
     * The classpath scanned for partial classes.
     */
    private Set<URL> classpath;


    public Anno4j() throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()));
    }

    public Anno4j(boolean persistSchemaAnnotations) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), null, persistSchemaAnnotations);
    }

    public Anno4j(URI defaultContext) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), defaultContext);
    }

    public Anno4j(IDGenerator idGenerator) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), idGenerator, null, true);
    }

    public Anno4j(IDGenerator idGenerator, URI defaultContext) throws RepositoryException, RepositoryConfigException {
        this(new SailRepository(new MemoryStore()), idGenerator, defaultContext, true);
    }

    public Anno4j(Repository repository) throws RepositoryException, RepositoryConfigException {
        this(repository, new IDGeneratorAnno4jURN(), null, true);
    }

    public Anno4j(Repository repository, IDGenerator idGenerator) throws RepositoryException, RepositoryConfigException {
        this(repository, idGenerator, null, true);
    }

    public Anno4j(Repository repository, URI defaultContext) throws RepositoryException, RepositoryConfigException {
        this(repository, new IDGeneratorAnno4jURN(), defaultContext, true);
    }

    public Anno4j(Repository repository, URI defaultContext, boolean persistSchemaAnnotations) throws RepositoryException, RepositoryConfigException {
        this(repository, new IDGeneratorAnno4jURN(), defaultContext, persistSchemaAnnotations);
    }

    public Anno4j(Repository repository, IDGenerator idGenerator, URI defaultContext, boolean persistSchemaAnnotations) throws RepositoryConfigException, RepositoryException {
        this(repository, idGenerator, defaultContext, persistSchemaAnnotations, Sets.<URL>newHashSet());
    }

    public Anno4j(Repository repository, IDGenerator idGenerator, URI defaultContext, boolean persistSchemaAnnotations, Set<URL> additionalClasses) throws RepositoryConfigException, RepositoryException {
        this.idGenerator = idGenerator;
        this.defaultContext = defaultContext;

        classpath = new HashSet<>();
        classpath.addAll(ClasspathHelper.forClassLoader());
        classpath.addAll(ClasspathHelper.forJavaClassPath());
        classpath.addAll(ClasspathHelper.forManifest());
        classpath.addAll(ClasspathHelper.forPackage(""));
        if(additionalClasses != null) {
            classpath.addAll(additionalClasses);
        }

        Reflections annotatedClasses = new Reflections(new ConfigurationBuilder()
                .setUrls(classpath)
                .useParallelExecutor()
                .filterInputsBy(FilterBuilder.parsePackages("-java, -javax, -sun, -com.sun"))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner(), new FieldAnnotationsScanner()));

        // Bugfix: Searching for Reflections creates a lot ot Threads, that are not closed at the end by themselves,
        // so we close them manually.
        annotatedClasses.getConfiguration().getExecutorService().shutdown();

        // Index conceptsByIri with @Iri annotation:
        indexConcepts(annotatedClasses);

        // find classes with @Partial annotation
        this.partialClasses = annotatedClasses.getTypesAnnotatedWith(Partial.class, true);

        scanForEvaluators(annotatedClasses);

        if(!repository.isInitialized()) {
            repository.initialize();
        }

        this.setRepository(repository, additionalClasses, additionalClasses);

        // Persist schema information to repository:
        if(persistSchemaAnnotations) {
            persistSchemaAnnotations(annotatedClasses);
        }
    }

    /**
     * Persists the schema information implied by schema annotations to the default graph of the connected triplestore.
     * Performs a validation that the schema annotations are consistent.
     * @param types The types which methods and field should be scanned for schema information.
     * @throws RepositoryException Thrown if an error occurs while persisting schema information.
     * @throws SchemaPersistingManager.InconsistentAnnotationException Thrown if the schema annotations are inconsistent.
     * @throws SchemaPersistingManager.ContradictorySchemaException Thrown if the schema information imposed by annotations contradicts with
     * schema information that is already present in the connected triplestore.
     */
    private void persistSchemaAnnotations(Reflections types) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.begin();

        try {
            SchemaPersistingManager persistingManager = new OWLSchemaPersistingManager(transaction.getConnection());
            persistingManager.persistSchema(types);

        } catch (SchemaPersistingManager.InconsistentAnnotationException | SchemaPersistingManager.ContradictorySchemaException e) {
            // Rollback on error and rethrow exception:
            transaction.rollback();
            throw e;
        }

        transaction.commit();
    }
    
    private void scanForEvaluators(Reflections annotatedClasses) {
        Set<Class<?>> defaultEvaluatorAnnotations = annotatedClasses.getTypesAnnotatedWith(Evaluator.class, true);

        Map<Class<? extends TestFunction>, Class<QueryEvaluator>> testFunctionEvaluators = new HashMap<>();
        Map<Class<? extends NodeSelector>, Class<QueryEvaluator>> defaultEvaluators = new HashMap<>();
        Map<Class<? extends NodeTest>, Class<TestEvaluator>> testEvaluators = new HashMap<>();
        Map<Class<? extends SelectorFunction>, Class<QueryEvaluator>> functionEvaluators = new HashMap<>();

        for (Class clazz : defaultEvaluatorAnnotations) {
            Evaluator evaluator = (Evaluator) clazz.getAnnotation(Evaluator.class);

            Class[] functionClasses = evaluator.value();

            for(Class functionClass : functionClasses) {
                if (ClassUtils.isAssignable(functionClass, TestFunction.class)) {
                    logger.debug("Found evaluator {} for TestFunction {}", clazz.getCanonicalName(), functionClass.getCanonicalName());
                    testFunctionEvaluators.put((Class<? extends TestFunction>) functionClass, clazz);
                } else if (ClassUtils.isAssignable(functionClass, NodeTest.class)) {
                    logger.debug("Found evaluator {} for NodeTest {}", clazz.getCanonicalName(), functionClass.getCanonicalName());
                    testEvaluators.put((Class<? extends NodeTest>) functionClass, clazz);
                } else if (ClassUtils.isAssignable(functionClass, SelectorFunction.class)) {
                    logger.debug("Found evaluator {} for NodeFunction {}", clazz.getCanonicalName(), functionClass.getCanonicalName());
                    functionEvaluators.put((Class<? extends SelectorFunction>) functionClass, clazz);
                } else {
                    logger.debug("Found evaluator {} for NodeSelector {}", clazz.getCanonicalName(), functionClass.getCanonicalName());
                    defaultEvaluators.put((Class<? extends NodeSelector>) functionClass, clazz);
                }
            }
        }

        evaluatorConfiguration.setDefaultEvaluators(defaultEvaluators);
        evaluatorConfiguration.setTestEvaluators(testEvaluators);
        evaluatorConfiguration.setTestFunctionEvaluators(testFunctionEvaluators);
        evaluatorConfiguration.setFunctionEvaluators(functionEvaluators);
    }

    /**
     * Indexes all {@link Iri} annotated interfaces by their IRI.
     * @param reflections Reflections object having an {@link TypeAnnotationsScanner} capturing all classes in classpath.
     */
    private void indexConcepts(Reflections reflections) {
        // The conceptsByIri are @Iri annotated interfaces:
        for(Class<?> concept : reflections.getTypesAnnotatedWith(Iri.class)) {
            // Must be a sub-interface of ResourceObject:
            if(concept.isAnnotationPresent(Iri.class) && concept.isInterface() && ResourceObject.class.isAssignableFrom(concept)) {
                // Get the conceptsByIri annotation and IRI:
                Iri iriAnnotation = concept.getAnnotation(Iri.class);
                String iri = iriAnnotation.value();

                conceptsByIri.put(iri, (Class<? extends ResourceObject>) concept);
            }
        }
    }

    private ObjectConnection createObjectConnection(URI context) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();

        if(context != null) {
            connection.setReadContexts(context);
            connection.setInsertContext(context);
            connection.setRemoveContexts(context);
        } else if (defaultContext != null) {
            connection.setReadContexts(defaultContext);
            connection.setInsertContext(defaultContext);
            connection.setRemoveContexts(defaultContext);
        }

       return connection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void persist(ResourceObject resource) throws RepositoryException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }

        transaction.persist(resource);
    }

    /**
     * Writes the resource object to the configured SPARQL endpoint with a corresponding INSERT query.
     * @param resource resource object to write to the SPARQL endpoint
     * @param context Graph context to query
     * @throws RepositoryException
     */
    public void persist(ResourceObject resource, URI context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.setAllContexts(context);
        transaction.persist(resource);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> T findByID(Class<T> type, String id) throws RepositoryException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }

        return transaction.findByID(type, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> T findByID(Class<T> type, URI id) throws RepositoryException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }
        return transaction.findByID(type, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearContext(URI context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.clearContext(context);
        transaction.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearContext(String context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.clearContext(context);
        transaction.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> List<T> findAll(Class<T> type) throws RepositoryException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }

        return transaction.findAll(type);
    }

    public <T extends ResourceObject> List<T> findAll(Class<T> type, URI context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.setAllContexts(context);

        return transaction.findAll(type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T> T createObject(Class<T> clazz) throws RepositoryException, IllegalAccessException, InstantiationException {
        return createObject(clazz, null);
    }

    @Override
    public <T> T createObject(Class<T> clazz, Resource id) throws RepositoryException, IllegalAccessException, InstantiationException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }

        return transaction.createObject(clazz, id);
    }

    /**
     * Creates a instance of the given class.
     * @param clazz Class of the instance to create. Can be an annotated interface.
     * @param context The graph context where the triples are inserted into. can be null for default graph.
     * @return A instance of the given class.
     */
    public <T> T createObject(Class<T> clazz, URI context) throws RepositoryException, IllegalAccessException, InstantiationException {
        return createObject(clazz, context, null);
    }

    public <T> T createObject(Class<T> clazz, URI context, Resource id) throws RepositoryException, IllegalAccessException, InstantiationException {
        Transaction transaction = createTransaction();
        transaction.setAllContexts(context);

        return transaction.createObject(clazz, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryService createQueryService() throws RepositoryException {
        Transaction transaction = createTransaction();

        if(defaultContext != null) {
            transaction.setAllContexts(defaultContext);
        }

        return transaction.createQueryService();
    }

    /**
     * Create query service
     *
     * @param context context to query
     * @return query service object for specified type
     */
    public QueryService createQueryService(URI context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.setAllContexts(context);
        return transaction.createQueryService();
    }

    @Override
    public ObjectQueryService createObjectQueryService() throws RepositoryException, RepositoryConfigException {
        Transaction transaction = createTransaction();
        return transaction.createObjectQueryService();
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
        setRepository(repository, Sets.<URL>newHashSet(), Sets.<URL>newHashSet());
    }

    /**
     * Configures the Repository (Connector for local/remote SPARQL repository) to use in Anno4j.
     *
     * @param repository Repository to use in Anno4j.
     * @param conceptJars URLs of JAR-files that are scanned for conceptsByIri.
     * @param behaviourJars URLs of JAR-files that are scanned for behaviours.
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public void setRepository(Repository repository, Set<URL> conceptJars, Set<URL> behaviourJars) throws RepositoryException, RepositoryConfigException {
        this.repository = repository;
        // update alibaba wrapper

        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepositoryConfig config = factory.getConfig();

        for(URL conceptJar : conceptJars) {
            config.addConceptJar(conceptJar);
        }
        for(URL behaviourJar : behaviourJars) {
            config.addBehaviourJar(behaviourJar);
        }

        if(partialClasses != null) {
            for(Class<?> clazz : this.partialClasses){
                if (!clazz.getSimpleName().endsWith("AbstractClass")) {
                    config.addBehaviour(clazz);
                }
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

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.objectRepository.setIdGenerator(idGenerator);
    }

    public URI getDefaultContext() {
        return defaultContext;
    }

    /**
     * Returns a Java type that corresponds to a most specific class of which the given resource is an instance of.
     * This method can be used to instantiate objects (see {@link #createObject(Class)}) for a resource which type is
     * not known. Note that this method can return any most specific type if there exist multiple ones for the resource.
     * To get all most specific types of a resource you may use {@link #getConcepts(String)}.
     * @param resource The IRI of the resource for which to determine a most specific concept.
     * @return Returns any (explicitly stated) most specific type of the given resource. This method returns
     * {@link ResourceObject} if no type is stated for the given resource.
     * @throws RepositoryException Thrown if there is an error accessing the triplestore.
     */
    public Class<? extends ResourceObject> getConcept(String resource) throws RepositoryException {
        // Get all most specific concepts:
        Collection<Class<? extends ResourceObject>> concepts = getConcepts(resource);

        // Return any of those concepts:
        if(!concepts.isEmpty()) {
            return concepts.iterator().next();
        } else {
            return ResourceObject.class;
        }
    }

    /**
     * Returns all Java types that correspond to the most specific classes of which the given resource is an instance of.
     * This method can e.g. be used to instantiate (see {@link #createObject(Class)}) an object for a resource with
     * unknown types. Especially if there may not be a single Java type capturing all those classes. If there exists a Java type
     * for every resource or only an arbitrary type is needed, you may use {@link #getConcept(String)} instead.
     * @param resource The IRI of the resource for which to determine the most specific concepts.
     * @return Returns the most specific (explicitly stated) concepts of {@code resource}. If no types can be determined
     * the returned collection contains {@link ResourceObject}.
     * @throws RepositoryException Thrown if there is an error accessing the triplestore.
     */
    public Collection<Class<? extends ResourceObject>> getConcepts(String resource) throws RepositoryException {
        ObjectConnection connection = getObjectRepository().getConnection();
        try {
            /*
            Select any class c1 of which the resource is an instance and for which there is no
            other class c2, c2 != c1, that is a subclass of c1:
             */
            ObjectQuery query = connection.prepareObjectQuery(
                    "SELECT ?c1 {" +
                    "   <" + resource + "> a ?c1 . " +
                    "   MINUS {" +
                    "       <" + resource + "> a ?c2 . " +
                    "       ?c2 rdfs:subClassOf+ ?c1 . " +
                    "       FILTER(?c1 != ?c2)" +
                    "   }" +
                    "}"
            );
            Result<RDFObject> result = query.evaluate(RDFObject.class);

            Collection<Class<? extends ResourceObject>> concepts = new HashSet<>();
            while (result.hasNext()) {
                // Get the classes IRI:
                String clazzIri = result.next().toString();
                // Look up the corresponding Java class:
                if(conceptsByIri.containsKey(clazzIri)) {
                    concepts.add(conceptsByIri.get(clazzIri));
                }
            }
            // No class found? Every resource is instance of rdfs:Resource aka. ResourceObject:
            if(concepts.isEmpty()) {
                concepts.add(ResourceObject.class);
            }

            return concepts;

        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    public Transaction createTransaction() throws RepositoryException {
        return new Transaction(objectRepository, evaluatorConfiguration);
    }

    /**
     * Creates a transaction operating on the given context.
     * @param context The context the transaction should operate on.
     * @return Returns the transaction.
     * @throws RepositoryException Thrown if an error occurs regarding the connection to the triplestore.
     */
    public Transaction createTransaction(URI context) throws RepositoryException {
        Transaction transaction = createTransaction();
        transaction.setAllContexts(context); // Let the transaction operate on the given context
        return transaction;
    }

    /**
     * Creates a new transaction which provides schema validation services at commit time.
     * Note that the validation assumes that the state present is valid when {@link ValidatedTransaction#begin()}
     * is called.
     * Schema annotations made at {@link ResourceObject} classes will be scanned the first time a validated transaction
     * is created.
     * @return The validated transaction. {@link ValidatedTransaction#begin()} must be called afterwards.
     * @throws RepositoryException Thrown if an error occurs regarding the connection to the triplestore.
     */
    public ValidatedTransaction createValidatedTransaction() throws RepositoryException {
        return createValidatedTransaction(null);
    }

    /**
     * Creates a new transaction which provides schema validation services at commit time.
     * Note that the validation assumes that the state present is valid when {@link ValidatedTransaction#begin()}
     * is called.
     * Schema annotations made at {@link ResourceObject} classes will be scanned the first time a validated transaction
     * is created.
     * @param context The context on which the transaction will operate on.
     * @return The validated transaction. {@link ValidatedTransaction#begin()} must be called afterwards.
     * @throws RepositoryException Thrown if an error occurs regarding the connection to the triplestore.
     */
    public ValidatedTransaction createValidatedTransaction(URI context) throws RepositoryException {
        ValidatedTransaction transaction = new ValidatedTransaction(objectRepository, evaluatorConfiguration);
        transaction.setAllContexts(context);
        return transaction;
    }

    /**
     * Returns the classpath that is scanned for {@link org.openrdf.annotations.Iri} and
     * {@link Partial} annotated classes.
     * @return Returns the classpath scanned.
     */
    public Set<URL> getScannedClasspath() {
        return Collections.unmodifiableSet(classpath);
    }
}