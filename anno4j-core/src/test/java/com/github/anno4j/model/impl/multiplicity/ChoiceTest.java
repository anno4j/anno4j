package com.github.anno4j.model.impl.multiplicity;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.MotivationFactory;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.body.TextualBody;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

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

    /**
     * Test was added to cover the issue of a request sent by email.
     * Issue concerned the not printing of the items contained in a Choice object.
     *
     * Test is kept until the functionality is tested more thoroughly.
     */
    @Test
    public void testPrintingChoiceItems() throws RepositoryException, IllegalAccessException, InstantiationException {

        Annotation annotation = this.anno4j.createObject(Annotation.class);

        Choice choice = anno4j.createObject(Choice.class);

        TextualBody txtBody1 = anno4j.createObject(TextualBody.class);

        Motivation taggingMotivation = MotivationFactory.getTagging(anno4j);

        txtBody1.addPurpose(taggingMotivation);
        txtBody1.setValue("love");

        TextualBody txtBody12 = anno4j.createObject(TextualBody.class);

        Motivation taggingMotivation2 = MotivationFactory.getTagging(anno4j);

        txtBody12.addPurpose(taggingMotivation2);
        txtBody12.setValue("love2222");
        choice.addItem(txtBody1);
        choice.addItem(txtBody12);

        TextualBody txtBody13 = anno4j.createObject(TextualBody.class);

        Motivation taggingMotivation3 = MotivationFactory.getTagging(anno4j);

        txtBody13.addPurpose(taggingMotivation3);
        txtBody13.setValue("love333333");
        annotation.addBody(txtBody13);
        annotation.addBody(choice);

        System.out.println(annotation.getTriples(RDFFormat.RDFXML));
    }
}