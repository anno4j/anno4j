package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.SvgSelector;
import com.github.anno4j.model.impl.target.SpecificResource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the QueryService.
 */
public class QueryServiceTest {

    private QueryService queryService = null;

    @Before
    public void resetQueryService() {
        queryService = Anno4j.getInstance().createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @BeforeClass
    public static void setUp() throws RepositoryException {
        // Persisting some data
        Annotation annotation = new Annotation();
        HashSet<Target> targets = new HashSet<>();
        SpecificResource specificResource = new SpecificResource();
        SpecificResource resource2 = new SpecificResource();
        resource2.setSelector(new SvgSelector());
        specificResource.setSelector(new FragmentSelector());
        targets.add(specificResource);
        targets.add(resource2);
        annotation.setTargets(targets);

        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);
    }

    @Test
    public void testCustomLDPah() throws Exception {
        List<Object> list = queryService.setAnnotationCriteria("fn:getSelector(.)[is-a oa:FragmentSelector]").execute();
        assertEquals(1, list.size());
    }
}