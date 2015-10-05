package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.agent.Organization;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.ObjectConnection;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the various agents (Organization, Person, Software).
 * <p/>
 * A simple annotation is built up for every agent, then persisted and queried.
 */
public class AgentTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testAgentPerson() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create and add the agent
        Person person = anno4j.createObject(Person.class);
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
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create and add the agent
        Organization organization = anno4j.createObject(Organization.class);
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
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create and add the agent
        Software software = anno4j.createObject(Software.class);
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