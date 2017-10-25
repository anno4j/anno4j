package com.github.anno4j.model.impl.collection;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.AS;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the AnnotationPage interface.
 */
@Partial
public abstract class AnnotationPageSupport extends ResourceObjectSupport implements AnnotationPage {

    @Iri(AS.NEXT)
    private AnnotationPage next;

    @Iri(AS.PREV)
    private AnnotationPage prev;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(AnnotationPage page) {
        this.next = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationPage getNext() {
        return this.next;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNextSymmetric(AnnotationPage page) {
        this.next = page;
        if (page.getPrev() == null || !page.getPrev().equals(this)) {
            page.setPrev(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrev(AnnotationPage page) {
        this.prev = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationPage getPrev() {
        return this.prev;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrevSymmetric(AnnotationPage page) {
        this.prev = page;
        if (page.getNext() == null || !page.getNext().equals(this)) {
            page.setNext(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItem(Annotation annotation) {
        HashSet<Annotation> annotations = new HashSet<>();

        Set<Annotation> current = this.getItems();

        if (current != null) {
            annotations.addAll(current);
        }

        annotations.add(annotation);
        this.setItems(annotations);
    }

    /**
     * Method returns a textual representation of the given AnnotationCollection, containing
     * .
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
     */
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            if (this.getItems() != null) {
                for (Annotation item : this.getItems()) {
                    sb.append(item.getTriples(RDFFormat.NTRIPLES));
                }
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch ( RDFHandlerException | RDFParseException | IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
}
