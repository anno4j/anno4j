package com.github.anno4j.alibaba;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test inheritance of annotated classes. RDF of Superclass should be mapped to a Subclass. Subclass can add more functionality and access superclass information.
 */
public class InheritanceTest {
    Repository repository;
    ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();

        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);
        connection = objectRepository.getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void inheritanceTest() throws Exception {
        // Create the base annotation
        Annotation annotation = new Annotation();

        // Create the body
        Superclass body = new Superclass();
        body.setValue("myValue");
        annotation.setBody(body);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Annotation> result = connection.getObjects(Annotation.class).asList();

        assertEquals(1, result.size());

        Annotation resultObject = result.get(0);
        Body resultBody = resultObject.getBody();

        assertTrue(resultBody instanceof  Superclass);
        assertEquals("myValue", ((Superclass) resultBody).getValue());
        assertTrue(resultBody instanceof Subclass);
        assertEquals("myValueCUSTOM", ((Subclass) resultBody).getCustomValue());
    }

    @Iri("http://www.example.com/Superclass")
    public static class Superclass extends Body {

        @Iri("http://www.example.com/Superclass/value")
        private String value;

        public Superclass() {
        }


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Iri("http://www.example.com/Superclass")
    public static class Subclass extends Superclass{

        public Subclass() {
        }

        public String getCustomValue(){
            return this.getValue() + "CUSTOM";
        }
    }
}
