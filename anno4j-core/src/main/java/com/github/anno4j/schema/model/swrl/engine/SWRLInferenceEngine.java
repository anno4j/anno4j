package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.AtomList;
import com.github.anno4j.schema.model.swrl.Rule;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.engine.conflictorder.ConflictResolutionStrategy;
import com.github.anno4j.schema.model.swrl.engine.conflictorder.SequentialStrategy;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Inference engine for a subset of the <a href="https://www.w3.org/Submission/SWRL/">Semantic Web Rule Language (SWRL)</a>.<br/>
 * The engine infers new triples by subsequently checking the applicability of rules and applying the head of the rule.
 * The order of rule application within a round is determined by {@link #getConflictResolutionStrategy()}.<br/>
 * <b>Definitions:</b><br/>
 * <i>DL-bound variables</i> A variable is said to be DL-bound in a rule r if it occurs in at least one class
 * or role atom of the body of r. Otherwise it's free.<br/>
 * <i>Computable variable</i> A free variable x occurring in a built-in atom b is said to be computable by b if:
 * <ul>
 *     <li>Every other variable y in b is either DL-bound or is computable by another built-in b'.</li>
 *     <li>Computation for b is supported, i.e. there exists a corresponding class
 *     {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin} that implements
 *     {@link com.github.anno4j.schema.model.swrl.builtin.Computation}.</li>
 * </ul>
 * <i>Dependent built-in</i> A built-in b is dependent on b' if b has a computable variable that is computable by b'
 * and not by b. (i.e. in precedural means b' needs to "compute" first such that the result can be used in b).<br/>
 * <b>Supported subset of SWRL:</b>
 * A SWRL rule r := a ⇒ h is supported if:
 * <ul>
 *     <li>r only uses OWL Lite constructs (classes/roles must be named)</li>
 *     <li>a contains at least one class or role atom</li>
 *     <li>Every built-in atom b in a has at most one variable that is computable by it</li>
 *     <li>a contains no free variables that are not computable</li>
 *     <li>The dependency relation of built-ins in a is cycle free</li>
 *     <li>h consists entirely of class and role atoms</li>
 *     <li>All variables of h are DL-bound or computable in a</li>
 * </ul>
 */
public class SWRLInferenceEngine {

    /**
     * Reorders rule bodies wrt. to built-in dependency relations and maximum SPARQL serializability.
     */
    private ExecutionPlanner executionPlanner = new ExecutionPlanner();

    /**
     * Retrieves candidate solutions by executing a (maximal) subset of rule bodies using a SPARQL query.
     */
    private BodySPARQLEvaluator bodySparqlEvaluator = new BodySPARQLEvaluator();

    /**
     * Evaluates atoms that are not SPARQL serializable.
     */
    private InMemoryEvaluator inMemoryEvaluator = new InMemoryEvaluator();

    /**
     * Inserts the head of a rule into the triplestore replacing variables with their respective bindings.
     */
    private SolutionProcessor headSPARQLEvaluator = new SolutionProcessor();

    /**
     * Thrown if a variable can't be bound.
     */
    public static class UnboundVariableException extends SWRLException {

        public UnboundVariableException() {
        }

        public UnboundVariableException(String message) {
            super(message);
        }
    }

    /**
     * Thrown if SWRL built-ins are cyclic dependent.
     */
    public static class CyclicDependencyException extends SWRLException {

        public CyclicDependencyException() {
        }

        public CyclicDependencyException(String message) {
            super(message);
        }
    }

    /**
     * Thrown if a rule in the rule base isn't supported.
     */
    public static class IllegalSWRLRuleException extends SWRLException {
        public IllegalSWRLRuleException() {
        }

        public IllegalSWRLRuleException(String msg) {
            super(msg);
        }

        public IllegalSWRLRuleException(Throwable t) {
            super(t);
        }
    }

    /**
     * The logger used to log information about the reasoning process.
     */
    private Logger logger = LoggerFactory.getLogger(SWRLInferenceEngine.class);

    /**
     * The rules that will be executed by {@link #executeSWRLRuleBase()}.
     */
    private List<Rule> ruleBase = new ArrayList<>();

    /**
     * Links variables to their true axiomatic atoms, i.e. those atoms that must hold for every
     * body (wrt. that variable).
     */
    private Map<Variable, List<Atom>> assertions = new HashMap<>();

    /**
     * Negative assertions are those rules in the rule base that have an empty consequent and thus
     * their body must never hold.
     */
    private Set<Rule> negativeAssertions = new HashSet<>();

    /**
     * A connection to the triplestore on which will be evaluated.
     */
    private ObjectConnection connection;

    /**
     * Maximum number of executions of the rule base during {@link #executeSWRLRuleBase()}.
     * If this is negative then the rules will be executed until the triplestore isn't changed anymore.
     */
    private int maxExecutionRounds = -1;

    /**
     * Whether to store the reordering of rule bodies in the triplestore.
     * Doing so reduces the time needed for rule execution but reorders the atoms in the rules bodies
     * (in a semantically equivalent way).
     */
    private boolean storeOptimizedOrders = true;

    /**
     * In case {@link #storeOptimizedOrders} is true, this flag indicates whether all rules of the rule base
     * have been optimized by {@link {@link #executionPlanner}}.
     */
    private boolean optimizationDone = false;

    /**
     * Whether the candidate bindings retrieved for the SPARQL-serializable prefixes should be cached each round.
     * This way the number of necessary SPARQL-queries is reduced, but maybe more rounds are needed to reach the same state.
     */
    private boolean useCandidateCache = true;

    /**
     * Contains the candidate bindings determined for SPARQL-serializable prefixes in the current round.
     * This cache must be invalidated after every round and is only used if {@link #useCandidateCache} is set.
     */
    private Map<List<Atom>, SolutionSet> candidateCache = new HashMap<>();

    /**
     * Rule execution order is determined by this strategy.
     */
    private ConflictResolutionStrategy conflictResolutionStrategy = new SequentialStrategy();

    /**
     * Initializes the SWRL inference engine with the given rule base for execution on the connected triplestore.
     * The created engine will store reordered bodies (cf. {@link #setStoreOptimizedOrders(boolean)}) and has no
     * limit on the number of rule base executions (cf. {@link #setMaxExecutionRounds(int)}).
     * Candidate caching is used (cf. {@link #isCandidateCacheUsed()}).
     * @param ruleBase The rules that will be used for inference.
     * @param connection A connection to the triplestore on which rules will be evaluated.
     */
    public SWRLInferenceEngine(Collection<Rule> ruleBase, ObjectConnection connection) {
        this.ruleBase.addAll(ruleBase);
        this.connection = connection;

        indexAxioms();
    }

    /**
     * Initializes the SWRL inference engine using all rules in the Anno4j connected triplestore.
     * The created engine will store reordered bodies (cf. {@link #setStoreOptimizedOrders(boolean)}) and has no
     * limit on the number of rule base executions (cf. {@link #setMaxExecutionRounds(int)}).
     * Candidate caching is used (cf. {@link #isCandidateCacheUsed()}).
     * @param anno4j An Anno4j object connected to a triplestore containing SWRL rules.
     * @throws RepositoryException Thrown if an error occurs while accessing the triplestore.
     */
    public SWRLInferenceEngine(Anno4j anno4j) throws RepositoryException {
        this(anno4j.findAll(Rule.class), anno4j.getObjectRepository().getConnection());
    }

    /**
     * Removes all axioms (rules with an empty body or head) and adds them to {@link #assertions} or
     * {@link #negativeAssertions} respectively.
     */
    private void indexAxioms() {
        Collection<Rule> axioms = new HashSet<>(); // Set of all axioms (positive and negative):
        for(Rule rule : ruleBase) {
            // The head of rules with an empty body are treated as trivially true:
            if(rule.getBody() == null || rule.getBody().isEmpty()) {
                for(Atom atom : rule.getHead().asList()) {
                    for(Variable variable : atom.getVariables()) {
                        // If this is the first time the variable is encountered in an axiomatic atom:
                        if(!assertions.containsKey(variable)) {
                            assertions.put(variable, new ArrayList<Atom>()); // create atom
                        }

                        assertions.get(variable).add(atom);
                    }
                }
                axioms.add(rule);
            }

            // Rules with empty head must never head:
            if(rule.getHead() == null || rule.getHead().isEmpty()) {
                negativeAssertions.add(rule);
                axioms.add(rule);
            }
        }
        ruleBase.removeAll(axioms); // Remove all axioms from rule base
    }

    /**
     * Evaluates the given rules body on the connected triplestore. All bindings fulfilling the body will be returned.
     * This method will reorder the atoms of the body if {@link #storeOptimizedOrders} is set.
     * Uses caching of candidate bindings if {@link #useCandidateCache} is set.
     * @param rule The rule which body will be evaluated.
     * @return Returns a set of binding combinations for the rules variables that represent a solution.
     * @throws RepositoryException Thrown if an error occurs while accessing the triplestore.
     * @throws InstantiationException Thrown if an built-in function implementation can't be instantiated.
     * @throws SWRLException Thrown if an error occurs during evaluation wrt. SWRL semantics.
     */
    private SolutionSet evaluateBody(Rule rule) throws RepositoryException, InstantiationException {
        AtomList plan;

        // If we don't store the optimized orders or optimization was not yet done:
        if(!storeOptimizedOrders || !optimizationDone) {
            // Reorder atoms wrt. built-in dependencies
            // and such that SPARQL serializable atoms are as much in front as possible:
            plan = executionPlanner.reorderAtoms(rule.getBody(), connection);

            // Store the plan:
            if(storeOptimizedOrders) {
                rule.setBody(plan);
            }
        } else {
            plan = rule.getBody();
        }

        logger.debug("Evaluation sequence for SWRL rule " + rule.getResourceAsString() + " is: " + plan);

        SolutionSet candidateBindings;
        // Check if we've cached the results for this subplan already:
        List<Atom> sparqlSerializablePrefix = bodySparqlEvaluator.longestSPARQLSerializablePrefix(plan);
        if(candidateCache.containsKey(sparqlSerializablePrefix)) {
            // Get the candidates from cache:
            candidateBindings = candidateCache.get(sparqlSerializablePrefix);

        } else {
            // Get candidates by SPARQL query:
            candidateBindings = bodySparqlEvaluator.findCandidateBindings(plan, connection);

            // Cache bindings if caching is activated:
            if(useCandidateCache) {
                candidateCache.put(sparqlSerializablePrefix, candidateBindings);
            }
        }

        logger.debug("Candidate bindings after SPARQL-evaluation of prefix " + sparqlSerializablePrefix.size()
                + "/" + plan.size() + ": " + candidateBindings.size());

        // Determine ultimate bindings by evaluating in memory:
        return inMemoryEvaluator.evaluate(plan.asList(), candidateBindings);
    }

    /**
     * Infers new triples by the rules contained in the SWRL inference engines rule base (cf. {@link #getRuleBase()}).
     * The inferred triples are inserted into the connected triplestore (cf. {@link #getConnection()}).
     * To do so the rule base is executed subsequently until no more triples can be inferred. To limit the number of rounds
     * the rule base is executed use {@link #setMaxExecutionRounds(int)}.
     * In order increase performance the rules in the triplestore can be reordered (cf. {@link #setStoreOptimizedOrders(boolean)}).
     * Otherwise the rules are optimized on the fly before each execution.
     * @throws RepositoryException Thrown if an error occurs while accessing the triplestore.
     * @throws InstantiationException Thrown if an built-in function implementation can't be instantiated.
     * @throws SWRLException Thrown if an error occurs during evaluation wrt. SWRL semantics.
     */
    public void executeSWRLRuleBase() throws RepositoryException, InstantiationException {
        int executions = 0;
        boolean modified = true;

        logger.info("Starting SWRL inference (" + ruleBase.size() + " rules, " + assertions.size()
                + " positive assertions, " + negativeAssertions.size() + " negative assertions)");
        logger.info("Max. rounds: " + (maxExecutionRounds >= 0 ? maxExecutionRounds : "Infinite")
                + ", CRS: " + conflictResolutionStrategy.getClass().getName()
                + ", Store optimized orders: " + (storeOptimizedOrders ? "yes" : "no")
                + ", Cache candidates: " + (useCandidateCache ? "yes" : "no"));

        while (modified && executions != maxExecutionRounds) {
            modified = false;

            logger.info("Starting SWRL inference round " + (executions + 1));

            // Get the bindings that must never be true:
            SolutionSet forbiddenBindings = new SolutionSet();
            for (Rule negativeAssertion : negativeAssertions) {
                forbiddenBindings.addAll(evaluateBody(negativeAssertion));
            }

            boolean roundFinished = false;
            while (!roundFinished) {
                // Get the next rule according to the conflict resolution strategy:
                Rule rule = conflictResolutionStrategy.fire(ruleBase, assertions, negativeAssertions);

                if(rule != null) {
                    // Get all bindings that fulfill the body:
                    SolutionSet bindings = evaluateBody(rule);

                    bindings.removeAll(forbiddenBindings); // Remove illegal bindings

                    // Get those atoms that are axiomatic for the bound variables:
                    Collection<Atom> axiomaticAtoms = new HashSet<>();
                    Collection<Variable> boundVariables = bindings.getVariables();
                    for(Variable variable : boundVariables) {
                        if(assertions.containsKey(variable)) {
                            for(Atom atom : assertions.get(variable)) {
                                // There must be no no free variables:
                                if(boundVariables.containsAll(atom.getVariables())) {
                                    axiomaticAtoms.add(atom);
                                }
                            }
                        }
                    }

                    boolean modificationByRule = false;
                    for(Bindings binding : bindings) {
                        modificationByRule |= headSPARQLEvaluator.commitHead(rule.getHead(), binding, axiomaticAtoms, connection);
                    }
                    modified |= modificationByRule;

                } else {
                    roundFinished = true;
                }
            }

            // Set flag that all rules were optimized:
            if(storeOptimizedOrders) {
                optimizationDone = true;
            }

            // Invalidate cache:
            if(useCandidateCache) {
                candidateCache.clear();
            }
            executions++; // Count execution so we can terminate on maxExecutionRounds
        }

        if(executions != maxExecutionRounds) {
            logger.info("Done (State not modified in last round)");
        } else {
            logger.info("Done (Execution limit exceeded)");
        }

    }

    /**
     * @return Returns all rules in this SWRL inference engines rule base.
     */
    public Collection<Rule> getRuleBase() {
        return ruleBase;
    }

    /**
     * @return Returns a connection to the triplestore.
     */
    public ObjectConnection getConnection() {
        return connection;
    }

    /**
     * The inference is done be executing the whole rule base subsequently until no more new triples can be inferred.
     * To limit the number of rule base executions a limit can be set.
     * @return Returns the current limit for rule base executions or a negative value if the number is unlimited.
     */
    public int getMaxExecutionRounds() {
        return maxExecutionRounds;
    }

    /**
     * The inference is done be executing the whole rule base subsequently until no more new triples can be inferred.
     * To limit the number of rule base executions a limit can be set.
     * @param maxExecutionRounds The maximum number of rule base executions or a negative number for no limit.
     */
    public void setMaxExecutionRounds(int maxExecutionRounds) {
        this.maxExecutionRounds = maxExecutionRounds;
    }

    /**
     * The rules bodies are reordered in an optimized way. This semantically equivalent reordering can be stored
     * as the rules body in the triplestore. This way the optimization must not be repeated when executing the rule.
     * @return Returns true if the optimized orders are stored in the triplestore.
     */
    public boolean isStoreOptimizedOrders() {
        return storeOptimizedOrders;
    }

    /**
     * The rules bodies are reordered in an optimized way. This semantically equivalent reordering can be stored
     * as the rules body in the triplestore. This way the optimization must not be repeated when executing the rule.
     * @param storeOptimizedOrders True if the optimized orders should be stored in the triplestore.
     */
    public void setStoreOptimizedOrders(boolean storeOptimizedOrders) {
        this.storeOptimizedOrders = storeOptimizedOrders;
    }

    /**
     * Returns whether candidate solutions are cached. Candidate solutions are retrieved from the triplestore during
     * inference. These candidates can be cached during an inference round such that subsequent rules with the same
     * SPARQL-serializable prefix can use the cached bindings instead of retrieving it again. This way the number
     * of necessary SPARQL-queries can be reduced, but the inference process takes some rounds more in general
     * (because triples inferred by rules are not available for other rules in the same round).
     * @return Returns true if candidate caching is activated.
     */
    public boolean isCandidateCacheUsed() {
        return useCandidateCache;
    }

    /**
     * Turns candidate caching on or off. Candidate solutions are retrieved from the triplestore during
     * inference. These candidates can be cached during an inference round such that subsequent rules with the same
     * SPARQL-serializable prefix can use the cached bindings instead of retrieving them again. This way the number
     * of necessary SPARQL-queries can be reduced, but the inference process takes some rounds more in general
     * (because triples inferred by rules are not available for other rules in the same round).
     * @param useCandidateCache Whether candidate solutions should be cached.
     */
    public void useCandidateCache(boolean useCandidateCache) {
        this.useCandidateCache = useCandidateCache;
    }

    /**
     * The conflict resolution strategy defines in which order rules are executed each round.
     * @return Returns the current conflict resolution strategy.
     */
    public ConflictResolutionStrategy getConflictResolutionStrategy() {
        return conflictResolutionStrategy;
    }

    /**
     * The conflict resolution strategy defines in which order rules are executed each round.
     * @param conflictResolutionStrategy The conflict resolution strategy to use from now on.
     */
    public void setConflictResolutionStrategy(ConflictResolutionStrategy conflictResolutionStrategy) {
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
}
