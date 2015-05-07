package com.github.anno4j.querying;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class QueryServiceTest {

    QueryService queryService;

    @Before
    public void setUp() throws Exception {
        queryService = new QueryService();
    }

    @Test
    public void dummyTest() throws Exception {

        queryService.setAnnotationCriteria("rdf:value/oa:hasBody", "");

        queryService.execute();

    }

}