package com.github.anno4j.model.impl.multiplicity;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
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

    /**
     * Tests, if the body and target functionality of Choice works
     */
    @Test
    public void choiceBodyTargetTest() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        Choice choiceBody = this.anno4j.createObject(Choice.class);
        choiceBody.addItem(this.anno4j.createObject(ResourceObject.class));

        Choice choiceTarget = this.anno4j.createObject(Choice.class);
        choiceTarget.addItem(this.anno4j.createObject(ResourceObject.class));
        choiceTarget.addItem(this.anno4j.createObject(ResourceObject.class));

        annotation.addTarget(choiceTarget);
        annotation.addBody(choiceBody);

        List<Annotation> result = this.anno4j.findAll(Annotation.class);

        assertEquals(1, result.size());

        Annotation resultAnnotation = result.get(0);

        assertEquals(1, ((Choice) resultAnnotation.getBodies().iterator().next()).getItems().size());
        assertEquals(2, ((Choice) resultAnnotation.getTargets().toArray()[0]).getItems().size());
    }
}