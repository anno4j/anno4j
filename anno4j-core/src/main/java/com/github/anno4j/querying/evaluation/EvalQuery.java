package com.github.anno4j.querying.evaluation;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.Criteria;
import com.github.anno4j.querying.QueryServiceConfiguration;
import com.github.anno4j.querying.evaluation.ldpath.LDPathEvaluator;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.marmotta.ldpath.backend.sesame.SesameValueBackend;
import org.apache.marmotta.ldpath.parser.LdPathParser;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.model.URI;

import java.io.StringReader;
import java.util.Map;

public class EvalQuery {

    public static <T extends ResourceObject> Query evaluate(QueryServiceConfiguration queryServiceDTO, URI rootType) throws ParseException {

        Query query = QueryFactory.make();
        query.setQuerySelectType();

        ElementGroup elementGroup = new ElementGroup();

        Var objectVar = Var.alloc("root");

        // Creating and adding the first triple - could be something like: "?objectVar rdf:type oa:Annotation
        Triple t1 = new Triple(objectVar, RDF.type.asNode(), NodeFactory.createURI(rootType.toString()));
        elementGroup.addTriplePattern(t1);

        // Evaluating the criteria
        for (Criteria c : queryServiceDTO.getCriteria()) {
            SesameValueBackend backend = new SesameValueBackend();

            LdPathParser parser = new LdPathParser(backend, queryServiceDTO.getConfiguration(), new StringReader(c.getLdpath()));
            Var var = LDPathEvaluator.evaluate(parser.parseSelector(queryServiceDTO.getPrefixes()), elementGroup, objectVar, queryServiceDTO.getEvaluatorConfiguration());

            if (c.getConstraint() != null) {
                String resolvedConstraint = resolveConstraintPrefix(c.getConstraint(), queryServiceDTO, parser);
                EvalComparison.evaluate(elementGroup, c, var, resolvedConstraint);
            }
        }

        // Adding all generated patterns to the query object
        query.setQueryPattern(elementGroup);

        // Choose what we want so select - SELECT ?annotation in this case
        query.addResultVar(objectVar);

        // Setting the default prefixes, like rdf: or dc:
        query.getPrefixMapping().setNsPrefixes(queryServiceDTO.getPrefixes());

        return query;
    }

    private static String resolveConstraintPrefix(String constraint, QueryServiceConfiguration queryServiceDTO, LdPathParser parser) throws ParseException {

        for (String namespace : queryServiceDTO.getPrefixes().keySet()) {
            if (constraint.startsWith(namespace + ":")) {
                return parser.resolveNamespace(namespace).toString() + constraint.substring(namespace.length() + 1);
            }
        }

        return constraint;
    }
}