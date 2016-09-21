package com.github.anno4j.model.impl.targets;

import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.annotations.Partial;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Partial
public abstract class SpecificResourceSupport implements SpecificResource {

    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
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

    @Override
    public void delete() {
        try {
            ObjectConnection connection = getObjectConnection();

            // deleting an existing selector
            if(getSelector() != null) {
                getSelector().delete();
                setSelector(null);
            }

            connection.removeDesignation(this, (URI) getResource());
            // explicitly removing the rdf type triple from the repository
            connection.remove(getResource(), null, null);
            connection.remove(null, null, getResource(), null);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
