package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.schema.model.swrl.*;
import com.github.anno4j.schema.model.swrl.builtin.Computation;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.*;

/**
 * The factory determines an execution plan for the evaluation of an SWRL atom conjunction.
 * Using {@link #reorderAtoms(AtomList, ObjectConnection)} a conjunction of atoms can be reordered, such that the following holds:
 * <ul>
 *     <li>All atoms of type {@link ClassAtom}, {@link DatavaluedPropertyAtom}, {@link IndividualPropertyAtom}
 *     are at the front of the result ordering</li>
 *     <li>{@link BuiltinAtom}s that can be serialized to SPARQL (cf. {@link SPARQLSerializable}) are in front of
 *     all other built-in atoms</li>
 *     <li>If a atom {@code x} has a free variable that can be determined by evaluation of another atom {@code y} then
 *     {@code x} appears after {@code y} in the result ordering.</li>
 * </ul>
 * <br/>
 * An built-in atom can determine a binding for a free variable {@code v} if there is no other variable {@code v'} than {@code v}
 * such that:
 * <ul>
 *     <li>{@code v'} is not bound by a class or role atom</li>
 *     <li>{@code v'} can't be determined by another built-in atom (recursively).</li>
 * </ul>
 */
class ExecutionPlanner {

    /**
     * Represents a node in the dependency forest.
     */
    private static class BuiltinDependencyNode {

        /**
         * The atom represented by this node.
         */
        private BuiltinAtom atom;

        /**
         * List of nodes which this node is dependent on.
         */
        private List<BuiltinDependencyNode> dependencies = new ArrayList<>();

        /**
         * List of nodes that are dependent on this node.
         */
        private List<BuiltinDependencyNode> dependentNodes = new ArrayList<>();

        /**
         * @param atom The atom represented by this node.
         */
        public BuiltinDependencyNode(BuiltinAtom atom) {
            this.atom = atom;
        }

        /**
         * @return The atom represented by this node.
         */
        public BuiltinAtom getAtom() {
            return atom;
        }

        /**
         * Adds a node on which this node is dependent.
         * @param dependency The dependency to add.
         */
        public void addDependency(BuiltinDependencyNode dependency) {
            dependencies.add(dependency);
            dependency.addDependentNode(this);
        }

        /**
         * @return All nodes on which this node is dependent.
         */
        public List<BuiltinDependencyNode> getDependencies() {
            return dependencies;
        }

        /**
         * Adds a node which is dependent on this node.
         * @param node The dependent node to add.
         */
        public void addDependentNode(BuiltinDependencyNode node) {
            dependentNodes.add(node);
        }

        /**
         * @return Returns all nodes that are dependent on this node.
         */
        public List<BuiltinDependencyNode> getDependentNodes() {
            return dependentNodes;
        }

        public void clearDependencies() {
            for(BuiltinDependencyNode dependency : dependencies) {
                dependency.getDependentNodes().remove(this);
            }
            dependencies.clear();
        }
    }

