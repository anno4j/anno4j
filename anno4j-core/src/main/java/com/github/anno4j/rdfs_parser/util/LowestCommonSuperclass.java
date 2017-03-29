package com.github.anno4j.rdfs_parser.util;

import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;

import java.util.*;

/**
 * Utility class for finding the lowest common superclass of {@link ExtendedRDFSClazz} objects.
 */
public class LowestCommonSuperclass {

    /**
     * Returns the superclasses of a class including all transitive superclasses and the class
     * itself.
     * @param clazz The class for which to find superclasses.
     * @return The transitive closure of superclasses of the given class including <code>clazz</code>.
     */
    private static Set<ExtendedRDFSClazz> getSuperclassClosure(ExtendedRDFSClazz clazz) {
        Set<ExtendedRDFSClazz> superClazzes = new HashSet<>();
        superClazzes.add(clazz); // Add the class itself

        // Recursively find transitive superclasses for each parent of the current class:
        for (ExtendedRDFSClazz superClazz : clazz.getSuperclazzes()) {
            superClazzes.addAll(getSuperclassClosure(superClazz));
        }

        return superClazzes;
    }

    /**
     * Returns the minimum distance from a class to the root of the inheritance tree.
     * With multiple inheritance multiple paths to the root are possible.
     * In this case the one with a minimum number of intermediate nodes is selected.
     * @param clazz The class for which the distance to the root should be calculated.
     * @return Returns the length of the shortest path to the root.
     */
    private static int minimumDistanceFromRoot(ExtendedRDFSClazz clazz) {
        // The root (rdfs:Class) has no parents.
        // The distance to itself is 0:
        if(clazz.getSuperclazzes().isEmpty()) {
            return 0;
        }

        int parentMinDistance = Integer.MAX_VALUE; // Current minimum distance seen

        // Iterate parents, calculate distances and select minimum one:
        for (ExtendedRDFSClazz parent : clazz.getSuperclazzes()) {
            int distance = minimumDistanceFromRoot(parent);

            if(distance < parentMinDistance) {
                parentMinDistance = distance;
            }
        }

        // The shortest path is the shortest path to a parent of the class and the class itself:
        return parentMinDistance + 1;
    }

    /**
     * Returns a lowest common superclass of the given classes.
     * The lowest common superclass is a class that is a (transitive) superclass of all
     * of the given classes and that has maximum distance to the inheritance trees root.
     * Note that multiple common superclasses may exist with the same distance to the root.
     * In this case any of these candidates is returned.
     * @param clazzes The classes to find a lowest common superclass.
     * @return A lowest common superclass of all given classes.
     */
    public static ExtendedRDFSClazz getLowestCommonSuperclass(Collection<ExtendedRDFSClazz> clazzes) {
        // If only one class is given, immediately return it for performance reasons:
        if(clazzes.size() == 1) {
            return clazzes.iterator().next();
        }

        Iterator<ExtendedRDFSClazz> clazzIter = clazzes.iterator();

        // The set of common superclasses. Initialize with all superclasses of the first class.
        // Includes the first class itself in case it is a superclass of all others:
        Collection<ExtendedRDFSClazz> common = getSuperclassClosure(clazzIter.next());

        // For each other class find superclasses and intersect with intersection of all previous classes:
        while(clazzIter.hasNext()) {
            common.retainAll(getSuperclassClosure(clazzIter.next()));
        }

        // common now contains all common superclasses.
        // Find one with maximum distance to root:
        int maximumDistance = -1;
        ExtendedRDFSClazz lowestCommonSuper = null;
        for (ExtendedRDFSClazz commonSuper : common) {
            int distance = minimumDistanceFromRoot(commonSuper);

            if (distance > maximumDistance) {
                maximumDistance = distance;
                lowestCommonSuper = commonSuper;
            }
        }

        return lowestCommonSuper;
    }
}
