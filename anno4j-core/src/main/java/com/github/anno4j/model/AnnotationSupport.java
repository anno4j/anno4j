package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;

@Partial
public abstract class AnnotationSupport extends ResourceObjectSupport implements Annotation {

    @Override
    public void addTarget(Target target) {
        if (this.getTarget() == null) {
            this.setTarget(new HashSet<Target>());
        }

        this.getTarget().add(target);
    }

    /**
     * Method returns a textual representation of the given Annotation,
     * containing its Body, Target and possible Selection, in a supported
     * serialisation format.
     *
     * Much faster implementation using SPARQL property paths, instead of
     * calling getTriples per each element of the annotation
     *
     * Can be a rather slow method if we want to print the triples of an
     * annotation that targets another annotation, etc.
     *
     * @param format The format which should be printed.
     * @return A textual representation if this object in the format.
     */
    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // Get the whole graph of a specific annotation
            String query = "CONSTRUCT {\n"
                    + "   <" + this.getResourceAsString() + "> ?prop ?val .\n"
                    + "   ?child ?childProp ?childPropVal . \n"
                    + "   ?someSubj ?incomingChildProp ?child .\n"
                    + "}\n"
                    + "WHERE {\n"
                    + "     <" + this.getResourceAsString() + "> ?prop ?val ; (<>|!<>)+ ?child . \n"
                    + "     ?child ?childProp ?childPropVal.\n"
                    + "     ?someSubj ?incomingChildProp ?child. \n"
                    + "}";
            // Execute the query
            RDFWriter writer = Rio.createWriter(format, out);
            // execute query
            GraphQueryResult results = sparqlGraphQuery(query);
            Rio.write(QueryResults.asModel(results), writer);
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException ex) {
            Logger.getLogger(AnnotationSupport.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Execute a graph query
     *
     * @param query
     * @return
     */
    public GraphQueryResult sparqlGraphQuery(String query) {
        GraphQueryResult result = null;
        try {
            RepositoryConnection conn = this.getObjectConnection();
            // Prepare the query
            GraphQuery q = conn.prepareGraphQuery(QueryLanguage.SPARQL, query);
            try {
                result = q.evaluate();
            } catch (QueryEvaluationException ex) {
                Logger.getLogger(AnnotationSupport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result;
        } catch (RepositoryException | MalformedQueryException ex) {
            Logger.getLogger(AnnotationSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
