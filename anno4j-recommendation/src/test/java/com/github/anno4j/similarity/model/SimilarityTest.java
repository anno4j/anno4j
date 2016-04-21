package com.github.anno4j.similarity.model;

import com.github.anno4j.querying.QueryService;
import com.github.anno4j.similarity.SimilarityTestSetup;
import com.github.anno4j.similarity.impl.TestBody1;
import com.github.anno4j.similarity.impl.TestBody2;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Manu on 20/04/16.
 */
public class SimilarityTest extends SimilarityTestSetup {

    @Test
    public void testSimilarityWithBodies() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Similarity sim = this.anno4j.createObject(Similarity.class);

        ObjectFactory fac = this.anno4j.getObjectRepository().getObjectService().createObjectFactory();

        URI uri = fac.getNameOf(TestBody1.class);
        URI uri2 = fac.getNameOf(TestBody2.class);

        sim.addBodyURI(uri);
        sim.addBodyURIAsString(uri2.toString());

        this.anno4j.persist(sim);

        QueryService qs = this.anno4j.createQueryService();

        List<Similarity> result = qs.execute(Similarity.class);

        assertEquals(1, result.size());

        Similarity resultObj = result.get(0);

        Iterator<URI> it = resultObj.getBodies().iterator();

        assertEquals("http://somepage.com#TestBody1", it.next().toString());
        assertEquals("http://somepage.com#TestBody2", it.next().toString());
    }

    @Override
    protected void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {

    }
}