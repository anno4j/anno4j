package com.github.anno4j.model.impl.targets;

import com.github.anno4j.model.ExternalWebResourceSupport;
import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.State;
import com.github.anno4j.model.impl.ResourceObject;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

@Partial
public abstract class SpecificResourceSupport extends ExternalWebResourceSupport implements SpecificResource {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStyleClass(String styleClass) {
        HashSet<String> styleClasses = new HashSet<>();

        if(this.getStyleClasses() != null) {
            styleClasses.addAll(this.getStyleClasses());
        }

        styleClasses.add(styleClass);
        this.setStyleClasses(styleClasses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addState(State state) {
        HashSet<State> states = new HashSet<>();

        Set<State> current = this.getStates();

        if(current != null) {
            states.addAll(current);
        }

        states.add(state);
        this.setStates(states);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRenderedVia(ResourceObject renderedVia) {
        HashSet<ResourceObject> rendered = new HashSet<>();

        Set<ResourceObject> current = this.getRenderedVia();

        if(current != null) {
            rendered.addAll(current);
        }

        rendered.add(renderedVia);
        this.setRenderedVia(rendered);
    }

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
        } catch (IOException | RDFHandlerException | RDFParseException | RepositoryException e) {
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
