package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSClazzSupport;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;

import java.util.Set;

/**
 * Support class for {@link BuildableRDFSClazz}.
 */
@Partial
public abstract class BuildableRDFSClazzSupport extends RDFSClazzSupport implements BuildableRDFSClazz {

    @Override
    public Set<RDFSProperty> getOutgoingProperties() throws RepositoryException {
        try {
            ObjectQuery query = getObjectConnection().prepareObjectQuery(
                    "SELECT ?p {" +
                            "   { " +
                            "       ?p rdfs:domain <" + getResourceAsString() + "> . " +
                            "   }" +
                            "   UNION" +
                            "   { " +
                            "       ?p rdfs:domain ?c . " +
                            "       <" + getResourceAsString() + "> rdfs:subClassOf+ ?c . " +
                            "   } " +
                            "   MINUS {" +
                            "       ?p2 rdfs:domain <" + getResourceAsString() + "> . " +
                            "       ?p owl:equivalentProperty+ ?p2 . " +
                            "       FILTER( str(?p2) < str(?p) )" +
                            "   }" +
                            "}"
            );
            return query.evaluate(RDFSProperty.class).asSet();
        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Set<RDFSProperty> getIncomingProperties() throws RepositoryException {
        try {
            ObjectQuery query = getObjectConnection().prepareObjectQuery(
                    "SELECT ?p {" +
                            "   { " +
                            "       ?p rdfs:range <" + getResourceAsString() + "> . " +
                            "   }" +
                            "   UNION" +
                            "   { " +
                            "       ?p rdfs:range ?c . " +
                            "       <" + getResourceAsString() + "> rdfs:subClassOf+ ?c . " +
                            "   } " +
                            "   MINUS {" +
                            "       ?p2 rdfs:range <" + getResourceAsString() + "> . " +
                            "       ?p owl:equivalentProperty+ ?p2 . " +
                            "       FILTER( str(?p2) < str(?p) )" +
                            "   }" +
                            "}"
            );
            return query.evaluate(RDFSProperty.class).asSet();
        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Set<RDFSClazz> getSubclazzes() throws RepositoryException {
        try {
            ObjectQuery query = getObjectConnection().prepareObjectQuery(
                    "SELECT ?sub {" +
                            "   ?sub rdfs:subClassOf <" + getResourceAsString() + "> . " +
                            "   FILTER(?super != <"  + getResourceAsString() + ">)" +
                            "}"
            );
            return query.evaluate(RDFSClazz.class).asSet();
        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Set<RDFSClazz> getDirectSuperclazzes() throws RepositoryException {
        ObjectConnection connection = getObjectConnection();
        try {
            ObjectQuery query = connection.prepareObjectQuery(
                    "SELECT ?c {" +
                    "  <" + getResourceAsString() + "> rdfs:subClassOf ?c . " +
                    "  MINUS {" +
                    "     <"+ getResourceAsString() + "> rdfs:subClassOf+ ?c2 . " +
                    "     ?c2 rdfs:subClassOf+ ?c . " +
                    "     FILTER(?c != ?c2 && <" + getResourceAsString() + "> != ?c2)" +
                    "  }" +
                    "}"
            );
            return query.evaluate(RDFSClazz.class).asSet();

        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean hasParent(String resource) throws RepositoryException {
        try {
            BooleanQuery query = getObjectConnection().prepareBooleanQuery(QueryLanguage.SPARQL,
                    "ASK {" +
                            "   <" + getResourceAsString() + "> rdfs:subClassOf+ <" + resource + "> . " +
                            "}"
            );
            return query.evaluate();
        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean hasParent(RDFSClazz clazz) throws RepositoryException {
        return hasParent(clazz.getResourceAsString());
    }

    @Override
    public boolean isLiteral() throws RepositoryException {
        return hasParent(RDFS.LITERAL);
    }

    @Override
    public boolean isDatatype() throws RepositoryException {
        return hasParent(RDFS.DATATYPE);
    }
}
