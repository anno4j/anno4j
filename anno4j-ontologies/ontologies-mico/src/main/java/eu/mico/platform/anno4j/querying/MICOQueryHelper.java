package eu.mico.platform.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.namespaces.DCTERMS;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

/**
 * The MICOQueryHelper provides shortcut functions to query specific items more easily,
 * e.g. all annotations related to a specific image.
 *
 * @author Andreas Eisenkolb
 */
public class MICOQueryHelper {

    /**
     * Singleton instance of this class.
     */
    private static MICOQueryHelper instance = null;

    /**
     * Allows to query all annotation objects of a given content item.
     *
     * @param contentItemId The id (url) of the content item.
     * @return List of annotations related to the given content item.
     */
    public List<Annotation> getAnnotationsOfContentItem(String contentItemId) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return Anno4j.getInstance().createQueryService(Annotation.class)
                .addPrefix(MICO.PREFIX, MICO.NS)
                .setAnnotationCriteria("^mico:hasContent/^mico:hasContentPart", contentItemId)
                .execute();
    }

    /**
     * Allows to query for the annotation object of a given content part.
     *
     * @param contentPartId The id (url) of the content part.
     * @return The annotation object of the given content part.
     */
    public Annotation getAnnotationOfContentPart(String contentPartId) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return (Annotation) Anno4j.getInstance().createQueryService(Annotation.class)
                .addPrefix(MICO.PREFIX, MICO.NS)
                .setAnnotationCriteria("^mico:hasContent", contentPartId)
                .execute().get(0);
    }

    /**
     * Allows to query for all annotations realated to a specific MIME type,
     * e.g. "images/jpeg" or "video/mp4"
     *
     * @param mimeType The specific MIME type
     * @return List of annotations related to the specific MIME type.
     */
    public List<Annotation> getAnnotationsByMIMEType(String mimeType) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return Anno4j.getInstance().createQueryService(Annotation.class)
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addPrefix(DCTERMS.PREFIX, DCTERMS.NS)
                .setAnnotationCriteria("^mico:hasContent/^mico:hasContentPart/mico:hasContentPart/dct:type", mimeType)
                .execute();
    }

    public List<Annotation> getAnnotationsBySourceName(String sourceName) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return Anno4j.getInstance().createQueryService(Annotation.class)
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addPrefix(DCTERMS.PREFIX, DCTERMS.NS)
                .setAnnotationCriteria("^mico:hasContent/^mico:hasContentPart/mico:hasContentPart/dct:source", sourceName)
                .execute();
    }

    /**
     * Getter for the Anno4j getter instance.
     *
     * @return singleton Anno4j instance.
     */
    public static MICOQueryHelper getInstance() {
        if (instance == null) {
            synchronized (MICOQueryHelper.class) {
                if (instance == null) {
                    instance = new MICOQueryHelper();
                }
            }
        }
        return instance;
    }
}
