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

/**
 * Evaluates a portion of a SWRL rules body by issuing a SPARQL-query to a triplestore.
 * Solutions for a SWRL rule body are found in two steps. First candidate solutions are found
 * by an instance of this class and then these bindings are checked whether they satisfy the rest of the body.
 * Instances of this class translate a maximum length prefix of a rules body into a SPARQL-query and executes
 * it in order to retrieve candidate bindings. This requires that all {@link ClassAtom}s, {@link IndividualPropertyAtom}s
 * and {@link DatavaluedPropertyAtom} are in front of any other atom. Also SWRL built-in which have an implementation
 * implementing {@link SPARQLSerializable} (as determined by the {@link SWRLBuiltInService}) can be evaluated by this class.
 * Thus the atom sequence passed to {@link #findCandidateBindings(AtomList, ObjectConnection)} should first be reordered
 * by {@link ExecutionPlanner}.
 */
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
            if(o instanceof Number || o instanceof Boolean) {
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
     * Transforms the given atom to a equivalent SPARQL expression.
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

    /**
     * Selects candidate solutions for the given atom execution plan by selecting a SPARQL serializable prefix, transforming it
     * to a SPARQL query and executing it.
     * Note that the given plan must be in executable order (wrt. dependencies of built-ins) and should by optimized
     * for SPARQL execution e.g. by {@link ExecutionPlanner}.
     * @param plan The plan for which to get candidate solutions.
     * @param connection Connection to a triplestore from which to get candidate solutions.
     * @return Returns candidate solutions.
     * @throws SPARQLSerializationException Thrown if an error occurs while transforming to SPARQL.
     * @throws SWRLInferenceEngine.IllegalSWRLRuleException Thrown if there is no SPARQL serializable prefix.
     * @throws InstantiationException Thrown if the implementation of any built-in could not be instantiated.
     * @throws com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine.UnboundVariableException Thrown if more than
     * one variable doesn't have determined bindings in any atom of {@code atomList}.
     */
    public SolutionSet findCandidateBindings(AtomList plan, ObjectConnection connection) throws SPARQLSerializationException, SWRLInferenceEngine.IllegalSWRLRuleException, InstantiationException, SWRLInferenceEngine.UnboundVariableException {
        if(plan.isEmpty()) {
            throw new SWRLInferenceEngine.IllegalSWRLRuleException("The rules body is empty.");
        }

        // Get the part of the plan that is SPARQL serializable:
        List<Atom> serializablePrefix = longestSPARQLSerializablePrefix(plan);

        // Get all variables in the prefix in a fixed order:
        List<Variable> variables = new ArrayList<>();
        variables.addAll(plan.getVariables());
        // If there are no variables, there's nothing to do:
        if(variables.isEmpty()) {
            throw new SWRLInferenceEngine.IllegalSWRLRuleException("No variables bound through class or role atoms.");
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

        queryBuilder.append("}");

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
                    if(values[i] != null) {
                        Variable variable = variables.get(i);
                        bindings.bind(variable, values[i]);
                    }
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
