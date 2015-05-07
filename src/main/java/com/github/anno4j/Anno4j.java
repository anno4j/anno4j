package com.github.anno4j;

import com.github.anno4j.exceptions.ConceptNotFoundException;
import com.github.anno4j.persistence.IDGenerator;
import com.github.anno4j.persistence.PersistenceService;
import com.github.anno4j.persistence.impl.IDGeneratorLocalURN;
import com.github.anno4j.querying.QueryService;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Read and write API for W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/)
 */
public class Anno4j {

    private static final String CONCEPT_PATH = "META-INF/org.openrdf.concepts";
    private static final Logger logger = LoggerFactory.getLogger(Anno4j.class);
    private static Anno4j instance = null;

    private IDGenerator idGenerator = new IDGeneratorLocalURN();

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

    public PersistenceService createPersistenceService() {
        return new PersistenceService(objectRepository);
    }

    public QueryService createQueryService(Class clazz) {
        return new QueryService(clazz, objectRepository);
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
                     // Check if the extractor has created the org.openrdf.concepts file. Alibaba requires this file (can be empty),
                    // to persist the annotated objects. If the file was not found, a ConceptNotFoundException will be thrown.
                    if (!new File(Anno4j.class.getClassLoader().getResource(CONCEPT_PATH).getFile()).isFile()) {
                        logger.error("No org.openrdf.conpepts file inside your META-INF directory");
                        throw new ConceptNotFoundException("Please create an empty org.openrdf.conpepts file inside your META-INF folder.");
                    }

                    instance = new Anno4j();
                }
            }
        }
        return instance;
    }

}
