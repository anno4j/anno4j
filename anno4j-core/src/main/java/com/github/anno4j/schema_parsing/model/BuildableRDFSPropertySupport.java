package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.RDFSPropertySupport;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectQuery;

import java.util.List;
import java.util.Set;


@Partial
public abstract class BuildableRDFSPropertySupport extends RDFSPropertySupport implements BuildableRDFSProperty {


    @Override
    public Set<RDFSProperty> getSubProperties() throws RepositoryException {
        try {
            ObjectQuery query = getObjectConnection().prepareObjectQuery(
                    "SELECT ?sub {" +
                            "   ?sub rdfs:subPropertyOf <" + getResourceAsString() + "> ." +
                            "}"
            );
            return query.evaluate(RDFSProperty.class).asSet();
        } catch (QueryEvaluationException | MalformedQueryException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Integer getCardinality(RDFSClazz domainClazz) throws RepositoryException {
        try {
            ObjectQuery query = getObjectConnection().prepareObjectQuery(
                    "SELECT ?card {" +
                            "   ?r a owl:Restriction . " +
                            "   <" + domainClazz.getResourceAsString() + "> rdfs:subClassOf+ ?r . " +
                            "   ?r owl:onProperty <" + getResourceAsString() + "> . " +

                            "   {" + // Case 1: Cardinality explicitly set:
                            "       ?r owl:cardinality ?card . " +
                            "   }" +
                            "   UNION " +
                            "   {" + // Case 2: Cardinality implied through same min/max:
                            "       ?r owl:minCardinality ?card . " +
                            "       ?r owl:maxCardinality ?card . " +
                            "   }" +
                            "   MINUS {" + // Ignore qualified cardinalities:
                            "       ?r owl:onClass ?oc . " +
                            "   }" +
                            "}"
            );

            List<Integer> result = query.evaluate(Integer.class).asList();
            if(result != null && !result.isEmpty()) {
                return result.get(0);
            } else {
                return null;
            }

        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

}