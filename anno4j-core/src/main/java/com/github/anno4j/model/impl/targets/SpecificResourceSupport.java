package com.github.anno4j.model.impl.targets;

import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.persistence.annotation.Partial;
import org.apache.commons.io.IOUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Partial
public abstract class SpecificResourceSupport extends AnnotationSupport implements SpecificResource {

    @Override
    public String getTriples(RDFFormat format) {
        assert this.getObjectConnection() != null : this.getClass().getCanonicalName() + "is not stored in any object store";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            writer = Rio.createWriter(format, out);
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

            if (getSelector() != null) {
                parser.parse(IOUtils.toInputStream(getSelector().getTriples(RDFFormat.NTRIPLES), "UTF-8"), "");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

}
