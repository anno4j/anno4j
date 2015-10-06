package com.github.anno4j.idGenerator;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
import org.junit.Test;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by schlegel on 06/10/15.
 */
public class IDGeneratorTest {

    @Test
    public void testCustomIDGenerator() throws Exception {
        Anno4j anno4j = new Anno4j(new CustomIDGenerator());
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();
        Person personObject = anno4j.createObject(Person.class);

        assertTrue(personObject.getResourceAsString().startsWith("urn:CUSTOMURL:"));
        connection.addObject(personObject);

        List<Person> resultList = connection.getObjects(Person.class).asList();
        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getResourceAsString().startsWith("urn:CUSTOMURL:"));
    }

    @Test
    public void testCustomIDGeneratorWithType() throws Exception {
        Anno4j anno4j = new Anno4j(new CustomIDGenerator());
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Software softwareObject = anno4j.createObject(Software.class);

        assertTrue(softwareObject.getResourceAsString().startsWith("urn:SOFTWARE:"));
        connection.addObject(softwareObject);

        List<Software> resultList = connection.getObjects(Software.class).asList();
        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getResourceAsString().startsWith("urn:SOFTWARE:"));
    }

    public static class CustomIDGenerator implements IDGenerator {

        @Override
        public Resource generateID(Set<URI> types) {
            if(types.contains(new MemValueFactory().createURI("http://www.w3.org/ns/prov/SoftwareAgent"))) {
                return new MemValueFactory().createURI("urn:SOFTWARE:" + UUID.randomUUID());
            } else {
                return new MemValueFactory().createURI("urn:CUSTOMURL:" + UUID.randomUUID());
            }
        }
    }
}
