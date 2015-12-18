package eu.mico.platform.anno4j.model;

import com.github.anno4j.Anno4j;
import eu.mico.platform.anno4j.model.provenance.Asset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.result.Result;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Suite to test the Item class.
 */
public class ItemTest {

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
    public void testItem() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException {
        Item item = anno4j.createObject(Item.class);

        Asset asset = anno4j.createObject(Asset.class);
        String format = "someFormat";
        String location = "someLocation";
        asset.setFormat(format);
        asset.setLocation(location);

        Part part1 = anno4j.createObject(Part.class);
        Part part2 = anno4j.createObject(Part.class);
        Part part3 = anno4j.createObject(Part.class);

        item.setAsset(asset);

        // Query for non existing Items
        Result<Item> result = connection.getObjects(Item.class);

        assertEquals(0, result.asList().size());

        // Persist the Item
        connection.addObject(item);

        // Query for now one existing Item
        result = connection.getObjects(Item.class);

        List<Item> resultList = result.asList();
        assertEquals(1, resultList.size());

        // The item does not have any parts yet
        assertEquals(0, resultList.get(0).getParts().size());

        // Add two parts
        HashSet<Part> parts = new HashSet<Part>();
        parts.add(part1);
        parts.add(part2);
        item.setParts(parts);

        assertEquals(2, resultList.get(0).getParts().size());

        // Now add one additional part by the addPart method
        item.addPart(part3);

        assertEquals(3, resultList.get(0).getParts().size());
    }
}
