package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.extensions.Extension1;
import com.github.anno4j.querying.extensions.Extension2;
import org.junit.Test;

import java.util.List;

/**
 * Test class for the QueryService.
 */
public class QueryServiceTest {
    @Test
    public void testUserExtension() throws Exception {
        Anno4j.getInstance().createQueryService().useExtension(Extension2.class).helloWorld().addFilterCriteria().execute();
        Anno4j.getInstance().createQueryService().useExtension(Extension1.class).doSomething().addFilterCriteria().execute();
    }
}