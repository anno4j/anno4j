package com.github.anno4j.model;

import com.github.anno4j.model.impl.agent.AgentOrganization;
import com.github.anno4j.model.impl.agent.AgentPerson;
import com.github.anno4j.model.impl.agent.AgentSoftware;
import com.github.anno4j.model.impl.annotation.AnnotationDefault;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.*;

public class AgentTest {

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
    public void testAgentPerson() throws Exception {
        // Create test annotation
        Annotation annotation = new AnnotationDefault();

        // Create and add the agent
        AgentPerson agentPerson = new AgentPerson();
        agentPerson.setName("TestName");
        agentPerson.setMbox("TextMbox");
        agentPerson.setOpenID("TestID");

        annotation.setAnnotatedBy(agentPerson);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<AgentPerson> result = connection.getObjects(AgentPerson.class).asList();

        AgentPerson resultObject = result.get(0);
        assertEquals(agentPerson.getName(), resultObject.getName());
        assertEquals(agentPerson.getMbox(), resultObject.getMbox());
        assertEquals(agentPerson.getOpenID(), resultObject.getOpenID());
    }

    @Test
    public void testAgentOrganization() throws Exception {
        // Create test annotation
        Annotation annotation = new AnnotationDefault();

        // Create and add the agent
        AgentOrganization agentOrganization = new AgentOrganization();
        agentOrganization.setName("TestName");

        annotation.setAnnotatedBy(agentOrganization);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<AgentOrganization> result = connection.getObjects(AgentOrganization.class).asList();

        AgentOrganization resultObject = result.get(0);
        assertEquals(agentOrganization.getName(), resultObject.getName());
    }

    @Test
    public void testAgentSoftware() throws Exception {
        // Create test annotation
        Annotation annotation = new AnnotationDefault();

        // Create and add the agent
        AgentSoftware agentSoftware = new AgentSoftware();
        agentSoftware.setName("TestName");
        agentSoftware.setHomepage("TestPage");

        annotation.setAnnotatedBy(agentSoftware);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<AgentSoftware> result = connection.getObjects(AgentSoftware.class).asList();

        AgentSoftware resultObject = result.get(0);
        assertEquals(agentSoftware.getName(), resultObject.getName());
        assertEquals(agentSoftware.getHomepage(), resultObject.getHomepage());
    }
}