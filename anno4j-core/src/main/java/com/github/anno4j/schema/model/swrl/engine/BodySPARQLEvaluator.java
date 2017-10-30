package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.swrl.*;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;

import java.util.ArrayList;
import java.util.List;

class BodySPARQLEvaluator extends SPARQLSerializer {

    /**
     * Translates objects to their SPARQL equivalent. This implementation behaves like
     * {@link SPARQLSerializer#toSPARQLSubgraphTerm(Object)} for variables and resources.
     * Literals will be interpreted with their datatype using a SPARQL FILTER and a dummy variable.
     * @param o The object to serialize to SPARQL.
     * @return Returns the SPARQL representation of the given object.
     */
    @Override
    String toSPARQLSubgraphTerm(Object o) {
        // If the object is a literal with datatype, we use FILTER to interpret it:
        if(!(o instanceof Variable) && !(o instanceof ResourceObject) && !(o instanceof CharSequence)) {
            /*
            For all supported literal datatypes we use a special variable and a FILTER.
            This doesn't lead to wrong syntax because literals are only allowed as object of triples.
            This way XQuery interpretation of filter will recognize equivalent values of different types.
            The values must be written without or with quotes for numbers and other other types respectively:
             */
            if(o instanceof Number) {
                return "?literal" + o.hashCode() + " FILTER(?literal" + o.hashCode() + " = " + o.toString() + ")";
            } else {
                return "?literal" + o.hashCode() + " FILTER(?literal" + o.hashCode() + " = \"" + o.toString() + "\")";
            }

        } else {
            return super.toSPARQLSubgraphTerm(o);
        }
    }

    /**
     * Returns the class atom as a SPARQL subgraph pattern (without surrounding {}).
     * For example the atom {@code ex:Person(x)} will be transformed to
     * {@code ?x a ex:Person . }
     * @param clazzAtom The class atom to transform.
     * @return Returns the SPARQL subgraph pattern equivalent or an empty string if the atom
     * does not contain a variable.
     */
    private String getClassAtomAsSubgraphPattern(ClassAtom clazzAtom) {
        if(clazzAtom.getArgument1() instanceof Variable) {
            Variable x = (Variable) clazzAtom.getArgument1();
            ResourceObject clazz = clazzAtom.getClazzPredicate();

            return toSPARQLSubgraphTerm(x) + " a " + toSPARQLSubgraphTerm(clazz) + " . ";
        } else {
            return "";
        }
    }

    /**
     * Returns the individual property atom as a SPARQL subgraph pattern (without surrounding {}).
     * For example the atom {@code ex:marriedTo(?x, ?y)} will be transformed to
     * {@code ?x ex:marriedTo ?y .}.
     * @param propertyAtom The atom to transform.
     * @return Returns the SPARQL equivalent of the atom.
     */
    private String getIndividualPropertyAtomAsSubgraphPattern(IndividualPropertyAtom propertyAtom) {
        ResourceObject property = propertyAtom.getPropertyPredicate();

        // Serialize to triple: "arg1 <property> arg2 ."
        return toSPARQLSubgraphTerm(propertyAtom.getArgument1()) + " "
                + toSPARQLSubgraphTerm(property) + " "
                + toSPARQLSubgraphTerm(propertyAtom.getArgument2()) + " . ";
    }

    /**
     * Returns the data-valued property atom as a SPARQL subgraph pattern (without surrounding {}).
     * For example the atom {@code ex:hasName(?x, ?y)} will be transformed to
     * {@code ?x ex:hasName ?y .}.
     * @param propertyAtom The atom to transform.
     * @return Returns the SPARQL equivalent of the atom.
     */
    private String getDatavaluedPropertyAtomAsSubgraphPattern(DatavaluedPropertyAtom propertyAtom) {
        ResourceObject property = propertyAtom.getPropertyPredicate();

        // Serialize to triple: "arg1 <property> arg2 ."
        return toSPARQLSubgraphTerm(propertyAtom.getArgument1()) + " "
                + toSPARQLSubgraphTerm(property) + " "
                + toSPARQLSubgraphTerm(propertyAtom.getArgument2()) + " . ";
    }

