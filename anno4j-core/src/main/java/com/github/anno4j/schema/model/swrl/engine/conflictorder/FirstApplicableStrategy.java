package com.github.anno4j.schema.model.swrl.engine.conflictorder;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.Rule;
import com.github.anno4j.schema.model.swrl.Variable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The first applicable strategy executes always the first rule. If this one didn't result
 * in a change of the state, it is suspended and the fire one is executed.
 * <b>Warning:</b> Due to the fact that front rules are preferred, it is possible that not all
 * rules are fired.
 */
public class FirstApplicableStrategy implements ConflictResolutionStrategy {

    /**
     * The index of the last suspended rule or -1 if no rules are suspended.
     */
    private int suspendedUntilIndex = -1;

    /**
     * How many rules were fired.
     */
    private int executions = 0;

    @Override
    public Rule fire(List<Rule> ruleBase, Map<Variable, List<Atom>> positiveAssertions, Collection<Rule> negativeAssertions) {
        // If as many rules are fired as in the rule base then the round is finished
        // (This is equivalent to all rules suspended)
        if(executions == ruleBase.size()) {
            return null; // Signalize round end
        }
        executions++;
        return ruleBase.get(suspendedUntilIndex + 1);
    }

    @Override
    public void feedback(Rule rule, boolean modified) {
        if(modified) {
            suspendedUntilIndex = -1;
        } else {
            suspendedUntilIndex++;
        }
    }
}
