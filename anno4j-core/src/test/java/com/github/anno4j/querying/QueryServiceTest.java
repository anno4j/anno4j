package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.SvgSelector;
import com.github.anno4j.model.impl.target.SpecificResource;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
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

        // Persisting the test data set
        Annotation annotation1 = new Annotation();

        FragmentSelector fragmentSelector = new FragmentSelector();
        fragmentSelector.setConformsTo("conformsTo");

        SpecificResource specificResource1 = new SpecificResource();
        specificResource1.setSelector(fragmentSelector);

        HashSet<Target> targets1= new HashSet<>();
        targets1.add(specificResource1);

        annotation1.setTargets(targets1);

        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);

        Annotation annotation2 = new Annotation();


        SvgSelector svgSelector = new SvgSelector();
        svgSelector.setConformsTo("conformsTo1");

        SpecificResource specificResource2 = new SpecificResource();
        specificResource2.setSelector(svgSelector);

        HashSet<Target> targets2 = new HashSet<>();
        targets2.add(specificResource2);

        annotation2.setTargets(targets2);

        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation2);
    }

    @Test
    public void testCustomGetSelectorFunction() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        // Testing the extended LDPath function: getSelector()
        List<Annotation> list = queryService.setAnnotationCriteria("fn:getSelector(.)[is-a oa:FragmentSelector]").execute();
        assertEquals(1, list.size());

        resetQueryService();

        List<Annotation> list1 = queryService.setAnnotationCriteria("fn:getSelector(.)").execute();
        assertEquals(2, list1.size());

    }

    @Test
    public void testCustomIsLiteralTestFunction() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list1 = queryService.setSelectorCriteria("[fn:isLiteral(dcterms:conformsTo)]").execute();
        assertEquals(2, list1.size());

        resetQueryService();

        List<Annotation> list2 = this.queryService.setBodyCriteria("[fn:isLiteral(ex:value)]").execute();
        assertEquals(0, list2.size());

        resetQueryService();
    }
}