package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.io.impl.ObjectParser;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Manu on 07/10/15.
 */
public class TextAreaBodyTest {

    @Test
    public void testJSONLDWithText() {

        try {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(JSONLD_TEXT, url, RDFFormat.JSONLD);

            Annotation annotation = annotations.get(0);
            assertEquals(1, annotations.size());

            TextAreaBody body = (TextAreaBody) annotation.getBody();

            assertEquals("en", body.getLanguage());
            assertEquals("text/plain", body.getFormat());
            assertEquals("This should be the content", body.getValue());

            objectParser.shutdown();
        } catch (RepositoryException | MalformedURLException | RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    private final static String JSONLD_TEXT = "{\n"
            + " \"@context\":\"https://raw.githubusercontent.com/w3c/web-annotation/gh-pages/jsonld/anno.jsonld\",\n"
            + " \"@type\":\"oa:Annotation\" , \n"
            + " \"body\": { \n"
            + " \"@type\":\"dctypes:Text\", \n"
            + " \"value\":\"This should be the content\", \n"
            + " \"format\":\"text/plain\", \n"
            + " \"language\":\"en\" \n"
            + " }, \n"
            + " \"target\": { \n"
            + " \"@id\": \"http://dbpedia.org/page/Yellowfin_tuna\" \n"
            + " }\n"
            + "}";
}