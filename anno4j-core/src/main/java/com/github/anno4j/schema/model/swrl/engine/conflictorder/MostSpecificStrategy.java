package com.github.anno4j.schema.model.swrl.engine.conflictorder;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.Rule;
import com.github.anno4j.schema.model.swrl.Variable;

import java.util.*;

/**
 * Behaves like {@link FirstApplicableStrategy} but prefers the most specific rules.
 * A rule {@code r1} is more specific as {@code r2} if {@code r1} has more atoms in its body than {@code r2}.
 * This is based on the assumption that more conditions mean more relevance.
 */
public class MostSpecificStrategy extends FirstApplicableStrategy {

    /**
     * Compares by the number of atoms in the rules body.
     */
    private static class SpecificityComparator implements Comparator<Rule> {

        /**
         * @param rule The rule to check.
         * @return Returns the number of atoms in the rules body.
         */
        private int getBodyLength(Rule rule) {
            if(rule.getBody() == null) {
                return 0;
            } else {
                return rule.getBody().size();
            }
        }

        @Override
        public int compare(Rule rule1, Rule rule2) {
            return getBodyLength(rule1) - getBodyLength(rule2);
        }
    }

    /**
     * Rule base sorted by specificity.
     */
    private List<Rule> ruleBaseBySpecificity;

    @Override
    public Rule fire(List<Rule> ruleBase, Map<Variable, List<Atom>> positiveAssertions, Collection<Rule> negativeAssertions) {
        // If this is the first call, sort rule base by specificity:
        if(ruleBaseBySpecificity == null) {
            ruleBaseBySpecificity = new ArrayList<>(ruleBase);
            Collections.sort(ruleBaseBySpecificity, new SpecificityComparator());
        }

        // Pick by first applicable order on specificity sorted rule base:
        return super.fire(ruleBaseBySpecificity, positiveAssertions, negativeAssertions);
    }

    @Override
    public void feedback(Rule rule, boolean modified) {
        super.feedback(rule, modified);
    }
}
