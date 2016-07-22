package com.github.anno4j.model.impl.selector;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite to test the RangeSelector interface.
 */
public class RangeSelectorTest {

    private Anno4j anno4j;

    private final static String XPATH_VALUE = "xpath";
    private final static String CSS_VALUE = "css";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testRangeSelector() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);
        annotation.addTarget(specificResource);

        RangeSelector rangeSelector = this.anno4j.createObject(RangeSelector.class);
        specificResource.setSelector(rangeSelector);

        XPathSelector xPathSelector = this.anno4j.createObject(XPathSelector.class);
        xPathSelector.setValue(XPATH_VALUE);

        CSSSelector cssSelector = this.anno4j.createObject(CSSSelector.class);
        cssSelector.setValue(CSS_VALUE);

        rangeSelector.setStartSelector(xPathSelector);
        rangeSelector.setEndSelector(cssSelector);

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasTarget/oa:hasSelector/oa:hasStartSelector[is-a oa:XPathSelector]");
        qs.addCriteria("oa:hasTarget/oa:hasSelector/oa:hasStartSelector/rdf:value", XPATH_VALUE);

        qs.addCriteria("oa:hasTarget/oa:hasSelector/oa:hasEndSelector[is-a oa:CssSelector]");
        qs.addCriteria("oa:hasTarget/oa:hasSelector/oa:hasEndSelector/rdf:value", CSS_VALUE);

        List<Annotation> result = qs.execute(Annotation.class);
        assertEquals(1, result.size());
    }
}