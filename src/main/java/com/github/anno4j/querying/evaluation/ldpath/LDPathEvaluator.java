package com.github.anno4j.querying.evaluation.ldpath;

import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import org.apache.marmotta.ldpath.api.selectors.NodeSelector;
import org.apache.marmotta.ldpath.model.selectors.*;

public class LDPathEvaluator {

    /**
     * Function to transform LDPath to SPARQL. Recursively splits the LDPath expression into the
     * separate parts and evaluate them. More specifically it creates the SPARQL query portions
     * for each considered part, using the Jena ARQ query engine
     *
     * @param nodeSelector The current NodeSelector of the LDPath
     * @param elementGroup ElementGroup containing the actual query parts
     * @param variable     The latest created variable
     * @return the latest referenced variable
     * @see <a href="https://jena.apache.org/documentation/query/">https://jena.apache.org/documentation/query/</a>
     */
    public static Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var variable) {
        if (nodeSelector instanceof PropertySelector) {
            return EvalPropertySelector.evaluate((PropertySelector) nodeSelector, elementGroup, variable);
        } else if (nodeSelector instanceof PathSelector) {
            return EvalPathSelector.evaluate((PathSelector) nodeSelector, elementGroup, variable);
        } else if (nodeSelector instanceof TestingSelector) {
            return EvalTestingSelector.evaluate((TestingSelector) nodeSelector, elementGroup, variable);
        } else if (nodeSelector instanceof GroupedSelector) {
            return EvalGroupedSelector.evaluate((GroupedSelector) nodeSelector, elementGroup, variable);
        } else if (nodeSelector instanceof UnionSelector) {
            return EvalUnionSelector.evaluate((UnionSelector) nodeSelector, elementGroup, variable);
        } else {
            throw new IllegalStateException(nodeSelector.getClass() + " is not supported.");
        }
    }
}