    /**
     * Transforms a SPARQL serializable built-in to its SPARQL FILTER equivalent.
     * The implementation of the built-in is picked by {@link SWRLBuiltInService} and must implement
     * {@link SPARQLSerializable}.
     * @param atom The built-in atom to transform.
     * @return Returns the SPARQL FILTER equivalent of the built-in.
     * @throws SPARQLSerializationException Thrown if the built-in can't be transformed.
     */
    private String getBuiltinAtomAsSubgraphPattern(BuiltinAtom atom) throws SPARQLSerializationException {
        SWRLBuiltInService service = SWRLBuiltInService.getBuiltInService();

        SWRLBuiltin builtin;
        try {
            builtin = service.getBuiltIn(atom);

        } catch (InstantiationException e) {
            throw new SPARQLSerializationException("Can't instantiate built-in!");
        }
        if(builtin instanceof SPARQLSerializable) {
            return " FILTER(" + ((SPARQLSerializable) builtin).asSPARQLFilterExpression() + ") ";
        } else {
            throw new SPARQLSerializationException(atom.getResourceAsString() + " doesn't describe a SPARQL-serializable built-in");
        }
    }

    /**
     * Transformes the given atom to a equivalent SPARQL expression.
     * @param atom The atom to transform.
     * @return Returns the equivalent SPARQL expression.
     * @throws SPARQLSerializationException Thrown if an error occurs while transforming the atom to its
     * SPARQL equivalent expression.
     */
    private String asSubgraphPattern(Atom atom) throws SPARQLSerializationException {
        if(atom instanceof ClassAtom) {
            return getClassAtomAsSubgraphPattern((ClassAtom) atom);
        } else if(atom instanceof IndividualPropertyAtom) {
            return getIndividualPropertyAtomAsSubgraphPattern((IndividualPropertyAtom) atom);
        } else if(atom instanceof DatavaluedPropertyAtom) {
            return getDatavaluedPropertyAtomAsSubgraphPattern((DatavaluedPropertyAtom) atom);
        } else if(atom instanceof BuiltinAtom) {
            return getBuiltinAtomAsSubgraphPattern((BuiltinAtom) atom);
        } else {
            throw new SPARQLSerializationException(atom.getResourceAsString() + " can't be serialized to SPARQL. Unknown type!");
        }
    }

    private String getClassAtomContradiction(ClassAtom clazzAtom) {
        if(clazzAtom.getArgument1() instanceof Variable) {
            Variable x = (Variable) clazzAtom.getArgument1();
            ResourceObject clazz = clazzAtom.getClazzPredicate();

            // A class atom is violated if the variables type is in a disjunctive class:
            return toSPARQLSubgraphTerm(x) + " a ?disjClazz" + clazzAtom.hashCode() + " . \n"
                    + "?disjClazz" + clazzAtom.hashCode() + " owl:disjointWith " + toSPARQLSubgraphTerm(clazz);
        } else {
            return "";
        }
    }

