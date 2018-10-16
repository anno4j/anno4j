package com.github.anno4j.model.impl.selector;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFFormat;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Manu on 01.02.18.
 */
public class FragmentSelectorTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testFragmentSelector() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException, RepositoryConfigException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);
        annotation.addTarget(specificResource);

        FragmentSelector selector = this.anno4j.createObject(FragmentSelector.class);
        specificResource.setSelector(selector);

        String output = annotation.getTriples(RDFFormat.JSONLD);

        System.out.println(output);

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", annotation.getResourceAsString());

        Annotation queriedAnnotation = qs.execute(Annotation.class).get(0);

        assertEquals(FragmentSpecification.W3C_MEDIA_FRAGMENTS.toString(),
                ((FragmentSelector) ((SpecificResource) queriedAnnotation.getTargets().toArray()[0]).getSelector()).getConformsTo().getResourceAsString());
    }
}
