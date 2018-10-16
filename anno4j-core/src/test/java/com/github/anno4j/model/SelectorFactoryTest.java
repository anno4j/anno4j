package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite for the SelectorFactory class.
 */
public class SelectorFactoryTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        // Empty given anno4j instance, as other data is persisted automatically
        this.clear();
    }

    @Test
    public void testMultipleSelectors() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        ResourceObject fragmentSpec = SelectorFactory.getMediaFragmentsSpecification(this.anno4j);

        List<ResourceObject> result = this.anno4j.findAll(ResourceObject.class);
        assertEquals(3, result.size());

        ResourceObject fragmentSpec2 = SelectorFactory.getMediaFragmentsSpecification(this.anno4j);
        assertEquals(3, result.size());

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", FragmentSpecification.W3C_MEDIA_FRAGMENTS.toString());

        List<ResourceObject> result2 = qs.execute(ResourceObject.class);
        assertEquals(1, result2.size());
    }

    private void clear() throws RepositoryException, UpdateExecutionException {
        String deleteUpdate = "DELETE {?s ?p ?o}\n" +
                "WHERE {?s ?p ?o}";

        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        Update update;
        try {
            update = connection.prepareUpdate(deleteUpdate);
        } catch (MalformedQueryException e) {
            e.printStackTrace();
            return;
        }

        update.execute();
    }
}