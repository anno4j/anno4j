package com.github.anno4j.similarity;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.similarity.impl.TestBody1;
import com.github.anno4j.similarity.impl.TestBody2;
import com.github.anno4j.similarity.ontologies.ANNO4JREC;
import org.junit.Before;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

/**
 * Created by Manu on 12/04/16.
 */
public abstract class SimilarityTestSetup {

    protected QueryService queryService;
    protected Anno4j anno4j;

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
        this.anno4j = new Anno4j();
        this.queryService = anno4j.createQueryService().addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);
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
    protected abstract void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException;

    protected void createBaseLineSimilarityAnnotations() throws RepositoryException, IllegalAccessException, InstantiationException {
        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody2 body = this.anno4j.createObject(TestBody2.class);

            anno.setBody(body);

            this.anno4j.persist(anno);
        }

        for (int i = 0; i <= 1; ++i) {
            Annotation anno = this.anno4j.createObject(Annotation.class);
            TestBody1 body = this.anno4j.createObject(TestBody1.class);

            if(i == 0) {
                body.setValue("test");
            }

            anno.setBody(body);

            this.anno4j.persist(anno);
        }
    }
}
