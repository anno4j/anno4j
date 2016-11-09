package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.util.TimeHelper;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Precedes;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

@Partial
@Precedes(ResourceObjectSupport.class)
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

        if (this.getTargets() != null) {
            targets.addAll(this.getTargets());
        }

        targets.add(target);
        this.setTargets(targets);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addBody(Body body) {
        HashSet<Body> bodies = new HashSet<>();

        if (this.getBodies() != null) {
            bodies.addAll(this.getBodies());
        }

        bodies.add(body);
        this.setBodies(bodies);
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
     * {@inheritDoc}
     */
    @Override
    public void addAudience(Audience audience) {
        HashSet<Audience> audiences = new HashSet<>();

        if(this.getAudiences() != null) {
            audiences.addAll(this.getAudiences());
        }

        audiences.add(audience);
        this.setAudiences(audiences);
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

            if (getBodies() != null) {
                for (Body body : getBodies()) {
                    sb.append(body.getTriples(RDFFormat.NTRIPLES));
                }
            }

            if (getTargets() != null) {
                for (Target target : getTargets()) {
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
            if (getBodies() != null) {
                for (Body body : getBodies()) {
                    body.delete();
                }
                setBodies(null);
            }

            // deleting existing targets one by one
            if (getTargets() != null) {
                for (Target target : getTargets()) {
                    target.delete();
                }
                setTargets(null);
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
        this.setGenerated(TimeHelper.createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setAnnotatedAt(TimeHelper.createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Deprecated
    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setSerializedAt(TimeHelper.createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setGenerated(String generated) {
        if (generated == null || TimeHelper.testTimeString(generated)) {
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
        if (serializedAt == null || TimeHelper.testTimeString(serializedAt)) {
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
        if (annotatedAt == null || TimeHelper.testTimeString(annotatedAt)) {
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
