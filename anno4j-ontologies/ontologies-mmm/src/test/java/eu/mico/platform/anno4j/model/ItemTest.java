package eu.mico.platform.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import eu.mico.platform.anno4j.model.provenance.Asset;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Suite to test the Item class.
 */
public class ItemTest {

    private Anno4j anno4j;
    private QueryService queryService;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("mmm", "http://www.mico-project.eu/ns/mmm/2.0/schema#");
    }

    @Test
    public void testItem() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException, ParseException {
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
        queryService.addCriteria("mmm:hasAsset[is-a mmm:Asset]");

        // Query for non existing Items
        List<Item> result = queryService.execute(MMM.ITEM);

        assertEquals(0, result.size());

        // Persist the Item
        anno4j.persist(item);

        // Query for now one existing Item
        result = queryService.execute(MMM.ITEM);

        assertEquals(1, result.size());

        // The item does not have any parts yet
        assertEquals(0, result.get(0).getParts().size());

        // Add two parts
        HashSet<Part> parts = new HashSet<Part>();
        parts.add(part1);
        parts.add(part2);
        item.setParts(parts);

        assertEquals(2, result.get(0).getParts().size());

        // Now add one additional part by the addPart method
        item.addPart(part3);

        assertEquals(3, result.get(0).getParts().size());
    }
}
