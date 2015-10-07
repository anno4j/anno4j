package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.COLORLAYOUT_BODY)
public interface ColorLayoutBody extends Body {

    @Iri(MICO.YDCCOEFF)
    String getYDC();

    @Iri(MICO.YDCCOEFF)
    void setYDC(String YDC);

    @Iri(MICO.CBDCCOEFF)
    String getCbDC();

    @Iri(MICO.CBDCCOEFF)
    void setCbDC(String cbDC);

    @Iri(MICO.CRDCCOEFF)
    String getCrDC();

    @Iri(MICO.CRDCCOEFF)
    void setCrDC(String crDC);

    @Iri(MICO.CBACCOEFF)
    String getCbAC();

    @Iri(MICO.CBACCOEFF)
    void setCbAC(String cbAC);

    @Iri(MICO.CRACCOEFF)
    String getCrAC();

    @Iri(MICO.CRACCOEFF)
    void setCrAC(String crAC);

    @Iri(MICO.YACCOEFF)
    String getYAC();

    @Iri(MICO.YACCOEFF)
    void setYAC(String YAC);
}
