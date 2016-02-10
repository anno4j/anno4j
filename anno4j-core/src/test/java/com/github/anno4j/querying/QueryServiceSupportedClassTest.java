package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.SvgSelector;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.model.namespaces.OADM;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for an error state query to the QueryService.
 */
public class QueryServiceSupportedClassTest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Iri(OADM.NS + "something")
    private interface WorkingResourceObject extends ResourceObject {
        // An object with an @IRI annotation
    }

    private interface ErrorResourceObject extends ResourceObject {
        // An object without @IRI annotation
    }

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testErrorQueryClassSupport() throws Exception {
        queryService.execute(ErrorResourceObject.class);
    }

    @Test
    public void testQueryClassSupport() throws Exception {
        List<WorkingResourceObject> list = queryService.execute(WorkingResourceObject.class);
        assertEquals(0, list.size());
    }
}