package com.github.anno4j;

import com.github.anno4j.persistence.IDGenerator;
import com.github.anno4j.persistence.PersistenceService;
import com.github.anno4j.persistence.impl.IDGeneratorAnno4jURN;
import com.github.anno4j.querying.QueryService;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Read and write API for W3C Web Annotation Data Model (http://www.w3.org/TR/annotation-model/) and W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/).
 *
 * <br/><br/>Anno4j can be configured by using the specific setter-methodes (e.g. setIdGenerator, setRepository). A default configuration (in-memory SPARQL endpoint) will be used if no configuration is set.
 *
 * <br/><br/> Usage: Anno4j implements a singelton pattern. The getInstance() methode can be called to get a Anno4j object.
 */
public class Anno4j {

    /**
     * Logger of this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Anno4j.class);

    /**
     * Singleton instance of this class.
     */
    private static Anno4j instance = null;

    /**
     * Configured ID generator for Anno4j.
     */
    private IDGenerator idGenerator = new IDGeneratorAnno4jURN();

    /**
     * Configured openrdf/sesame repository for connecting a local/remote SPARQL endpoint.
     */
    private Repository repository;

    /**
     * Wrapper of the repository field for alibaba, will be updated if a new repository is set.
     */
    private ObjectRepository objectRepository;


    /**
     * Private constructor because of singleton pattern
     */
    private Anno4j() {
        // In-memory default configuration
        Repository sailRepository = new SailRepository(new MemoryStore());
        try {
            sailRepository.initialize();
            this.setRepository(sailRepository);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create persistence object
     * @return persistence object
     */
    public PersistenceService createPersistenceService() {
        return new PersistenceService(objectRepository);
    }

    /**
     * Create persistence object
     * @param graph Graph context to query
     * @return persistence object
     */
    public PersistenceService createPersistenceService(URI graph) {
        return new PersistenceService(objectRepository, graph);
    }

    /**
     * Create query service
     * @param clazz Result type
     * @return query service object for specified type
     */
    public QueryService createQueryService(Class clazz) {
        return new QueryService(clazz, objectRepository);
    }

    /**
     * Create query service
     * @param clazz Result type
     * @param graph Graph context to query
     * @return query service object for specified type
     */
    public QueryService createQueryService(Class clazz, URI graph) {
        return new QueryService(clazz, objectRepository, graph);
    }

    /**
     * Getter for the configured IDGenerator intance.
     * @return configured IDGenerator instance.
     */
    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    /**
     * Configures the IDGenerator to use in Anno4j.
     * @param idGenerator IDGenerator to use in Anno4j.
     */
    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * Getter for the configured Repository instance (Connector for local/remote SPARQL repository).
     * @return configured Repository instance
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Configures the Repository (Connector for local/remote SPARQL repository) to use in Anno4j.
     * @param repository Repository to use in Anno4j.
     * @throws RepositoryException
     * @throws RepositoryConfigException
     */
    public void setRepository(Repository repository) throws RepositoryException, RepositoryConfigException {
        this.repository = repository;
        // update alibaba wrapper
        this.objectRepository = new ObjectRepositoryFactory().createRepository(repository);
    }

    /**
     * Getter for configured ObjectRepository (openrdf/alibaba wrapper for the internal Repository).
     * @return configured ObjectRepository.
     */
    public ObjectRepository getObjectRepository() {
        return objectRepository;
    }

    /**
     * Getter for the Anno4j getter instance.
     * @return singleton Anno4j instance.
     */
    public static Anno4j getInstance() {
        if(instance == null) {
            synchronized (Anno4j.class) {
                if(instance == null) {
                    instance = new Anno4j();
                }
            }
        }
        return instance;
    }
}