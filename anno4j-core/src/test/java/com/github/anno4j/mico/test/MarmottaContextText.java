package com.github.anno4j.mico.test;

import com.github.anno4j.Anno4j;
import com.github.anno4j.mico.model.ItemMMM;
import com.github.anno4j.mico.model.PartMMM;
import com.github.anno4j.querying.QueryService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderTransformationSubstitution.log;
import static org.junit.Assert.assertEquals;

/**
 * Test case for a querying error, persumably created by missing post param support of marmotta. Only occures with marmotta as backend. Marmotta seems to ignore the default-graph parameter of the post request.
 */
public class MarmottaContextText {

    private static Anno4j anno4j;
    private static boolean isMarmottaAvailable = false;

    @BeforeClass
    public static void setUp() throws Exception {
        String marmottaBaseUrl = "http://localhost:8080/marmotta";
        SPARQLRepository sparqlRepository = new SPARQLRepository(marmottaBaseUrl + "/sparql/select", marmottaBaseUrl + "/sparql/update");
        anno4j = new Anno4j(sparqlRepository);

        //retrieve the status of the registration service
        HttpGet httpGetInfo = new HttpGet(marmottaBaseUrl);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGetInfo);
            int status = response.getStatusLine().getStatusCode();
            log.info("looking for marmotta service at {}",httpGetInfo.toString());
            if (status == 200) {
                isMarmottaAvailable = true;
            }
        } catch (Exception ignore) {
        } finally {
            if (response != null){
                readAndIgnoreResponseBody(response);
                response.close();
            }
        }
    }

    @Test
    public void testContextQuery() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {

        Assume.assumeTrue(isMarmottaAvailable);

        QueryService preqs = anno4j.createQueryService();
        List<PartMMM> preresult = preqs.execute(PartMMM.class);

        ItemMMM item = anno4j.createObject(ItemMMM.class);
        PartMMM part = anno4j.createObject(PartMMM.class, (URI) item.getResource());
        item.addPart(part);

        ItemMMM item2 = anno4j.createObject(ItemMMM.class);
        PartMMM part2 = anno4j.createObject(PartMMM.class, (URI) item2.getResource());
        item2.addPart(part2);

        QueryService qs = anno4j.createQueryService((URI) item.getResource());
        List<PartMMM> result = qs.execute(PartMMM.class);
        assertEquals(1, result.size());

        QueryService qs2 = anno4j.createQueryService();
        List<PartMMM> result2 = qs2.execute(PartMMM.class);
        assertEquals(preresult.size()+2, result2.size());

        QueryService qs3 = anno4j.createQueryService((URI) item2.getResource());
        List<PartMMM> result3 = qs3.execute(PartMMM.class);
        assertEquals(1, result3.size());
    }

    /**
     * workaround, to avoid broken-pipe on server when not reading body
     *
     * @param response
     * @throws IOException
     */
    private static void readAndIgnoreResponseBody(CloseableHttpResponse response)
            throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null && entity.isStreaming()) {
            // read content to avoid broken pipe on server
            EntityUtils.toString(entity, StandardCharsets.UTF_8);
        }
    }
}
