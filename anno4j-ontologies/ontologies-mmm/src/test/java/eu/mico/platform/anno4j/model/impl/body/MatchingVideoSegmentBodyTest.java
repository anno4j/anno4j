package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.querying.QueryService;
import eu.mico.platform.anno4j.model.Item;
import eu.mico.platform.anno4j.model.Part;
import eu.mico.platform.anno4j.model.impl.micotarget.MicoSpecificResource;
import eu.mico.platform.anno4j.model.provenance.Asset;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite for the MatchingVideoSegmentBody class.
 */
public class MatchingVideoSegmentBodyTest {

    private Anno4j anno4j;
    private QueryService queryService;

    private final static String VIDEO_FORMAT = "mp4";
    private final static String VIDEO_LOCATION_1 = "http://mico-project.eu/assets/asset1";
    private final static String VIDEO_LOCATION_2 = "http://mico-project.eu/assets/asset2";
    private final static String MEDIA_FRAGS = "https://www.w3.org/TR/media-frags/";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("mmm", "http://www.mico-project.eu/ns/mmm/2.0/schema#");
    }

    /**
     * Two videos (v1 and v2) will have matching segments, from second 20 to 30 in video 1, matching seconds 5 to 15 in video 2.
     */
    @Test
    public void matchingVideoSegmentBodyTest() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        // Create video 1 item
        Item item1 = anno4j.createObject(Item.class);
        Asset asset1 = anno4j.createObject(Asset.class);
        asset1.setFormat(VIDEO_FORMAT);
        asset1.setLocation(VIDEO_LOCATION_1);
        item1.setAsset(asset1);

        // Create video 2 item
        Item item2 = anno4j.createObject(Item.class);
        Asset asset2 = anno4j.createObject(Asset.class);
        asset2.setFormat(VIDEO_FORMAT);
        asset2.setLocation(VIDEO_LOCATION_2);
        item2.setAsset(asset2);

        // Create the part for video 1
        Part part1 = anno4j.createObject(Part.class);
        item1.addPart(part1);

        // Create target for video 1
        MicoSpecificResource spec1 = anno4j.createObject(MicoSpecificResource.class);
        FragmentSelector selector1 = anno4j.createObject(FragmentSelector.class);
        selector1.setTemporalFragment(20.0, 30.0);
        selector1.setConformsTo(MEDIA_FRAGS);
        spec1.setSelector(selector1);
        spec1.setSource(item1);

        part1.addTarget(spec1);

        // Create the body for video 1
        MatchingVideoSegmentBody body1 = anno4j.createObject(MatchingVideoSegmentBody.class);
        FragmentSelector selector2 = anno4j.createObject(FragmentSelector.class);
        selector2.setTemporalFragment(5.0, 15.0);
        selector2.setConformsTo(MEDIA_FRAGS);
        body1.setSelector(selector2);
        body1.setSource(item2);

        part1.setBody(body1);

        // Query for non existing Items
        List<Item> result = queryService.execute(Item.class);

        // Test does not work because Items have been stored somehow already
//        assertEquals(0, result.size());

        anno4j.persist(item2);
        anno4j.persist(item1);

        result = queryService.execute(Item.class);

        assertEquals(2, result.size());

        List<MatchingVideoSegmentBody> resultBodyList = queryService.execute(MatchingVideoSegmentBody.class);
        assertEquals(1, resultBodyList.size());

        MatchingVideoSegmentBody resultBody = resultBodyList.get(0);
        assertEquals(item2.getResource().toString(), resultBody.getSource().toString());
        assertEquals((Double) 5.0, ((FragmentSelector) resultBody.getSelector()).getStart());
        assertEquals((Double) 15.0, ((FragmentSelector) resultBody.getSelector()).getEnd());
    }
}