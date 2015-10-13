package eu.mico.platform.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.querying.QueryService;
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
     * Selector type restriction
     */
    private String selectorTypeRestriction;

    /**
     * Body type restriction
     */
    private String bodyTypeRestriction;

    /**
     * Target type restriction
     */
    private String targetTypeRestriction;

    /**
     * A configured instance of anno4j
     */
    private Anno4j anno4j;

    public MICOQueryHelper(Anno4j anno4j) {
        this.anno4j = anno4j;
    }


    /**
     * Allows to query all annotation objects of a given content item.
     *
     * @param contentItemId The id (url) of the content item.
     * @return List of annotations related to the given content item.
     *
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ParseException
     */
    public List<Annotation> getAnnotationsOfContentItem(String contentItemId) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        QueryService qs = anno4j.createQueryService()
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addCriteria("^mico:hasContent/^mico:hasContentPart", contentItemId);

        processTypeRestriction(qs);

        return qs.execute();
    }

    /**
     * Allows to query for the annotation object of a given content part.
     *
     * @param contentPartId The id (url) of the content part.
     * @return The annotation object of the given content part.
     *
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ParseException
     */
    public Annotation getAnnotationOfContentPart(String contentPartId) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        QueryService qs = anno4j.createQueryService()
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addCriteria("^mico:hasContent", contentPartId);

        processTypeRestriction(qs);

        return (Annotation) qs.execute().get(0);
    }

    /**
     * Allows to query for all annotations related to a specific MIME type,
     * e.g. "images/jpeg" or "video/mp4".
     *
     * @param mimeType The specific MIME type.
     * @return List of annotations related to the specific MIME type.
     *
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ParseException
     */
    public List<Annotation> getAnnotationsByMIMEType(String mimeType) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        QueryService qs =  anno4j.createQueryService()
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addPrefix(DCTERMS.PREFIX, DCTERMS.NS)
                .addCriteria("^mico:hasContent/^mico:hasContentPart/mico:hasContentPart/dct:type", mimeType);

        processTypeRestriction(qs);

        return qs.execute();
    }

    /**
     * Allows to query for all annotations, specifying the name of the source,
     * e.g. the name of the injected image.
     *
     * @param sourceName The name of the injected source.
     * @return List of annotations related to the specific source name.
     *
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ParseException
     */
    public List<Annotation> getAnnotationsBySourceName(String sourceName) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        QueryService qs = anno4j.createQueryService()
                .addPrefix(MICO.PREFIX, MICO.NS)
                .addPrefix(DCTERMS.PREFIX, DCTERMS.NS)
                .addCriteria("^mico:hasContent/^mico:hasContentPart/mico:hasContentPart/dct:source", sourceName);

        processTypeRestriction(qs);

        return qs.execute();
    }

    /**
     * @param type The type of the body as String, i.e. "mico:AVQBody"
     */
    public MICOQueryHelper filterBodyType(String type) {
        this.bodyTypeRestriction = "[is-a "+ type + "]";
        return this;
    }

    /**
     * @param type The type of the selector as String, i.e. "oa:FragmentSelector"
     */
    public MICOQueryHelper filterSelectorType(String type) {
        this.selectorTypeRestriction  = "[is-a "+ type + "]";
        return this;
    }

    /**
     * @param type The type of the target as String, i.e. "mico:IntialTarget"
     */
    public MICOQueryHelper filterTargetType(String type) {
        this.targetTypeRestriction = "[is-a "+ type + "]";
        return this;
    }

    /**
     * Checks if type restrictions were set and adds them to the QueryService object.
     *
     * @param qs The anno4j QueryService object
     */
    private void processTypeRestriction(QueryService qs) {
        if(selectorTypeRestriction != null) {

            qs.addCriteria("oa:hasTarget/oa:hasSelector" + selectorTypeRestriction);
        }

        if(bodyTypeRestriction != null) {
            qs.addCriteria("oa:hasBody" + bodyTypeRestriction);
        }

        if(targetTypeRestriction != null) {
            qs.addCriteria("oa:hasTarget" + targetTypeRestriction);
        }
    }
}
