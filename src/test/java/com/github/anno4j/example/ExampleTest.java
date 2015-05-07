package com.github.anno4j.example;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.StringURLResource;
import com.github.anno4j.model.impl.agent.AgentPerson;
import com.github.anno4j.model.impl.agent.AgentSoftware;
import com.github.anno4j.model.impl.annotation.AnnotationDefault;
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
import org.openrdf.sail.memory.MemoryStore;

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
    public void exampleTest() {
        // Create the base annotation
        Annotation annotation = new AnnotationDefault();
        annotation.setMotivatedBy(new Commenting());
        annotation.setAnnotatedAt("2014-09-28T12:00:00Z");
        annotation.setSerializedAt("2013-02-04T12:00:00Z");

        // Create the person agent for the annotation
        AgentPerson agentPerson = new AgentPerson();
        agentPerson.setName("A. Person");

        annotation.setAnnotatedBy(agentPerson);

        // Create the software agent for the annotation
        AgentSoftware agentSoftware = new AgentSoftware();
        agentSoftware.setName("Code v2.1");
        agentSoftware.setHomepage("http://example.org/agent2/homepage1");

        annotation.setSerializedBy(agentSoftware);

        // Create the body
        TextAnnotationBody body = new TextAnnotationBody("text/plain", "One of my favourite cities", "en");
        annotation.setBody(body);

        // Create the target and selector
        SpecificResource specificResource = new SpecificResource();

        TextPositionSelector textPositionSelector = new TextPositionSelector(4096, 4104);

        specificResource.setSelector(textPositionSelector);
        StringURLResource source = new StringURLResource("http://example.org/source1");
        annotation.setTarget(specificResource);
    }
}
