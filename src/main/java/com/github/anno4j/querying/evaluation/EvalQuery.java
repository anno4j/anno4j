package com.github.anno4j.querying.evaluation;

import com.github.anno4j.model.ontologies.OADM;
import com.github.anno4j.querying.Criteria;
import com.github.anno4j.querying.evaluation.ldpath.LDPathEvaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.parser.LdPathParser;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

public class EvalQuery {

    private static final Logger logger = LoggerFactory.getLogger(EvalQuery.class);

    public static Query evaluate(ArrayList<Criteria> criteria, Map<String, String> prefixes) throws ParseException {

        Query query = QueryFactory.make();
        query.setQuerySelectType();

        ElementGroup elementGroup = new ElementGroup();

        Var annotationVar = Var.alloc("annotation");

        // Creating and adding the first triple - "?annotation rdf:type oa:Annotation
        Triple t1 = new Triple(annotationVar, RDF.type.asNode(), NodeFactory.createURI(OADM.ANNOTATION));
        elementGroup.addTriplePattern(t1);

        // Evaluating the criteria
        for (Criteria c : criteria) {
            SesameValueBackend backend = new SesameValueBackend();
            LdPathParser parser = new LdPathParser(backend, new StringReader(c.getLdpath()));
            Var var = LDPathEvaluator.evaluate(parser.parseSelector(prefixes), elementGroup, annotationVar);

            if (c.getConstraint() != null) {
                EvalComparison.evaluate(elementGroup, c, var);
            }
        }

        // Adding all generated patterns to the query object
        query.setQueryPattern(elementGroup);

        // Choose what we want so select - SELECT ?annotation in this case
        query.addResultVar(annotationVar);
        // Setting the default prefixes, like rdf: or dc:
        query.getPrefixMapping().setNsPrefixes(prefixes);

        return query;
    }
}