package com.github.anno4j.schema.model.swrl.engine.conflictorder;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.Rule;
import com.github.anno4j.schema.model.swrl.Variable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Sequentially fires every rule once per round.
 */
public class SequentialStrategy implements ConflictResolutionStrategy {

    private int lastIndex = -1;

    @Override
    public Rule fire(List<Rule> ruleBase, Map<Variable, List<Atom>> positiveAssertions, Collection<Rule> negativeAssertions) {
        if(lastIndex < ruleBase.size() - 1) {
            lastIndex++;
            return ruleBase.get(lastIndex);
        } else {
            lastIndex = -1; // Reset index
            return null;
        }
    }

    @Override
    public void feedback(Rule rule, boolean modified) {
        // Ignore results of rule evaluation
    }
}
