package com.github.anno4j.model.impl.collection;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.Target;
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
 * Support class for the AnnotationCollection interface.
 */
@Partial
public abstract class AnnotationCollectionSupport extends ResourceObjectSupport implements AnnotationCollection {


    @Iri(AS.FIRST)
    private AnnotationPage firstPage;

    @Iri(AS.LAST)
    private AnnotationPage lastPage;

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationPage getLastPage() {
        return lastPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastPage(AnnotationPage lastPage) {
        this.lastPage = lastPage;
        lastPage.setPartOf(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastPageCascading(AnnotationPage page) {
        this.lastPage = page;
        page.setPartOf(this);

        AnnotationPage prev = page.getPrev();
        while(prev != null) {
            prev.setPartOf(this);

            prev = prev.getPrev();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationPage getFirstPage() {
        return firstPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstPage(AnnotationPage firstPage) {
        this.firstPage = firstPage;
        firstPage.setPartOf(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstPageCascading(AnnotationPage page) {
        this.firstPage = page;
        page.setPartOf(this);

        AnnotationPage next = page.getNext();
        while(next != null) {
            next.setPartOf(this);

            next = next.getNext();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLabel(String label) {
        HashSet<String> labels = new HashSet<>();

        Set<String> current = this.getLabels();

        if(current != null) {
            labels.addAll(current);
        }

        labels.add(label);
        this.setLabels(labels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotal() {
        int total = 0;
        AnnotationPage page = this.getFirstPage();

        while(page != null) {
            total += page.getItems().size();
            page = page.getNext();
        }

        return total;
    }

    /**
     * Method returns a textual representation of the given AnnotationCollection, containing
     * .
     *
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
     */
    public String getTriplesExpanded(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            AnnotationPage first = this.getFirstPage();
            if(first != null) {
                sb.append(first.getTriples(RDFFormat.NTRIPLES));
            }

            AnnotationPage last = this.getLastPage();
            if(last != null) {
                sb.append(last.getTriples(RDFFormat.NTRIPLES));
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (IOException | RDFHandlerException | RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
}
