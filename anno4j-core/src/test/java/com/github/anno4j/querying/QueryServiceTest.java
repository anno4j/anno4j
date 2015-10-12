package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.SvgSelector;
import com.github.anno4j.model.impl.target.SpecificResource;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the QueryService.
 */
public class QueryServiceTest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");

        // Persisting some data
        Annotation annotation =  anno4j.createObject(Annotation.class);
        HashSet<Target> targets = new HashSet<>();
        SpecificResource specificResource =  anno4j.createObject(SpecificResource.class);
        SpecificResource resource2 =  anno4j.createObject(SpecificResource.class);
        resource2.setSelector( anno4j.createObject(SvgSelector.class));
        specificResource.setSelector(anno4j.createObject(FragmentSelector.class));
        targets.add(specificResource);
        targets.add(resource2);
        annotation.setTarget(targets);

        anno4j.createPersistenceService().persistAnnotation(annotation);
    }

    @Test
    public void testCustomLDPah() throws Exception {
        List<Object> list = queryService.addCriteria("fn:getSelector(.)[is-a oa:FragmentSelector]").execute();
        assertEquals(1, list.size());
    }
}