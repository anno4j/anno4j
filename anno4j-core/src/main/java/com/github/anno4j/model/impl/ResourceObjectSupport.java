package com.github.anno4j.model.impl;

import com.github.anno4j.annotations.Partial;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.ByteArrayOutputStream;

@Partial
public abstract class ResourceObjectSupport implements ResourceObject {

    private Resource resource = IDGenerator.BLANK_RESOURCE;

    /**
     * Method returns a textual representation of the given ResourceObject in a supported serialisation format.
     *
     * @param format    The format which should be printed.
     * @return          A textual representation if this object in the format.
     */
    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            RDFWriter writer = Rio.createWriter(format, out);
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Gets Unique identifier for the instance.
     *
     * @return Value of Unique identifier for the instance..
     */
    @ParameterTypes({})
    public Resource getResource(ObjectMessage msg) throws Exception {
        Resource proceed = (Resource) msg.proceed();

        if(proceed == null ) {
            return this.resource;
        } else if(!IDGenerator.BLANK_RESOURCE.equals(this.resource)) {
            return this.resource;
        } else  {
            return proceed;
        }
    }

    /**
     * Sets new Unique identifier for the instance by a given String.
     *
     * @param resourceAsString Textual representation of the new value of Unique identifier for the instance.
     */
    @Override
    public void setResourceAsString(String resourceAsString) {
        this.setResource(new URIImpl(resourceAsString));
    }

    /**
     * Gets new identifier for this instance as String.
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return getResource().stringValue();
    }

}