    private String getRoleAtomContradiction(Atom propertyAtom, ResourceObject property, Object argument1, Object argument2) {
        String atomSuffix;
        if (propertyAtom.hashCode() >= 0) {
            atomSuffix = "" + propertyAtom.hashCode();
        } else {
            atomSuffix = "_" + propertyAtom.hashCode();
        }

        StringBuilder builder = new StringBuilder();
        /*
        Argument 1 can't occur as the subject of the atoms property p if there is already
        a explicitly different subject x in relation by p to argument 2 and one of the following holds:
        - p is inverse functional
        - there is an inverse property pinv and its (max)cardinality is 1
        Both can only be checked if arg2 isn't a variable.
        Sketch of the below SPARQL MINUS pattern:

        # There x has arg2 as value of property p:
        ?x <p> <arg2> .

        # arg1 and x are different individuals:
        { <arg1> owl:differentFrom ?x . } UNION { ?x owl:differentFrom <arg1> }

        { # Case 1: p is inverse functional:
            <p> a owl:InverseFunctionalProperty .

        } UNION { # Case 2: p has inverse property pinv with cardinality 1:
            { ?pinv owl:inverseOf <p> . } UNION { <p> owl:inverseOf ?pinv . }

            <arg2> a ?c .
            ?c rdfs:subClassOf ?r .
            ?r a owl:Restriction .
            ?r owl:onProperty ?pinv .
            { ?r owl:maxCardinality 1 . } UNION { ?r owl:cardinality 1 . }
        }
         */
        if(!(argument2 instanceof Variable)) {
            String suffix = atomSuffix + "_1"; // Suffix for unique variable names
            builder.append(
                    "?x" + suffix + " " + toSPARQLSubgraphTerm(property) + " " + toSPARQLSubgraphTerm(argument2) + " .\n" +

                    "{ " + toSPARQLSubgraphTerm(argument1) + " owl:differentFrom ?x" + suffix + " . } " +
                            "UNION { ?x" + suffix + " owl:differentFrom " + toSPARQLSubgraphTerm(argument1) + " }\n" +

                    "{\n" +
                    "    " + toSPARQLSubgraphTerm(property) + " a owl:InverseFunctionalProperty .\n" +
                    "} UNION {\n" +
                    "    { ?pinv" + suffix + " owl:inverseOf " + toSPARQLSubgraphTerm(property) + " . } UNION { "
                            + toSPARQLSubgraphTerm(property) + " owl:inverseOf ?pinv" + suffix + " . }\n" +

                    "   " + toSPARQLSubgraphTerm(argument2) + " a ?c" + suffix + " .\n" +
                    "    ?c" + suffix + " rdfs:subClassOf ?r" + suffix + " .\n" +
                    "    ?r" + suffix + " a owl:Restriction .\n" +
                    "    ?r" + suffix + " owl:onProperty ?pinv" + suffix + " .\n" +
                    "    { ?r" + suffix + " owl:maxCardinality 1 . } UNION { ?r" + suffix + " owl:cardinality 1 . }\n" +
                    "}"
            );
        }
        /*
        Argument 2 can't occur as object of the atoms property p if there is already
        a explicitly different object x as arg1's value of p and one of the following holds:
        - p is functional
        - p has (max)cardinality 1 for any type of arg1
        Both can only be checked of arg1 isn't a variable.

        # ?x is p-value of arg1:
        <arg1> <p> ?x .

        # arg2 and x are different individuals or different literals:
        { <arg2> owl:differentFrom ?x . } UNION { ?x owl:differentFrom <arg2> }
        FILTER((isLiteral(<arg2>) && !isLiteral(?x)) || (!isLiteral(<arg2>) && isLiteral(?x)) || (<arg2> != ?x))

        { # Case 1: p is functional:
            <p> a owl:FunctionalProperty .

        } UNION {
            <arg1> a ?c .
            ?c rdfs:subClassOf ?r .
            ?r a owl:Restriction .
            ?r owl:onProperty <p> .
            { ?r owl:maxCardinality 1 . } UNION { ?r owl:cardinality 1 . }
        }
         */
        if(!(argument1 instanceof Variable)) {
            String suffix = atomSuffix + "_2"; // Suffix for unique variable names
            builder.append(
                    toSPARQLSubgraphTerm(argument1) + " " + toSPARQLSubgraphTerm(property) + " ?x" + suffix + " . \n" +

                    "{ " + toSPARQLSubgraphTerm(argument2) + " owl:differentFrom ?x" + suffix + " . } " +
                            "UNION { ?x" + suffix + " owl:differentFrom " + toSPARQLSubgraphTerm(argument2) + " } \n" +

                    "FILTER((isLiteral(" + toSPARQLSubgraphTerm(argument2) + ") && !isLiteral(?x" + suffix
                            + ")) || (!isLiteral(" + toSPARQLSubgraphTerm(argument2) + ") && isLiteral(?x"
                            + suffix + ")) || (" + toSPARQLSubgraphTerm(argument2) + " != ?x" + suffix + ")) \n" +

                    "   {\n" +
                    "       " + toSPARQLSubgraphTerm(property) + " a owl:FunctionalProperty .\n" +
                    "   } UNION {\n" +
                    "   " + toSPARQLSubgraphTerm(argument1) + " a ?c" + suffix + " .\n" +
                    "   ?c" + suffix + " rdfs:subClassOf ?r"+ suffix + " .\n" +
                    "   ?r" + suffix + " a owl:Restriction .\n" +
                    "   ?r" + suffix + " owl:onProperty " + toSPARQLSubgraphTerm(property) + " .\n" +
                    "   { ?r" + suffix + " owl:maxCardinality 1 . } UNION { ?r" + suffix + " owl:cardinality 1 . }\n" +
                    "}"
            );
        }

        return builder.toString();
    }

