package com.github.anno4j.model.impl.target;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.apache.commons.io.IOUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.rio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Conforms to http://www.w3.org/ns/oa#SpecificResource
 *
 * A resource identifies part of another Source resource, a particular representation of a resource, a resource with styling hints for renders, or any combination of these.
 *
 * The Specific Resource takes the role of oa:hasBody or oa:hasTarget in an oa:Annotation instead of the Source resource.
 *
 * There MUST be exactly 1 oa:hasSource relationship associated with a Specific Resource.
 *
 * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource.
 *
 * There MAY be 0 or 1 oa:hasState relationship for each Specific Resource.
 *
 * If the Specific Resource has an HTTP URI, then the exact segment of the Source resource that it identifies, and only the segment, MUST be returned when the URI is dereferenced. For example, if the segment of interest is a region of an image and the Specific Resource has an HTTP URI, then dereferencing it MUST return the selected region of the image as it was at the time when the annotation was created. Typically this would be a burden to support, and thus the Specific Resource SHOULD be identified by a globally unique URI, such as a UUID URN. If it is not considered important to allow other Annotations or systems to refer to the Specific Resource, then a blank node MAY be used instead.
 */
@Iri(OADM.SPECIFIC_RESOURCE)
public class SpecificResource extends Target {

    /**
     * Refers to http://www.w3.org/ns/oa#hasSource
     * The relationship between a Specific Resource and the resource that it is a more specific representation of.
     * There must be exactly 1 oa:hasSource relationship associated with a Specific Resource.
     */
    @Iri(OADM.HAS_SOURCE)   private ResourceObject source;

    /**
     * Refers to http://www.w3.org/ns/oa#hasSelector
     * The relationship between a oa:SpecificResource and a oa:Selector.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource.
     */
    @Iri(OADM.HAS_SELECTOR) private Selector selector;

    /**
     * Refers to http://www.w3.org/ns/oa#hasScope
     * The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     * There MAY be 0 or more hasScope relationships for each Specific Resource.
     */
    @Iri(OADM.HAS_SCOPE)    private ResourceObject scope;

    /**
     * Standard constructor.
     */
    public SpecificResource() {};

    /**
     * Constructor setting the source and selector variables.
     * @param source    Specifies the original target of the corresponding annotation.
     * @param selector  Points to the given selector.
     */
    public SpecificResource(ResourceObject source, Selector selector) {
        this.source = source;
        this.selector = selector;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#hasSelector
     * The relationship between a oa:SpecificResource and a oa:Selector.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#hasSelector
     * The relationship between a oa:SpecificResource and a oa:Selector.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource..
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#hasSelector
     * The relationship between a oa:SpecificResource and a oa:Selector.
     * There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource..
     *
     * @param selector New value of Refers to http:www.w3.orgnsoa#hasSelector
     *                 The relationship between a oa:SpecificResource and a oa:Selector.
     *                 There MUST be exactly 0 or 1 oa:hasSelector relationship associated with a Specific Resource..
     */
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#hasSource
     * The relationship between a Specific Resource and the resource that it is a more specific representation of.
     * There must be exactly 1 oa:hasSource relationship associated with a Specific Resource..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#hasSource
     * The relationship between a Specific Resource and the resource that it is a more specific representation of.
     * There must be exactly 1 oa:hasSource relationship associated with a Specific Resource..
     */
    public RDFObject getSource() {
        return source;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#hasSource
     * The relationship between a Specific Resource and the resource that it is a more specific representation of.
     * There must be exactly 1 oa:hasSource relationship associated with a Specific Resource..
     *
     * @param source New value of Refers to http:www.w3.orgnsoa#hasSource
     *               The relationship between a Specific Resource and the resource that it is a more specific representation of.
     *               There must be exactly 1 oa:hasSource relationship associated with a Specific Resource..
     */
    public void setSource(ResourceObject source) {
        this.source = source;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#hasScope
     * The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     * There MAY be 0 or more hasScope relationships for each Specific Resource..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#hasScope
     * The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     * There MAY be 0 or more hasScope relationships for each Specific Resource..
     */
    public RDFObject getScope() {
        return scope;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#hasScope
     * The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     * There MAY be 0 or more hasScope relationships for each Specific Resource..
     *
     * @param scope New value of Refers to http:www.w3.orgnsoa#hasScope
     *              The relationship between a Specific Resource and the resource that provides the scope or context for it in this Annotation.
     *              There MAY be 0 or more hasScope relationships for each Specific Resource..
     */
    public void setScope(ResourceObject scope) {
        this.scope = scope;
    }

    @Override
    public String getTriples(RDFFormat format) {
        assert this.getObjectConnection() != null : this.getClass().getCanonicalName() + "is not stored in any object store";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
        RDFWriter writer = Rio.createWriter(format, out);
        parser.setRDFHandler(writer);
        try {
            parser.parse(IOUtils.toInputStream(super.getTriples(RDFFormat.NTRIPLES), "UTF-8"), "");
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
        }

        return out.toString();
    }

    @Override
    public String toString() {
        return "SpecificResource{" +
                "resource='" + this.getResource() + "'" +
                ", source=" + source +
                ", selector=" + selector +
                ", scope=" + scope +
                "}'";
    }
}