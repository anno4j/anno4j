package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.OADM;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Partial
public abstract class AnnotationSupport extends ResourceObjectSupport implements Annotation {

    @Override
    public Set<Target> getTarget() {
        return target;
    }

    @Override
    public void setTarget(Set<Target> target) {

        if(target != null) {
            this.target.clear();
            this.target.addAll(target);
        } else {
            this.target.clear();
        }

    }

    @Iri(OADM.HAS_TARGET)
    private Set<Target> target = new HashSet<>();

    @Override
    public void addTarget(Target target) {
        if (this.getTarget() == null) {
            this.setTarget(new HashSet<Target>());
        }

        this.getTarget().add(target);
    }

    /**
     * Method returns a textual representation of the given Annotation, containing
     * its Body, Target and possible Selection, in a supported serialisation format.
     *
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
     */
    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            if (getBody() != null) {
                sb.append(getBody().getTriples(RDFFormat.NTRIPLES));
            }

            if (getTarget() != null) {
                for (Target target : getTarget()) {
                    sb.append(target.getTriples(RDFFormat.NTRIPLES));
                }
            }

            if (getAnnotatedBy() != null) {
                sb.append(getAnnotatedBy().getTriples(RDFFormat.NTRIPLES));
            }

            if (getSerializedBy() != null) {
                sb.append(getSerializedBy().getTriples(RDFFormat.NTRIPLES));
            }

            if (getMotivatedBy() != null) {
                sb.append(getMotivatedBy().getTriples(RDFFormat.NTRIPLES));
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (IOException | RDFHandlerException | RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds) {

        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if (hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if (minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if (seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        builder.append("Z");

        this.setSerializedAt(builder.toString());
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds) {

        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if (hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if (minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if (seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        builder.append("Z");

        this.setAnnotatedAt(builder.toString());
    }

    @Override
    public void delete() {
        try {
            ObjectConnection connection = getObjectConnection();

            // deleting an existing body
            if (getBody() != null) {
                getBody().delete();
                setBody(null);
            }

            // deleting existing targets one by one
            if (getTarget() != null) {
                for (Target target : getTarget()) {
                    target.delete();
                }
                setTarget(null);
            }

            // deleting possible provenance information
            setAnnotatedBy(null);
            setAnnotatedAt(null);
            setMotivatedBy(null);
            setSerializedBy(null);
            setSerializedAt(null);


            // finally removing this annotation
            connection.removeDesignation(this, (URI) getResource());
            // explicitly removing the rdf type triple from the repository
            connection.remove(getResource(), null, null);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