    /**
     * Tests whether the dependency graph containing {@code dependentFreeNode} contains a cycle.
     * @param dependentFreeNode A dependent free node of the graph to test.
     * @param visited Set of all nodes yet visited.
     * @return Returns true iff the graph contains a cycle. Returns false if the graph is cycle-free.
     */
    private boolean containsCycle(BuiltinDependencyNode dependentFreeNode, Set<BuiltinDependencyNode> visited) {
        if(visited.contains(dependentFreeNode)) { // We've visited this node before. So there must be a cycle.
            return true;
        } else {
            visited.add(dependentFreeNode); // Mark this node as visited

            // Do DFS on all child nodes:
            for (BuiltinDependencyNode child : dependentFreeNode.getDependencies()) {
                if(containsCycle(child, new HashSet<>(visited))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tests whether the dependency graph containing {@code dependentFreeNode} contains a cycle.
     * @param dependentFreeNode The dependent free node of the subgraph to test.
     * @return Returns true iff the graph contains a cycle. Returns false if the graph is cycle-free.
     */
    private boolean containsCycle(BuiltinDependencyNode dependentFreeNode) {
        return containsCycle(dependentFreeNode, new HashSet<BuiltinDependencyNode>());
    }

    /**
     * Returns the computable variable of the given atom.
     * A variable of the atom is qualified as being computable if:
     * <ul>
     *     <li>The corresponding built-in function (as assigned by
     *     {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService}) is of type {@link Computation}</li>
     *     <li>All other variables are bound by class/role atoms or are the computable variable of another built-in atom.</li>
     * </ul>
     * @param atom The atom for which the computable variable should be determined.
     * @param atomList All atoms of the conjunction (including class/role atoms).
     * @return Returns the computable variable of {@code atom} or null if all arguments of the atom are determined.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if any variable is neither bound by a class/role atom or as the computable
     * variable of any built-in atom.
     * @throws InstantiationException Thrown if the corresponding {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin} object
     * could not be instantiated.
     * @throws IllegalArgumentException Thrown if the built-in function corresponding to {@code atom} isn't a {@link Computation}.
     */
    private Variable getComputableVariable(BuiltinAtom atom, AtomList atomList) throws SWRLInferenceEngine.UnboundVariableException, InstantiationException {
        if(!(atom.getBuiltin() instanceof Computation)) {
            throw new IllegalArgumentException(atom + " must correspond to a computable built-in function.");
        }

        Collection<Variable> freeVariables = atomList.getFreeVariables();

        // Iterate as long new variables can be determined bound by a built-in computation:
        boolean changed = true;
        while (changed) {
            changed = false;

            for (BuiltinAtom builtinAtom : atomList.getBuiltInAtoms()) {
                if (builtinAtom.getBuiltin() instanceof Computation) {
                    // Get the arguments of the computation that are still considered free:
                    Collection<Variable> variables = new ArrayList<>(builtinAtom.getVariables());
                    variables.retainAll(freeVariables);

                    // If there is only one free variable, it can be bound by the computation:
                    if(variables.size() == 1) {
                        // The variable is bound by this computation. Remove it from free variable set:
                        freeVariables.removeAll(variables);
                        changed = true;
                    }


                    if (builtinAtom.equals(atom)) {
                        // If this atom is the searched one and it has only one free variable. This variable is the one that's computable:
                        if(variables.size() == 1) {
                            return variables.iterator().next();

                        } else if(variables.size() == 0) { // No free variables? No computation (atom is simply a boolean predicate)
                            return null;
                        }
                    }
                }
            }
        }

        // At least one built-in atom has more than one free variable:
        throw new SWRLInferenceEngine.UnboundVariableException("Variables " + freeVariables.toString()
                + " can neither be bound by a class atom, role atom or computable built-in atom.");
    }

    /**
     * Constructs the dependency graph of the built-in atoms.
     * A built-in atom {@code x} depends on another built-in atom {@code y} iff
     * any free variable of {@code x} is bound as a computation result of {@code y}.
     * @param atomList The atoms for which built-ins a dependency graph should be created.
     * @return Returns those nodes of the graph without dependent nodes.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if any variable is neither bound by a class/role atom
     * or as the computable variable of any built-in atom.
     */
    private Collection<BuiltinDependencyNode> constructDependencyGraph(AtomList atomList) throws SWRLInferenceEngine.UnboundVariableException, InstantiationException {
        // Get the variables bound by class or role atoms:
        Collection<Variable> bound = atomList.getBoundVariables();

        // Get the built-in atoms of the conjunction:
        Collection<BuiltinAtom> builtinAtoms = atomList.getBuiltInAtoms();

        // Construct the nodes of the forest:
        Map<BuiltinAtom, BuiltinDependencyNode> dependencyNodesByAtom = new HashMap<>();
        for (BuiltinAtom atom : builtinAtoms) {
            dependencyNodesByAtom.put(atom, new BuiltinDependencyNode(atom));
        }

        // Get the built-ins that bind free variables by a computation:
        Map<Variable, BuiltinDependencyNode> computableVariables = new HashMap<>();
        for (BuiltinAtom atom : builtinAtoms) {
            if(atom.getBuiltin() instanceof Computation) {
                Variable computableVariable = getComputableVariable(atom, atomList);
                if(computableVariable != null) {
                    computableVariables.put(computableVariable, dependencyNodesByAtom.get(atom));
                }
            }
        }

        // Construct the dependency edges between the nodes:
        for (BuiltinAtom atom : builtinAtoms) {
            // Get the variables of the atom that are not bound by class or role atom:
            Collection<Variable> atomFreeVariables = new ArrayList<>(atom.getVariables());
            atomFreeVariables.removeAll(bound);

            BuiltinDependencyNode node = dependencyNodesByAtom.get(atom);

            // For each free variable check if it's computed by another built-in atom.
            // In this case add the computation as dependency:
            for (Variable variable : atomFreeVariables) {
                // Add the node that does compute the variable as dependency. Ignore if it's this node:
                if(computableVariables.containsKey(variable) && !computableVariables.get(variable).equals(node)){
                    node.addDependency(computableVariables.get(variable));

                // Error if the free variable is also not bound by any computation:
                } else if(computableVariables.get(variable) == null || !computableVariables.get(variable).equals(node)) {
                    throw new SWRLInferenceEngine.UnboundVariableException("The variable " + variable.toString()
                            + " is not bound by a class, role atom or computable built-in atom.");
                }
            }
        }

        // Get those node of the graph that are dependency of no other node:
        Collection<BuiltinDependencyNode> dependentFreeNodes = new HashSet<>();
        for (BuiltinDependencyNode node : dependencyNodesByAtom.values()) {
            if(node.getDependentNodes().isEmpty()) {
                dependentFreeNodes.add(node);
            }
        }

        return dependentFreeNodes;
    }

    /**
     * Sorts the dependency forest in topological order.
     * In the returned list no atom {@code x} occurs after another atom {@code y} if {@code x}
     * depends on {@code y}.
     * @param dependentFreeNodes The dependent free nodes of the dependency graph.
     * @return Returns an ordering of the atoms according to the dependency relation.
     */
    private List<BuiltinDependencyNode> topologicalSort(Collection<BuiltinDependencyNode> dependentFreeNodes) {
        /*
         * Implementation of Kahns topological sort algorithm.
         * See: Kahn, Arthur B. (1962), "Topological sorting of large networks", Communications of the ACM, 5 (11): 558–562
         */
        // We must mark edges as visited, so remove them later from this working copy:
        Map<BuiltinDependencyNode, List<BuiltinDependencyNode>> workingDependents = new HashMap<>();

        List<BuiltinDependencyNode> result = new ArrayList<>();
        Set<BuiltinDependencyNode> s = new HashSet<>(dependentFreeNodes);

        while (!s.isEmpty()) {
            BuiltinDependencyNode node = s.iterator().next();
            s.remove(node);

            result.add(node);

            for (BuiltinDependencyNode dependent : node.getDependencies()) {
                // If the dependent set of the node is not cached, copy it:
                if(!workingDependents.containsKey(dependent)) {
                    workingDependents.put(dependent, new ArrayList<>(dependent.getDependentNodes()));
                }

                if(workingDependents.get(dependent).size() == 1) {
                    s.add(dependent);
                }
                workingDependents.get(dependent).remove(node);
            }
        }

        Collections.reverse(result);
        return result;
    }

    /**
     * Optimizes an execution order by pulling in front SPARQL serializable atoms as far as possible.
     * @param plan The plan to optimize. This object will be reordered.
     * @throws InstantiationException Thrown if a built-in implementation couldn't be instantiated.
     */
    private void optimizePlan(List<BuiltinDependencyNode> plan) throws InstantiationException {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 1; i < plan.size(); i++) {
                BuiltinDependencyNode first = plan.get(i - 1);
                BuiltinDependencyNode second = plan.get(i);

                // Only bubble forward SPARQL-serializable over other nodes:
                if (second instanceof SPARQLSerializable && !(first instanceof SPARQLSerializable)) {
                    // But only if the second is not dependent on the first:
                    if(!second.getDependencies().contains(first)) {
                        plan.set(i - 1, second);
                        plan.set(i, first);
                        changed = true;
                    }
                }
            }
        }
    }

    /**
     * Determines an ordering of the atoms such that the following conditions hold:
     * <ul>
     *     <li>All atoms of type {@link ClassAtom}, {@link DatavaluedPropertyAtom}, {@link IndividualPropertyAtom}
     *     are at the front of the result ordering</li>
     *     <li>{@link BuiltinAtom}s that can be serialized to SPARQL (cf. {@link SPARQLSerializable}) are in front of
     *     all other built-in atoms they are not dependent on.</li>
     *     <li>If a atom {@code x} has a free variable that can be determined by evaluation of another atom {@code y} then
     *     {@code x} appears after {@code y} in the result ordering.</li>
     * </ul>
     * @param atomList The atoms to reorder.
     * @return Returns an ordering of the atoms such that the above conditions hold.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if there is a variable that is neither bound by a class/role atom
     * nor by a {@link Computation}.
     * @throws InstantiationException Thrown if an implementation (s. {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin}
     * can't by instantiated for any of the built-in atoms.
     * @throws SWRLInferenceEngine.CyclicDependencyException Thrown if the built-in execution is cyclic dependent.
     */
    public AtomList reorderAtoms(AtomList atomList, ObjectConnection connection) throws RepositoryException, InstantiationException {
        // Construct the dependency graph for built-in atoms:
        Collection<BuiltinDependencyNode> dependentFreeNodes = constructDependencyGraph(atomList);

        // Check each subgraph for cyclic dependencies:
        for (BuiltinDependencyNode dependentFreeNode : dependentFreeNodes) {
            if(containsCycle(dependentFreeNode)) {
                throw new SWRLInferenceEngine.CyclicDependencyException();
            }
        }

        // Determine an order according the dependency relation:
        List<BuiltinDependencyNode> order = topologicalSort(dependentFreeNodes);

        // Pull SPARQL serializable atoms as far in front as possible:
        optimizePlan(order);

        // Put class/role atoms in front of all built-in atoms:
        AtomList plan = connection.createObject(AtomList.class);
        plan.addAll(atomList.getClassAndRoleAtoms());
        for (BuiltinDependencyNode current : order) {
            plan.add(current.getAtom());
        }


        return plan;
    }
}
