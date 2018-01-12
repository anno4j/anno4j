package com.github.anno4j.model.impl;

import com.github.anno4j.annotations.Partial;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
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
     * Returns true if the resource object this support class belongs to has the given type,
     * i.e. a {@code rdf:type}-path exists from the resource to the given class.
     * @param type The type for which to check. This should be (but is not required to be) a RDFS/OWL class.
     * @return Returns true iff the resource has the specified {@code type}.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    protected boolean isInstance(Resource type) throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            // Do a SPARQL ASK query for a rdf:type edge or a subclass relationship of any assigned type:
            BooleanQuery query = connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                    "ASK {" +
                            "   {" +
                            "       <" + getResourceAsString() + "> rdf:type+ <" + type.toString() + "> . " +
                            "   } UNION {" +
                            "       <" + getResourceAsString() + "> a ?c ." +
                            "       ?c rdfs:subClassOf+ <" + type.toString() + "> . " +
                            "   }" +
                            "}"
            );
            return query.evaluate();

        } catch (MalformedQueryException | RepositoryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns true if the resource object this support class belongs to has the given type,
     * i.e. a {@code rdf:type}-path exists from the resource to the given class.
     * @param type The type for which to check. This should be (but is not required to be) a RDFS/OWL class.
     * @return Returns true iff the resource has the specified {@code type}.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    protected boolean isInstance(Class<? extends ResourceObject> type) throws RepositoryException {
        // Get the types @Iri annotation:
        Iri annotation = type.getAnnotation(Iri.class);
        if(annotation != null) {
            return isInstance(new URIImpl(annotation.value()));

        } else {
            // If the type has no @Iri annotation it's not a class and thus this resource isn't an instance:
            return false;
        }
    }

    /**
     * Returns the resource object with the given type. This can be used to safely "down-cast" types if required.
     * A check is performed whether the resource has actually the requested type.
     * @param type The type requested.
     * @param <T> The type requested.
     * @return Returns the resource object as an instance of the requested type.
     * @throws RepositoryException Thrown if an error occurs accessing the repository.
     * @throws ClassCastException Thrown if the resource doesn't have the requested type.
     */
    protected <T extends ResourceObject> T cast(Class<T> type) throws RepositoryException, ClassCastException {
        // First check if the resource has really the requested type:
        if(isInstance(type)) {
            // Retrieve the object with the requested type from the object connection:
            ObjectConnection connection = getObjectConnection();
            try {
                return connection.findObject(type, getResource());
            } catch (QueryEvaluationException e) {
                throw new RepositoryException(e);
            }
        } else {
            // The resource object isn't an instance of the requested target type:
            throw new ClassCastException(getResourceAsString() + " can't be cast to " + type.getName());
        }
    }

    /**
     * Method returns a textual representation of the given ResourceObject in a supported serialisation format.
     *
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
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
    public void setResource(Resource resource) throws MalformedQueryException, RepositoryException, UpdateExecutionException {

        String subjectQuery = "DELETE {?old ?p ?o}" +
                " INSERT {?new ?p ?o}" +
                " WHERE {" +
                " BIND(<"+this.getResourceAsString()+"> as ?old)" +
                " BIND(<"+resource+"> as ?new)" +
                " ?old ?p ?o.};";

        String predicateQuery = "DELETE {?s ?old ?o}" +
                " INSERT {?s ?new ?o}" +
                " WHERE {" +
                " BIND(<"+this.getResourceAsString()+"> as ?old)" +
                " BIND(<"+resource+"> as ?new)" +
                " ?s ?old ?o.};";

        String objectQuery = "DELETE {?s ?p ?old}" +
                " INSERT {?s ?p ?new}" +
                " WHERE {" +
                " BIND(<"+this.getResourceAsString()+"> as ?old)" +
                " BIND(<"+resource+"> as ?new)" +
                " ?s ?p ?old.};";

        String query;
        if (this.getObjectConnection().getInsertContext() != null) {
            String with = "With <" + this.getObjectConnection().getInsertContext().stringValue()+ "> ";
            query = with + subjectQuery + " " + with + predicateQuery + " " + with + objectQuery;
        } else {
            query = subjectQuery + " " + predicateQuery + " " + objectQuery;
        }

        this.getObjectConnection().getDelegate().prepareUpdate(QueryLanguage.SPARQL, query).execute();

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

        if (proceed == null) {
            return this.resource;
        } else if (!IDGenerator.BLANK_RESOURCE.equals(this.resource)) {
            return this.resource;
        } else {
            return proceed;
        }
    }

    /**
     * Sets new Unique identifier for the instance by a given String.
     *
     * @param resourceAsString Textual representation of the new value of Unique identifier for the instance.
     */
    @Override
    public void setResourceAsString(String resourceAsString) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
        this.setResource(new URIImpl(resourceAsString));
    }

    /**
     * Gets new identifier for this instance as String.
     *
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return getResource().stringValue();
    }

    @Override
    public void delete() {
        try {
            ObjectConnection connection = getObjectConnection();
            connection.removeDesignation(this, (URI) getResource());
            // explicitly removing the rdf type triple from the repository
            connection.remove(getResource(), null, null);
            connection.remove(null, null, getResource(), null);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

}
