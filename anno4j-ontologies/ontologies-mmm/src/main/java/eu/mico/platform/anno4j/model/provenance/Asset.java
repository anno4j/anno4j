package eu.mico.platform.anno4j.model.provenance;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.DC;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

/**
 * This class represents an Asset for an Item or a Part. An Asset stands for a multimedia file, associated with its
 * format and location.
 */
@Iri(MMM.ASSET)
public interface Asset extends ResourceObject {

    /**
     * Gets this Asset's corresponding location over the http://www.mico-project.eu/ns/mmm/2.0/schema#hasLocation relationship.
     *
     * @return The corresponding location of this Asset.
     */
    @Iri(MMM.HAS_LOCATION)
    String getLocation();

    /**
     * Sets this Asset's corresponding location over the http://www.mico-project.eu/ns/mmm/2.0/schema#hasLocation relationship.
     *
     * @param location   The corresponding location of this Asset.
     */
    @Iri(MMM.HAS_LOCATION)
    void setLocation(String location);

    /**
     * Gets this Asset's corresponding format over the dc:format relationship.
     *
     * @return The format of this Asset.
     */
    @Iri(DC.FORMAT)
    String getFormat();

    /**
     * Sets this Asset's corresponding format over the dc:format relationship.
     *
     * @param format    The format to set.
     */
    @Iri(DC.FORMAT)
    void setFormat(String format);
}
