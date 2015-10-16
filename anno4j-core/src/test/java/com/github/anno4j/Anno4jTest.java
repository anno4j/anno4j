package com.github.anno4j;

import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 16/10/15.
 */
public class Anno4jTest {

    @Before
    public void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
    }

    @Test
    public void testGetAnnotation() throws Exception {
        Annotation annotation = new Annotation();
        String url = annotation.getResource().toString();
        String dateString = new Date().toString();
        annotation.setSerializedAt(dateString);

        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotationNew = Anno4j.getInstance().getAnnotation(url);

        assertEquals(url, annotationNew.getResource().toString());
        assertEquals(dateString, annotationNew.getSerializedAt());
    }
}