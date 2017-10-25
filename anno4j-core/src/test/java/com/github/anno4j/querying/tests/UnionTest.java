package com.github.anno4j.querying.tests;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QuerySetup;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 09/10/15.
 */
public class UnionTest extends QuerySetup {

    @Test
    public void testDoubleUnionBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody/(ex:subBody | ex:subBody2)[is-a ex:unionBody | is-a ex:unionBody2]")
                .execute();

        assertEquals(2, annotations.size());
    }

    @Test
    public void testNoUnionBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody[is-a ex:unionBody]")
                .execute();

        assertEquals(2, annotations.size());
    }

    @Test
    public void testUnionBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, InstantiationException, IllegalAccessException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody[is-a ex:unionBody] | oa:hasBody[is-a ex:unionBodyAlternative]")
                .execute();
        
        assertEquals(3, annotations.size());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation1 =  anno4j.createObject(Annotation.class);
        annotation1.setGenerated("2015-01-28T12:00:00Z");
        UnionTestBody unionTestBody1 = anno4j.createObject(UnionTestBody.class);
        unionTestBody1.setSubBody(anno4j.createObject(UnionTestBodyAlternative.class));
        unionTestBody1.setSubBody2(anno4j.createObject(UnionTestBody.class));
        annotation1.addBody(unionTestBody1);

        Annotation annotation2 =  anno4j.createObject(Annotation.class);
        annotation2.setGenerated("2015-01-28T12:00:00Z");
        UnionTestBody unionTestBody2 = anno4j.createObject(UnionTestBody.class);
        unionTestBody2.setSubBody(anno4j.createObject(UnionTestBodyAlternative.class));
        unionTestBody2.setSubBody2(anno4j.createObject(UnionTestBodyAlternative.class));
        annotation2.addBody(unionTestBody2);

        Annotation annotation3 =  anno4j.createObject(Annotation.class);
        annotation3.setGenerated("2015-01-28T12:00:00Z");
        UnionTestBodyAlternative unionTestBodyAlternative = anno4j.createObject(UnionTestBodyAlternative.class);
        unionTestBodyAlternative.setSubBody(anno4j.createObject(UnionTestBody2.class));
        unionTestBodyAlternative.setSubBody2(anno4j.createObject(UnionTestBodyAlternative.class));
        annotation3.addBody(unionTestBodyAlternative);
    }

    @Iri("http://www.example.com/schema#unionBody")
    public static interface UnionTestBody extends Body {

        @Iri("http://www.example.com/schema#subBody")
        Body getSubBody();

        @Iri("http://www.example.com/schema#subBody")
        void setSubBody(Body body);

        @Iri("http://www.example.com/schema#subBody2")
        Body getSubBody2();

        @Iri("http://www.example.com/schema#subBody2")
        void setSubBody2(Body body);
    }

    @Iri("http://www.example.com/schema#unionBody2")
    public static interface UnionTestBody2 extends Body {

        @Iri("http://www.example.com/schema#subBody")
        Body getSubBody();

        @Iri("http://www.example.com/schema#subBody")
        void setSubBody(Body body);

        @Iri("http://www.example.com/schema#subBody2")
        Body getSubBody2();

        @Iri("http://www.example.com/schema#subBody2")
        void setSubBody2(Body body);
    }

    @Iri("http://www.example.com/schema#unionBodyAlternative")
    public static interface UnionTestBodyAlternative extends Body {

        @Iri("http://www.example.com/schema#subBody")
        Body getSubBody();

        @Iri("http://www.example.com/schema#subBody")
        void setSubBody(Body body);

        @Iri("http://www.example.com/schema#subBody2")
        Body getSubBody2();

        @Iri("http://www.example.com/schema#subBody2")
        void setSubBody2(Body body);
    }
}
