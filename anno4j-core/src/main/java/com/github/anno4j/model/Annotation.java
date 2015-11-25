package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.*;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Conforms to oa:Annotation (http://www.openannotation.org/spec/core/core.html)
 */
@Iri(OADM.ANNOTATION)
public class Annotation extends ResourceObject {

    /**
     * Refers to http://www.w3.org/ns/oa#hasBody
     */
    @Iri(OADM.HAS_BODY)
    private Body body;
    /**
     * Refers to http://www.w3.org/ns/oa#hasTarget
     */
    @Iri(OADM.HAS_TARGET)
    private Set<Target> targets;
    /**
     * Refers to http://www.w3.org/ns/oa#motivatedBy
     */
    @Iri(OADM.MOTIVATED_BY)
    private Motivation motivatedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#serializedBy
     */
    @Iri(OADM.SERIALIZED_BY)
    private Agent serializedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#serializedAt
     */
    @Iri(OADM.SERIALIZED_AT)
    private String serializedAt;
    /**
     * Refers to http://www.w3.org/ns/oa#annotatedBy
     */
    @Iri(OADM.ANNOTATED_BY)
    private Agent annotatedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#annotatedAt
     */
    @Iri(OADM.ANNOTATED_AT)
    private String annotatedAt;

    /**
     * Constructor.
     */
    public Annotation() {
        this.targets = new HashSet<>();
    }

    /**
     * Gets http:www.w3.org/ns/oa#hasBody relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasBody.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Sets http:www.w3.org/ns/oa#hasBody.
     *
     * @param body New value of http:www.w3.orgnsoa#hasBody.
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationship. DEPRECATED: Use getTargets() instead. The result of this implementation may vary with mutliple targets.
     *
     * @return Value of http:www.w3.org/ns/oa#hasTarget.
     */
    @Deprecated
    public Target getTarget() {
        if (this.targets != null && this.targets.size() > 0) {
            return this.targets.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationships.
     *
     * @return Values of http:www.w3.org/ns/oa#hasTarget.
     */
    public Set<Target> getTargets() {
        return this.targets;
    }

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget. DEPRECATED: Use setTargets() or addTarget() instead. The current implementation replaces the current targets with the new target.
     *
     * @param target New value of http:www.w3.org/ns/oa#hasTarget.
     */
    @Deprecated
    public void setTarget(Target target) {
        this.targets = new HashSet<>();
        this.targets.add(target);
    }

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget.
     *
     * @param targets New value of http:www.w3.org/ns/oa#hasTarget.
     */
    public void setTargets(Set<Target> targets) {
        this.targets = targets;
    }

    /**
     * Adds a http:www.w3.org/ns/oa#hasTarget relationship.
     *
     * @param target New http:www.w3.org/ns/oa#hasTarget relationship.
     */
    public void addTarget(Target target) {
        if (this.targets == null) {
            this.targets = new HashSet<>();
        }

        this.targets.add(target);
    }

    /**
     * Gets http:www.w3.org/ns/oa#motivatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa/#motivatedBy.
     */
    public Motivation getMotivatedBy() {
        return motivatedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#motivatedBy.
     *
     * @param motivatedBy New value of http:www.w3.org/ns/oa#motivatedBy.
     */
    public void setMotivatedBy(Motivation motivatedBy) {
        this.motivatedBy = motivatedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#serializedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedBy.
     */
    public Agent getSerializedBy() {
        return serializedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#serializedBy.
     *
     * @param serializedBy New value of http:www.w3.org/ns/oa#serializedBy.
     */
    public void setSerializedBy(Agent serializedBy) {
        this.serializedBy = serializedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#annotatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedBy.
     */
    public Agent getAnnotatedBy() {
        return annotatedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#annotatedBy.
     *
     * @param annotatedBy New value of http:www.w3.org/ns/oa#annotatedBy.
     */
    public void setAnnotatedBy(Agent annotatedBy) {
        this.annotatedBy = annotatedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#serializedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedAt.
     */
    public String getSerializedAt() {
        return serializedAt;
    }

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt.
     *
     * @param serializedAt New value of http:www.w3.org/ns/oa#serializedAt.
     */
    public void setSerializedAt(String serializedAt) {
        this.serializedAt = serializedAt;
    }

    /**
     * Gets http:www.w3.org/ns/oa#annotatedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedAt.
     */
    public String getAnnotatedAt() {
        return annotatedAt;
    }

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt.
     *
     * @param annotatedAt New value of http:www.w3.org/ns/oa#annotatedAt.
     */
    public void setAnnotatedAt(String annotatedAt) {
        this.annotatedAt = annotatedAt;
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
        parser.setRDFHandler(Rio.createWriter(format, out));

        try {
            StringBuilder sb = new StringBuilder();

            sb.append(super.getTriples(RDFFormat.NTRIPLES));

            if (getBody() != null) {
                sb.append(getBody().getTriples(RDFFormat.NTRIPLES));
            }

            if (getTargets() != null) {
                for(Target target : getTargets()) {
                    sb.append(target.getTriples(RDFFormat.NTRIPLES));
                }
            }
            parser.parse(IOUtils.toInputStream(sb.toString()), "");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "resource='" + this.getResource() + "'" +
                ", body=" + ((body != null) ? body.toString() : "empty") +
                ", targets=" + ((targets != null) ? targets.toString() : "empty") +
                ", motivatedBy=" + motivatedBy +
                ", serializedBy=" + serializedBy +
                ", serializedAt='" + serializedAt + '\'' +
                ", annotatedBy=" + annotatedBy +
                ", annotatedAt='" + annotatedAt + '\'' +
                '}';
    }
}