package com.github.anno4j.schema.model.swrl.engine.conflictorder;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.Rule;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A conflict resolution strategy is used for determining the order of {@link Rule} application in a
 * {@link SWRLInferenceEngine}.
 * Implementation choose the fire rule that should be fired (s. {@link #fire(List, Map, Collection)}
 * and gets feedback whether the rule modified the state (s. {@link #feedback(Rule, boolean)}.
 */
public interface ConflictResolutionStrategy {

    /**
     * Chooses the fire rule for evaluation. Implementations can return the same rule mutiple times but should
     * signalize the end of a round by returning null once. This way termination of the inference procedure is ensured.
     * @param ruleBase All rules in the rule base without axioms.
     * @param positiveAssertions The positive assertions indexed by their variables, i.e. axioms that always hold.
     *                           The atoms corresponds to the heads of positive axioms after the Lloyd-Topor transformation.
     * @param negativeAssertions The negative assertions, i.e. axioms that never hold.
     * @return Returns the fire rule (from {@code ruleBase}) or null to signalize that a round is finished.
     */
    Rule fire(List<Rule> ruleBase, Map<Variable, List<Atom>> positiveAssertions, Collection<Rule> negativeAssertions);

    /**
     * Callback with information whether a rule application changed the database state.
     * Implementations may use this feedback to optimize the rule order.
     * @param rule The rule executed.
     * @param modified True iff the rule application modified the state.
     */
    void feedback(Rule rule, boolean modified);
}
