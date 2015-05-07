package com.github.anno4j.querying;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class QueryObjectTest {

    QueryObject queryObject;

    @Before
    public void setUp() throws Exception {
        queryObject = new QueryObject();
    }

    @Test
    public void dummyTest() throws Exception {

        queryObject.setAnnotationCriteria("rdf:value/oa:hasBody", "");

        queryObject.execute();

    }

}