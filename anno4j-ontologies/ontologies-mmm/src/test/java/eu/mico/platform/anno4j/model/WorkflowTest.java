package eu.mico.platform.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.targets.SpecificResource;
import eu.mico.platform.anno4j.model.provenance.Asset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
import org.openrdf.rio.RDFFormat;

import static org.junit.Assert.assertEquals;

/**
 * Test Suite builds up multiple Items and Parts that are interconnected.
 */
public class WorkflowTest {

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
    /**
     * Bigger Item and Part Test, building the example of the MMM spec of Figure 9.
     *
     * Shallow test.
     */
    public void workflowTest() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException {
        // Create the Item
        Item item = anno4j.createObject(Item.class);

        Asset asset = anno4j.createObject(Asset.class);
        String format = "someItemFormat";
        String location = "someItemLocation";
        asset.setFormat(format);
        asset.setLocation(location);

        item.setAsset(asset);
        item.setSerializedAt(2015, 18, 12, 10, 52, 00);

        // Create Part 1
        Part part1 = anno4j.createObject(Part.class);
        part1.addInput(item);

        PartTest.TestBody body1 = anno4j.createObject(PartTest.TestBody.class);
        body1.setValue("someBodyValue1");
        part1.setBody(body1);

        SpecificResource spec1 = anno4j.createObject(SpecificResource.class);
        spec1.setSource(item);
        part1.addTarget(spec1);

        item.addPart(part1);

        // Create Part 2
        Part part2 = anno4j.createObject(Part.class);
        part2.addInput(part1);

        PartTest.TestBody body2 = anno4j.createObject(PartTest.TestBody.class);
        body2.setValue("someBodyValue2");
        part2.setBody(body2);

        SpecificResource spec2 = anno4j.createObject(SpecificResource.class);
        spec2.setSource(item);
        part2.addTarget(spec2);

        Asset asset2 = anno4j.createObject(Asset.class);
        asset2.setFormat("someFormat2");
        asset2.setLocation("someLocation2");

        item.addPart(part2);

        // Create Part 3
        Part part3 = anno4j.createObject(Part.class);
        part3.addInput(part2);

        PartTest.TestBody body3 = anno4j.createObject(PartTest.TestBody.class);
        body3.setValue("someBodyValue3");
        part3.setBody(body3);

        SpecificResource spec3 = anno4j.createObject(SpecificResource.class);
        spec3.setSource(part2);
        part3.addTarget(spec3);

        item.addPart(part3);

        // Persist
        connection.addObject(item);

        Result<Item> result = connection.getObjects(Item.class);

        Item resultItem = result.asList().get(0);

        // Test
        assertEquals(3, resultItem.getParts().size());

//        System.out.println(resultItem.getTriples(RDFFormat.TURTLE));

        for(Part part: resultItem.getParts()) {
//            System.out.println(part.getTriples(RDFFormat.TURTLE));

            String partResource = part.getResourceAsString();
            if(partResource.equals(part1.getResourceAsString())) {
                assertEquals(item.getResource(), part.getInputs().iterator().next().getResource());

            } else if(partResource.equals(part2.getResourceAsString())) {
                assertEquals(part1.getResource(), part.getInputs().iterator().next().getResource());

            } else {
                assertEquals(part2.getResource(), part.getInputs().iterator().next().getResource());
            }
        }
    }
}
