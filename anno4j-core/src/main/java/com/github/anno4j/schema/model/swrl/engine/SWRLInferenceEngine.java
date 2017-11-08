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

import java.util.*;


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
     * Whether to store the reorderings of rule bodies in the triplestore.
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
     * limit on the number of rule base executions (cf. {@link #setMaxExecutionRounds(int)}). Candidate caching is used.
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
     * @param anno4j An Anno4j object connected to a triplestore containing SWRL rules.
     * @throws RepositoryException Thrown if an error occurs while accessing the triplestore.
     */
    public SWRLInferenceEngine(Anno4j anno4j) throws RepositoryException {
        this(anno4j.findAll(Rule.class), anno4j.getObjectRepository().getConnection());
    }

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

        List<Atom> assertions = new ArrayList<>();
        for (Variable variable : plan.getVariables()) {
            if(this.assertions.containsKey(variable)) {
                assertions.addAll(this.assertions.get(variable));
            }
        }

        System.out.println("Current execution plan: " + plan); // TODO remove

        SolutionSet candidateBindings;
        // Check if we've cached the resulsts for this subplan already:
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

        System.out.println("Candidates: " + candidateBindings); // TODO remove

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

        while (modified && executions != maxExecutionRounds) {
            modified = false;

            System.out.println("Starting SWRL inference round " + (executions + 1)); // TODO remove

            // Get the bindins that must never be true:
            SolutionSet forbiddenBindings = new SolutionSet();
            for (Rule negativeAssertion : negativeAssertions) {
                forbiddenBindings.addAll(evaluateBody(negativeAssertion));
            }

            boolean roundFinished = false;
            while (!roundFinished) {
                Rule rule = conflictResolutionStrategy.fire(ruleBase, assertions, negativeAssertions);

                if(rule != null) {
                    // Get all bindings that fulfill the body:
                    SolutionSet bindings = evaluateBody(rule);
                    bindings.removeAll(forbiddenBindings); // Remove illegal bindings
                    System.out.println("Solutions: " + bindings); // TODO remove

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
        System.out.println("Done (State not modified in last round)"); // TODO remove
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
    public boolean isUseCandidateCache() {
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
    public void setUseCandidateCache(boolean useCandidateCache) {
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
