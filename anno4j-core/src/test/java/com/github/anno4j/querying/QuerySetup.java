package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * The QuerySetup abstract class bundles Services and processes that are
 * needed to write tests for the Anno4j QueryService.
 */
public abstract class QuerySetup {
    protected QueryService queryService = null;

    /**
     * Setting up the test environment. It will initialize Anno4j and its QueryService. Besides that,
     * a default test namespace will be set ("ex", "http://www.example.com/schema#") and at the end,
     * it triggers the persistTestData function of the particular test method.
     *
     * @throws RepositoryConfigException
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Before
    public void setupUpQueryTest() throws RepositoryConfigException, RepositoryException, IllegalAccessException, InstantiationException {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
        queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        queryService.addPrefix("ex", "http://www.example.com/schema#");
        this.persistTestData();
    }

    /**
     * Persists the test data which will be querried from the particular
     * test classes. This method has to be implemented by the actual test class.
     *
     * @throws RepositoryException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public abstract void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException;
}
