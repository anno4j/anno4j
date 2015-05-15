package com.github.anno4j.model;

import com.github.anno4j.model.impl.agent.Organization;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
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

import static org.junit.Assert.assertEquals;

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
        Annotation annotation = new Annotation();

        // Create and add the agent
        Person person = new Person();
        person.setName("TestName");
        person.setMbox("TextMbox");
        person.setOpenID("TestID");

        annotation.setAnnotatedBy(person);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Person> result = connection.getObjects(Person.class).asList();

        Person resultObject = result.get(0);
        assertEquals(person.getName(), resultObject.getName());
        assertEquals(person.getMbox(), resultObject.getMbox());
        assertEquals(person.getOpenID(), resultObject.getOpenID());
    }

    @Test
    public void testAgentOrganization() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();

        // Create and add the agent
        Organization organization = new Organization();
        organization.setName("TestName");

        annotation.setAnnotatedBy(organization);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Organization> result = connection.getObjects(Organization.class).asList();

        Organization resultObject = result.get(0);
        assertEquals(organization.getName(), resultObject.getName());
    }

    @Test
    public void testAgentSoftware() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();

        // Create and add the agent
        Software software = new Software();
        software.setName("TestName");
        software.setHomepage("TestPage");

        annotation.setAnnotatedBy(software);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Software> result = connection.getObjects(Software.class).asList();

        Software resultObject = result.get(0);
        assertEquals(software.getName(), resultObject.getName());
        assertEquals(software.getHomepage(), resultObject.getHomepage());
    }
}