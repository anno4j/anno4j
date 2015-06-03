package com.github.anno4j.querying.evaluation;

import com.github.anno4j.querying.Criteria;
import com.github.anno4j.querying.evaluation.ldpath.LDPathEvaluator;
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

    /**
     * Converts itself to a executable SPARQL query.
     *
     * @return the SPARQL query as string
     */
    public static String evaluate(ArrayList<Criteria> criteria, Map<String, String> prefixes) throws ParseException {

        StringBuilder query = new StringBuilder();

        /**
         * Adding the prefixes to the SPARQL query (format: PREFIX Label: <IRI>)
         */
        for (String key : prefixes.keySet()) {
            query
                    .append("PREFIX ")
                    .append(key)
                    .append(": <")
                    .append(prefixes.get(key))
                    .append("> ")
                    .append(System.getProperty("line.separator"));
        }

        // For readability: Adding an empty line between the prefix and the statement part
        query
                .append(System.getProperty("line.separator"))
                .append("SELECT ?annotation ")
                .append(System.getProperty("line.separator"))
                .append("WHERE {")
                .append(System.getProperty("line.separator"))
                .append("?annotation a oa:Annotation .")
                .append(System.getProperty("line.separator"));

        SesameValueBackend backend = new SesameValueBackend();

        // Creating the actual statements
        for (Criteria c : criteria) {

            query.append("{ ").append(System.getProperty("line.separator"));

            LdPathParser parser = new LdPathParser(backend, new StringReader(c.getLdpath()));

            String variableName = LDPathEvaluator.evaluate(parser.parseSelector(prefixes), query, "annotation");

            if (c.getConstraint() != null) {
                EvalComparison.evaluate(query, c, variableName);
            }

            query.append("}").append(System.getProperty("line.separator"));
        }

        query.append("}");

        logger.info("Created query:\n" + query.toString());

        return query.toString();
    }
}
