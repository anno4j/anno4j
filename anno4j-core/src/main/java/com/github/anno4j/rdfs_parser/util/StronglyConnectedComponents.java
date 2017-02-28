package com.github.anno4j.rdfs_parser.util;

import com.github.anno4j.rdfs_parser.model.RDFSClazz;

import java.util.*;

/**
 * Utility class for finding strongly connected components (SCC) in inheritance trees
 * of {@link RDFSClazz}es.
 * The SCCs of a graph are maximal partitions where every node is reachable from every other node.
 *
 * Uses the algorithm presented by R. Tarjan in
 * <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.327.8418">
 *     Depth first search and linear graph algorithms</a>,
 * which determines the SCCs of an directed graph G = (V, E) in O(|V| + |E|).
 */
public class StronglyConnectedComponents {

    /**
     * Wrapper for {@link RDFSClazz} storing additional information required
     * in the Tarjan algorithm for strongly connected components.
     */
    private static class Node {

        /**
         * The wrapped class.
         */
        private RDFSClazz clazz;

        /**
         * The unique index the algorithm assigned to this class.
         */
        private int index;

        /**
         * The lowest {@link #index} of nodes reachable from this node.
         * This enables to define a class of a SCC as the root
         * if it has the lowest index in this partition of the graph.
         * These roots are used to ensure that each SCC is output only once.
         */
        private int lowLink;

        /**
         * Whether this node is on the stack maintained by the algorithm.
         */
        private boolean isOnStack;

        /**
         * Initializes the wrapper instance with a class and its unique index.
         * @param clazz The clazz to store in this node.
         * @param index The index of the stored class.
         */
        public Node(RDFSClazz clazz, int index) {
            this.clazz = clazz;
            this.index = index;
            this.lowLink = index;
            this.isOnStack = false;
        }

        /**
         * @return The class stored in this node.
         */
        public RDFSClazz getClazz() {
            return clazz;
        }

        /**
         * @param clazz The class stored in this node.
         */
        public void setClazz(RDFSClazz clazz) {
            this.clazz = clazz;
        }

        /**
         * @return The unique index the algorithm assigned to the contained class.
         */
        public int getIndex() {
            return index;
        }

        /**
         * @return The lowest index of nodes reachable from this one.
         */
        public int getLowLink() {
            return lowLink;
        }

        /**
         * @param lowLink The lowest index of nodes reachable from this one.
         */
        public void setLowLink(int lowLink) {
            this.lowLink = lowLink;
        }

        /**
         * @return Whether this node is on the stack maintained by the algorithm.
         */
        public boolean isOnStack() {
            return isOnStack;
        }

        /**
         * @param onStack Whether this node is on the stack maintained by the algorithm.
         */
        public void setOnStack(boolean onStack) {
            isOnStack = onStack;
        }
    }

    /**
     * Utility class for generating unique indices.
     */
    private static class IndexGenerator {
        /**
         * The last index assigned to a class.
         */
        private int lastIndex = 0;

        /**
         * @return A unique index to be assigned to a class.
         */
        private int nextIndex() {
            return lastIndex++;
        }
    }

    /**
     * Finds strongly connected components with more than than one class in it.
     * @param clazz The current class to start DFS from.
     * @param sccs The collection of SCCs already detected. Found SCCs will be inserted into this collection.
     * @param visitedNodes The nodes already visited by the algorithm indexed by the class they wrap.
     * @param stack The stack maintained by the algorithm.
     * @param indexGenerator Generator for creating unique indices for classes.
     */
    private static void strongConnect(RDFSClazz clazz, Collection<Collection<RDFSClazz>> sccs,
                               Map<RDFSClazz, Node> visitedNodes, Stack<Node> stack, IndexGenerator indexGenerator) {
        // Assign an index to the clazz and put it on the stack:
        Node node = new Node(clazz, indexGenerator.nextIndex());
        visitedNodes.put(clazz, node);
        stack.push(node);
        node.setOnStack(true);

        // DFS on the subclasses:
        for (RDFSClazz subClazz : clazz.getSubClazzes()) {
            // Subclass not yet visited?
            if(!visitedNodes.containsKey(subClazz)) {
                // Recursively continue DFS on the subclass:
                strongConnect(subClazz, sccs, visitedNodes, stack, indexGenerator);
                // Check if a node with a lower index is part of the SCC.
                // By definition the lowlink of a node is the lowest index assigned to a node in a SCC it is part of:
                node.setLowLink(Math.min(node.getLowLink(), visitedNodes.get(subClazz).getLowLink()));

            } else if (visitedNodes.get(subClazz).isOnStack()) { // Visited and on stack
                // If the class was previously visited then its index may be lower than the current lowlink:
                node.setLowLink(Math.min(node.getLowLink(), visitedNodes.get(subClazz).getIndex()));
            }
        }

        // Output a SCC if this node is the root of one:
        // The root of a SCC is defined as the node with the lowest index (the lowlink):
        if(node.getIndex() == node.getLowLink()) {
            Collection<RDFSClazz> scc = new HashSet<>();
            Node sccParticipant = null;


            while (!node.equals(sccParticipant)) {
                sccParticipant = stack.pop();
                sccParticipant.setOnStack(false);
                scc.add(sccParticipant.getClazz());
            }

            sccs.add(scc);
        }
    }

    /**
     * Returns all SCCs in the inheritance trees with the given classes as roots.
     * @param seeds The classes to start searching from.
     * @return The strongly connected components found in the subtrees. Each subcollection
     * contains a subgraph which is a SCC.
     */
    public static Collection<Collection<RDFSClazz>> findSCCs(Collection<RDFSClazz> seeds) {
        Collection<Collection<RDFSClazz>> cycles = new HashSet<>();

        for (RDFSClazz clazz : seeds) {
            strongConnect(clazz, cycles, new HashMap<RDFSClazz, Node>(), new Stack<Node>(), new IndexGenerator());
        }

        return cycles;
    }
}
