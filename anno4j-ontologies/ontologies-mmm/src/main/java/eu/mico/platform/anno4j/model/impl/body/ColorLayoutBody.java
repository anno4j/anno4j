package eu.mico.platform.anno4j.model.impl.body;

import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.COLORLAYOUT_BODY)
public interface ColorLayoutBody extends MicoBody {

    @Iri(MMM.YDCCOEFF)
    String getYDC();

    @Iri(MMM.YDCCOEFF)
    void setYDC(String YDC);

    @Iri(MMM.CBDCCOEFF)
    String getCbDC();

    @Iri(MMM.CBDCCOEFF)
    void setCbDC(String cbDC);

    @Iri(MMM.CRDCCOEFF)
    String getCrDC();

    @Iri(MMM.CRDCCOEFF)
    void setCrDC(String crDC);

    @Iri(MMM.CBACCOEFF)
    String getCbAC();

    @Iri(MMM.CBACCOEFF)
    void setCbAC(String cbAC);

    @Iri(MMM.CRACCOEFF)
    String getCrAC();

    @Iri(MMM.CRACCOEFF)
    void setCrAC(String crAC);

    @Iri(MMM.YACCOEFF)
    String getYAC();

    @Iri(MMM.YACCOEFF)
    void setYAC(String YAC);
}
