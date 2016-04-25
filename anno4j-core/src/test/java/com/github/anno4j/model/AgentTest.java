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

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
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

        // Query persisted object
        List<Person> result = anno4j.findAll(Person.class);

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

        // Query persisted object
        List<Organization> result = anno4j.findAll(Organization.class);

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

        // Query persisted object
        List<Software> result = anno4j.findAll(Software.class);

        Software resultObject = result.get(0);
        assertEquals(software.getName(), resultObject.getName());
        assertEquals(software.getHomepage(), resultObject.getHomepage());
    }
}