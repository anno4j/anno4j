package eu.mico.platform.anno4j.model;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Target;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import java.util.Set;

/**
 * Class represents a Part. A Part resembles an extractor step and consecutively an (intermediary)
 * result of an Item and its extraction chain.
 */
@Iri(MMM.PART)
public interface Part extends Annotation {

    /**
     * Gets http:www.w3.org/ns/oa#hasBody relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasBody.
     */
    @Iri(MMM.HAS_BODY)
    @Override
    Body getBody();

    /**
     * Sets http:www.w3.org/ns/oa#hasBody.
     *
     * @param body New value of http:www.w3.orgnsoa#hasBody.
     */
    @Iri(MMM.HAS_BODY)
    @Override
    void setBody(Body body);

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationships.
     *
     * @return Values of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(MMM.HAS_TARGET)
    @Override
    Set<Target> getTarget();

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget.
     *
     * @param targets New value of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(MMM.HAS_TARGET)
    @Override
    void setTarget(Set<Target> targets);

    /**
     * Gets the objects that were the semantical input for this Part.
     *
     * @return A set of objects that are used as semantical input for creating this Part.
     */
    @Iri(MMM.HAS_INPUT)
    Set<RDFObject> getInputs();

    /**
     * Sets the Set of objects that are the semantical input for this Part.
     *
     * @param inputs    The set of objects that form the semantical input for this Part
     */
    @Iri(MMM.HAS_INPUT)
    void setInputs(Set<RDFObject> inputs);

    /**
     * Adds a single object to the set of objects, that form the semantical input for this Part.
     *
     * @param input The object that is to be added to the set of objects, that form the semantical input for this part.
     */
    void addInput(RDFObject input);

    /**
     * Adds a http:www.w3.org/ns/oa#hasTarget relationship.
     *
     * @param target New http:www.w3.org/ns/oa#hasTarget relationship.
     */
    @Override
    void addTarget(Target target);
}
