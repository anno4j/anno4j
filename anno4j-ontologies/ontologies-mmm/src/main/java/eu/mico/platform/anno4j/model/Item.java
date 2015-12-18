package eu.mico.platform.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import eu.mico.platform.anno4j.model.provenance.Asset;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Class represents an Item of the MICO workflow.
 * For every ingested multimedia file at the platform, an Item will be created which corresponds
 * (with its follow Parts) to the file with its analysed metadata background.
 */
@Iri(MMM.ITEM)
public interface Item extends ResourceObject {

    /**
     * Gets the available Parts over corresponding http://www.mico-project.eu/ns/mmm/2.0/schema#hasPart relationships.
     *
     * @return Values of http://www.mico-project.eu/ns/mmm/2.0/schema#hasPart.
     */
    @Iri(MMM.HAS_PART)
    Set<Part> getParts();

    /**
     * Sets a set of Parts by specifying http://www.mico-project.eu/ns/mmm/2.0/schema#hasPart relationships.
     *
     * @param parts New values of http://www.mico-project.eu/ns/mmm/2.0/schema#hasPart.
     */
    @Iri(MMM.HAS_PART)
    void setParts(Set<Part> parts);

    /**
     * Adds a new single http://www.mico-project.eu/ns/mmm/2.0/schema#hasPart relationship to this Item.
     *
     * @param part The new Part to add.
     */
    void addPart(Part part);

    /**
     * Gets this Item's corresponding Asset over the http://www.mico-project.eu/ns/mmm/2.0/schema#hasAsset relationship.
     *
     * @return The corresponding Asset of this Item.
     */
    @Iri(MMM.HAS_ASSET)
    Asset getAsset();

    /**
     * Sets the Asset of this Item, by specifying the http://www.mico-project.eu/ns/mmm/2.0/schema#hasAsset relationship.
     *
     * @param asset The Asset to set.
     */
    @Iri(MMM.HAS_ASSET)
    void setAsset(Asset asset);

    @Iri(OADM.SERIALIZED_AT)
    String getSerializedAt();

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt.
     *
     * @param serializedAt New value of http:www.w3.org/ns/oa#serializedAt.
     */
    @Iri(OADM.SERIALIZED_AT)
    void setSerializedAt(String serializedAt);

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt according to the format year-month-dayThours:minutes:secondsZ, e.g. 2015-12-16T12:00:00Z.
     *
     * @param year      The year to set.
     * @param month     The month to set.
     * @param day       The day to set.
     * @param hours     The hours to set.
     * @param minutes   The minutes to set.
     * @param seconds   The seconds to set.
     */
    void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds);
}
