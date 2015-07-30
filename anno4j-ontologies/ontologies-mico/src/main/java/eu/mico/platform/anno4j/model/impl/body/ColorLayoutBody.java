package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.Body;
import eu.mico.platform.anno4j.model.namespaces.MICO;
import org.openrdf.annotations.Iri;

@Iri(MICO.COLORLAYOUT_BODY)
public class ColorLayoutBody extends Body {

    @Iri(MICO.YDCCOEFF)
    private String YDC;

    @Iri(MICO.CBDCCOEFF)
    private String CbDC;

    @Iri(MICO.CRDCCOEFF)
    private String CrDC;

    @Iri(MICO.CBACCOEFF)
    private String CbAC;

    @Iri(MICO.CRACCOEFF)
    private String CrAC;

    @Iri(MICO.YACCOEFF)
    private String YAC;

    public ColorLayoutBody() {
    }

    public ColorLayoutBody(String YDC, String CbDC, String CrDC, String CbAC, String CrAC, String YAC) {
        this.YDC = YDC;
        this.CbDC = CbDC;
        this.CrDC = CrDC;
        this.CbAC = CbAC;
        this.CrAC = CrAC;
        this.YAC = YAC;
    }

    public String getYDC() {
        return YDC;
    }

    public void setYDC(String YDC) {
        this.YDC = YDC;
    }

    public String getCbDC() {
        return CbDC;
    }

    public void setCbDC(String cbDC) {
        CbDC = cbDC;
    }

    public String getCrDC() {
        return CrDC;
    }

    public void setCrDC(String crDC) {
        CrDC = crDC;
    }

    public String getCbAC() {
        return CbAC;
    }

    public void setCbAC(String cbAC) {
        CbAC = cbAC;
    }

    public String getCrAC() {
        return CrAC;
    }

    public void setCrAC(String crAC) {
        CrAC = crAC;
    }

    public String getYAC() {
        return YAC;
    }

    public void setYAC(String YAC) {
        this.YAC = YAC;
    }
}