    private String getIndividualPropertyAtomContradiction(IndividualPropertyAtom propertyAtom) {
        Object argument1 = propertyAtom.getArgument1();
        Object argument2 = propertyAtom.getArgument2();
        ResourceObject property = propertyAtom.getPropertyPredicate();

        return getRoleAtomContradiction(propertyAtom, property, argument1, argument2);
    }

    private String getDatavaluedPropertyAtomContradiction(DatavaluedPropertyAtom propertyAtom) {
        Object argument1 = propertyAtom.getArgument1();
        Object argument2 = propertyAtom.getArgument2();
        ResourceObject property = propertyAtom.getPropertyPredicate();

        return getRoleAtomContradiction(propertyAtom, property, argument1, argument2);
    }

    private String getAtomContradiction(Atom atom) {
        if (atom instanceof ClassAtom) {
            return getClassAtomContradiction((ClassAtom) atom);
        } else if(atom instanceof IndividualPropertyAtom) {
            return getIndividualPropertyAtomContradiction((IndividualPropertyAtom) atom);
        } else if(atom instanceof DatavaluedPropertyAtom) {
            return getDatavaluedPropertyAtomContradiction((DatavaluedPropertyAtom) atom);
        } else {
            return "";
        }
    }

    /**
     * Selects candidate solutions for the given atom execution plan by selecting a SPARQL serializable prefix, transforming it
     * to a SPARQL query and executing it.
     * Note that the given plan must be in executable order (wrt. dependencies of built-ins) and should by optimized
     * for SPARQL execution e.g. by {@link ExecutionPlanFactory}.
     * @param plan The plan for which to get candidate solutions.
     * @param assertions Atoms that must not be violated (but must not necessarily be true due to open world assumption).
     * @param connection Connection to a triplestore from which to get candidate solutions.
     * @return Returns candidate solutions.
     * @throws SPARQLSerializationException Thrown if an error occurs while transforming to SPARQL.
     * @throws SWRLInferenceEngine.IllegalSWRLRuleException Thrown if there is no SPARQL serializable prefix.
     */
    public SolutionSet findCandidateBindings(AtomList plan, List<Atom> assertions, ObjectConnection connection) throws SPARQLSerializationException, SWRLInferenceEngine.IllegalSWRLRuleException {
        // Get the part of the plan that is SPARQL serializable:
        List<Atom> serializablePrefix = longestSPARQLSerializablePrefix(plan);

        // Get all variables in the prefix in a fixed order:
        List<Variable> variables = new ArrayList<>(plan.getVariables());
        // If there are no variables, there's nothing to do:
        if(variables.isEmpty()) {
            throw new SWRLInferenceEngine.IllegalSWRLRuleException();
        }

        SolutionSet solutions = new SolutionSet();

        // Construct projection: SELECT ?var1 ?var2 ... {
        StringBuilder queryBuilder = new StringBuilder("SELECT");
        for (Variable variable : variables) {
            queryBuilder.append(" ")
                    .append(toSPARQLSubgraphTerm(variable));
        }
        queryBuilder.append("{\n");

        // Fill in subgraph pattern:
        for (Atom atom : serializablePrefix) {
            queryBuilder.append(asSubgraphPattern(atom))
                        .append("\n");
        }

        // Subtract bindings that contradict assertions:
        queryBuilder.append("MINUS {");
        for (Atom assertion : assertions) {
            queryBuilder.append(getAtomContradiction(assertion));
        }
        queryBuilder.append("}");

        queryBuilder.append("}");

        System.out.println(queryBuilder); // TODO Remove

        // Execute query:
        try {

            ObjectQuery query = connection.prepareObjectQuery(queryBuilder.toString());

            for(Object item : query.evaluate().asSet()) {
                // evaluate() returns an array or a single object depending on the number of variables.
                // So in case of a single object wrap it into an array:
                Object[] values;
                if(item instanceof Object[]) {
                    values = (Object[]) item;
                } else {
                    values = new Object[]{item};
                }

                // For each variable-binding find the variable and add to intermediate result:
                Bindings bindings = new Bindings();
                for (int i = 0; i < values.length; i++) {
                    Variable variable = variables.get(i);

                    bindings.bind(variable, values[i]);
                }

                // Add the binding-combination to result:
                solutions.add(bindings);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
            throw new SPARQLSerializationException(e);
        }

        return solutions;
    }
}
