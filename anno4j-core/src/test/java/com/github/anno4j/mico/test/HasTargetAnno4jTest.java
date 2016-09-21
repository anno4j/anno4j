package com.github.anno4j.mico.test;

import com.github.anno4j.Anno4j;
import com.github.anno4j.mico.model.ItemMMM;
import com.github.anno4j.mico.model.PartMMM;
import com.github.anno4j.mico.model.SpecificResourceMMM;
import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Test case for the runtime error, persumably created by the multiple inheritance of the PartMMM.
 */
public class HasTargetAnno4jTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testHasTargetWithAnno4j() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        URI context = new URIImpl("http://example.org");

        ItemMMM item = this.anno4j.createObject(ItemMMM.class, context);

        PartMMM part = this.anno4j.createObject(PartMMM.class, context);
        item.addPart(part);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class, context);
        SpecificResourceMMM specificResourceMMM = this.anno4j.createObject(SpecificResourceMMM.class, context);

        part.addTarget(specificResource);
        part.addTarget(specificResourceMMM);

        QueryService qs = this.anno4j.createQueryService(context);
        QueryService qs2 = this.anno4j.createQueryService(context);

        List<PartMMM> result = qs.addPrefix(MMM.PREFIX, MMM.NS).addCriteria("mmm:hasTarget").execute(PartMMM.class);
        List<PartMMM> result2 = qs2.addPrefix(MMM.PREFIX, MMM.NS). addCriteria("oa:hasTarget").execute(PartMMM.class);

        // Should find a single part with 2 targets
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getTarget().size());

        // Shouldn't find annotation based hasTarget properties
        assertEquals(0, result2.size());
    }
}
