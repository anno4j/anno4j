package eu.mico.platform.anno4j.model.impl.body;

import com.github.anno4j.model.namespaces.DC;
import eu.mico.platform.anno4j.model.MicoBody;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;

@Iri(MMM.MULTIMEDIA_BODY)
public interface MultiMediaBody extends MicoBody {

    @Iri(DC.FORMAT)
    String getFormat();

    @Iri(DC.FORMAT)
    void setFormat(String format);
}
