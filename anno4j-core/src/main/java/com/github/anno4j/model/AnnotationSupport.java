package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@Partial
public abstract class AnnotationSupport extends CreationProvenanceSupport implements Annotation {

    /**
     * Refers to http://www.w3.org/ns/oa#annotatedAt.
     * Deprecated property.
     */
    @Iri(OADM.ANNOTATED_AT)
    private String annotatedAt;

    /**
     * Refers to http://www.w3.org/ns/oa#serializedAt.
     * Deprecated property.
     */
    @Iri(OADM.SERIALIZED_AT)
    private String serializedAt;

    /**
     * Refers to http://purl.org/dc/terms/issued.
     */
    @Iri(DCTERMS.ISSUED)
    private String generated;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTarget(Target target) {
        HashSet<Target> targets = new HashSet<>();

        if (this.getTarget() != null) {
            targets.addAll(this.getTarget());
        }

        targets.add(target);
        this.setTarget(targets);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMotivation(Motivation motivation) {
        HashSet<Motivation> motivations = new HashSet<>();

        if (this.getMotivatedBy() != null) {
            motivations.addAll(this.getMotivatedBy());
        }

        motivations.add(motivation);
        this.setMotivatedBy(motivations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBodyText(String text) {
        HashSet<String> texts = new HashSet<>();

        if (this.getBodyTexts() != null) {
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
            if (this.getSerializedAt() != null) {
                this.setSerializedAt(null);
            }

            if (this.getSerializedBy() != null) {
                this.setSerializedBy(null);
            }

            if (this.getAnnotatedAt() != null) {
                this.setAnnotatedAt(null);
            }

            if (this.getAnnotatedBy() != null) {
                this.setAnnotatedBy(null);
            }

            if (this.getCreator() != null) {
                this.setCreator(null);
            }

            if (this.getMotivatedBy() != null) {
                this.setMotivatedBy(null);
            }

            if (this.getCreated() != null) {
                this.setCreated(null);
            }

            if (this.getGenerated() != null) {
                this.setGenerated(null);
            }

            if (this.getGenerator() != null) {
                this.setGenerator(null);
            }

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
    public void setGenerated(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setGenerated(createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setAnnotatedAt(createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setSerializedAt(createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setGenerated(String generated) {
        if (generated == null || this.testTimeString(generated)) {
            this.generated = generated;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(String serializedAt) {
        if (serializedAt == null || this.testTimeString(serializedAt)) {
            this.serializedAt = serializedAt;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setAnnotatedAt(String annotatedAt) {
        if (annotatedAt == null || this.testTimeString(annotatedAt)) {
            this.annotatedAt = annotatedAt;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String getGenerated() {
        return this.generated;
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public String getSerializedAt() {
        return this.serializedAt;
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public String getAnnotatedAt() {
        return this.annotatedAt;
    }
}
