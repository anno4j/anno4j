package com.github.anno4j;

import com.github.anno4j.persistence.IDGenerator;
import com.github.anno4j.persistence.PersistenceService;
import com.github.anno4j.persistence.impl.IDGeneratorLocalURN;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Read and write API for W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/)
 */
public class Anno4j {


    private static Anno4j instance = null;

    private IDGenerator idGenerator = new IDGeneratorLocalURN();
    private PersistenceService persistenceService = new PersistenceService();

    private Repository repository;
    private ObjectRepository objectRepository;


    private Anno4j() {
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

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) throws RepositoryException, RepositoryConfigException {
        this.repository = repository;
        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        this.objectRepository = factory.createRepository(repository);
    }

    public ObjectRepository getObjectRepository() {
        return objectRepository;
    }

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
