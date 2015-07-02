package com.github.anno4j.querying;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.*;
import com.hp.hpl.jena.sparql.algebra.optimize.*;

/**
 *
 */
public class QueryOptimizer {

    private static volatile QueryOptimizer INSTANCE;

    /**
     * private constructor to prevent client from instantiating.
     *
     */
    private QueryOptimizer() {
        //to prevent instantiating by Reflection call 
        if(INSTANCE != null)
            throw new IllegalStateException("Already initialized.");
    }

    public static QueryOptimizer getInstance() {
        //local variable increases performance by 25 percent 
        //Joshua Bloch "Effective Java, Second Edition", p. 283-284
        QueryOptimizer result = INSTANCE;
        if (result == null) {
            synchronized (QueryOptimizer.class) {
                result = INSTANCE;
                if (result == null) {
                    INSTANCE = result = new QueryOptimizer();
                }
            }
        }
        return result;
    }

    /**
     * Optimizes the join order of the query
     *
     * @param sparql The SPARQL query
     *
     * @return The optimized SPARQL query
     */
    public String optimizeJoinOrder(String sparql) {
        Op op = Algebra.compile(QueryFactory.create(sparql));
        Transform joinReorder = new TransformJoinStrategy();
        op = Transformer.transform(joinReorder, op);

        return OpAsQuery.asQuery(op).serialize();
    }

    /**
     * Optimizes the query in multiple ways:
     *
     * <ol>
     *     <li>Redo FILTER (A&&B) as FILTER(A) FILTER(B) (as an expr list)</li>
     *     <li>Optimizes the FILTER disjunctions</li>
     *     <li>Rewrite an algebra expression to put filters as close to their bound variables</li>
     * </ol>
     *
     * @param sparql The SPARQL query
     *
     * @return The optimized SPARQL query
     */
    public String optimizeFilters(String sparql) {
        Op op = Algebra.compile(QueryFactory.create(sparql));

        Transform filterConjunction = new TransformFilterConjunction();
        Transform filterDisjunction = new TransformFilterDisjunction();
        Transform filterPlacement = new TransformFilterPlacement();

        op = Transformer.transform(filterConjunction, op);
        op = Transformer.transform(filterDisjunction, op);
        op = Transformer.transform(filterPlacement, op);

        return OpAsQuery.asQuery(op).serialize();
    }


    /**
     * Reformats the SPARQL query for logging purpose
     *
     * @param sparql The generated SPARQL query
     *
     * @return Formatted query
     */
    public String prettyPrint(String sparql) {
        return OpAsQuery.asQuery(Algebra.compile(QueryFactory.create(sparql))).serialize();
    }
}
