package com.github.anno4j.model.impl.multiplicity;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Basic test for the Choice class.
 */
public class ChoiceTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void basicChoiceTest() throws RepositoryException, IllegalAccessException, InstantiationException {
        Choice choice = this.anno4j.createObject(Choice.class);

        Annotation anno1 = this.anno4j.createObject(Annotation.class);
        Annotation anno2 = this.anno4j.createObject(Annotation.class);

        choice.addItem(anno1);
        choice.addItem(anno2);

        List<Choice> result = this.anno4j.findAll(Choice.class);

        assertEquals(1, result.size());
    }
}