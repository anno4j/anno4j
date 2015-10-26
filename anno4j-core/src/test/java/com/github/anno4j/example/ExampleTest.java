package com.github.anno4j.example;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
import com.github.anno4j.model.impl.motivation.Commenting;
import com.github.anno4j.model.impl.selector.TextPositionSelector;
import com.github.anno4j.model.impl.target.SpecificResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test case to implement the example annotation at {@link }http://www.w3.org/TR/2014/WD-annotation-model-20141211/#complete-example}.
 */
public class ExampleTest {

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
    public void exampleTest() throws Exception {
        // Create the base annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotatedAt("2014-09-28T12:00:00Z");
        annotation.setSerializedAt("2013-02-04T12:00:00Z");
        annotation.setMotivatedBy(new Commenting());

        // Create the person agent for the annotation
        Person person = new Person();
        person.setName("A. Person");
        person.setOpenID("http://example.org/agent1/openID1");

        annotation.setAnnotatedBy(person);

        // Create the software agent for the annotation
        Software software = new Software();
        software.setName("Code v2.1");
        software.setHomepage("http://example.org/agent2/homepage1");

        annotation.setSerializedBy(software);

        // Create the body
        TextAnnotationBody body = new TextAnnotationBody("text/plain", "One of my favourite cities", "en");
        annotation.setBody(body);

        // Create the selector
        SpecificResource specificResource = new SpecificResource();

        TextPositionSelector textPositionSelector = new TextPositionSelector(4096, 4104);

        specificResource.setSelector(textPositionSelector);

        // Create the actual target
        ResourceObject source = new ResourceObject();
        source.setResourceAsString("http://example.org/source1");
        specificResource.setSource(source);

        annotation.setTarget(specificResource);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Annotation> result = connection.getObjects(Annotation.class).asList();

        assertEquals(1, result.size());

        Annotation resultObject = result.get(0);

        assertEquals(annotation.getResource(), resultObject.getResource());
    }
}
