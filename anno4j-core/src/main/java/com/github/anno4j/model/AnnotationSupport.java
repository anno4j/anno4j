package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.OADM;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@Partial
public abstract class AnnotationSupport extends CreationProvenanceSupport implements Annotation {

    @Override
    public void addTarget(Target target) {
        HashSet<Target> targets = new HashSet<>();

        if(this.getTarget() != null) {
            targets.addAll(this.getTarget());
        }

        targets.add(target);
        this.setTarget(targets);
    }

    @Override
    public void addMotivation(Motivation motivation) {
        HashSet<Motivation> motivations = new HashSet<>();

        if (this.getMotivatedBy() != null) {
            motivations.addAll(this.getMotivatedBy());
        }

        motivations.add(motivation);
        this.setMotivatedBy(motivations);
    }


    @Override
    public void addBodyText(String text) {
        HashSet<String> texts = new HashSet<>();

        if(this.getBodyTexts() != null) {
            texts.addAll(this.getBodyTexts());
        }

        texts.add(text);
        this.setBodyTexts(texts);
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

            if (getCreator() != null) {
                sb.append(getCreator().getTriples(RDFFormat.NTRIPLES));
            }

            if (getGenerator() != null) {
                sb.append(getGenerator().getTriples(RDFFormat.NTRIPLES));
            }

            if (getMotivatedBy() != null) {
                for (Motivation motivation : this.getMotivatedBy()) {
                    sb.append(motivation.getTriples(RDFFormat.NTRIPLES));
                }
            }

            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (IOException | RDFHandlerException | RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
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
            setCreated(null);
            setCreator(null);
            setMotivatedBy(null);
            setGenerated(null);
            setGenerator(null);
            setSerializedAt(null);
            setSerializedBy(null);
            setAnnotatedAt(null);
            setAnnotatedBy(null);


            // finally removing this annotation
            connection.removeDesignation(this, (URI) getResource());
            // explicitly removing the rdf type triple from the repository
            connection.remove(getResource(), null, null);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setGenerated(int year, int month, int day, int hours, int minutes, int seconds) {

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

        this.setGenerated(builder.toString());
    }

    @Deprecated
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

    @Deprecated
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
}
