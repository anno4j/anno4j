package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.swrl.*;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.Collection;

/**
 * A solution processor takes a set of solutions and a SWRL rule head.
 * It inserts the head into a triplestore subsequently replacing the variables
 * with all their solution bindings.
 * For this it is necessary that the given rule head consists solely of class and role atoms
 * and that all variables are bound by the rules body.
 */
class SolutionProcessor extends SPARQLSerializer {

    /**
     * Translates the given object to its SPARQL equivalent (s. {@link SPARQLSerializer#toSPARQLSubgraphTerm(Object)})
     * replacing variables by their bound value.
     * @param o The object to transform.
     * @param bindings Bindings for variables.
     * @return Returns the object translated to SPARQL.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if {@code o} is a variable, for which no binding exists in
     * {@code bindings}.
     */
    private String toSPARQLSubgraphTerm(Object o, Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        if(o instanceof Variable) {
            if(bindings.bound((Variable) o)) {
                return toSPARQLSubgraphTerm(bindings.get((Variable) o));
            } else {
                throw new SWRLInferenceEngine.UnboundVariableException("Variable " + ((Variable) o).getResourceAsString()
                        + " is not bound but used in head!");
            }
        } else {
            return toSPARQLSubgraphTerm(o);
        }
    }

    /**
     * Transforms a class atom into a SPARQL subgraph pattern replacing all variables by their respective binding.
     * For example the atom {@code ex:Person(?x)} with binding {@code ?x := ex:Mike} will be transformed to
     * {@code ex:Mike a ex:Person . }.
     * @param clazzAtom The class atom to transform.
     * @param bindings Bindings for variables.
     * @return Returns the SPARQL equivalent of the given class atom.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if the atom contains a variable which is not bound in {@code bindings}.
     */
    private String getClassAtomAsSubgraphPattern(ClassAtom clazzAtom, Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        if(clazzAtom.getArgument1() instanceof Variable) {
            return toSPARQLSubgraphTerm(clazzAtom.getArgument1(), bindings)
                    + " a "
                    + toSPARQLSubgraphTerm(clazzAtom.getClazzPredicate())
                    + " . ";

        } else {
            return "";
        }
    }

    /**
     * Transforms a individual property atom into a SPARQL subgraph pattern replacing all variables by their respective bindings.
     * For example the atom {@code ex:marriedTo(?x, ?y)} with bindings {@code ?x := ex:Mike, ?y := ex:Julia} will be transformed to
     * {@code ex:Mike ex:marriedTo ex:Julia . }
     * @param propertyAtom The atom to transform.
     * @param bindings Bindings for variables.
     * @return Returns the SPARQL equivalent of the given property atom.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if the atom contains a variable which is not bound in {@code bindings}.
     */
    private String getIndividualPropertyAtomAsSubgraphPattern(IndividualPropertyAtom propertyAtom, Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        ResourceObject property = propertyAtom.getPropertyPredicate();

        // Serialize to triple: "arg1 <property> arg2 ."
        return toSPARQLSubgraphTerm(propertyAtom.getArgument1(), bindings) + " "
                + toSPARQLSubgraphTerm(property, bindings) + " "
                + toSPARQLSubgraphTerm(propertyAtom.getArgument2(), bindings) + " . ";
    }

    /**
     * Transforms a data-valued property atom into a SPARQL subgraph pattern replacing all variables by their respective bindings.
     * For example the atom {@code ex:hasName(?x, ?y)} with bindings {@code ?x := ex:Mike, ?y := "Mike"} will be transformed to
     * {@code ex:Mike ex:hasName "Mike" . }
     * @param propertyAtom The atom to transform.
     * @param bindings Bindings for variables.
     * @return Returns the SPARQL equivalent of the given property atom.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if the atom contains a variable which is not bound in {@code bindings}.
     */
    private String getDatavaluedPropertyAtomAsSubgraphPattern(DatavaluedPropertyAtom propertyAtom, Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        ResourceObject property = propertyAtom.getPropertyPredicate();

        // Serialize to triple: "arg1 <property> arg2 ."
        return toSPARQLSubgraphTerm(propertyAtom.getArgument1(), bindings) + " "
                + toSPARQLSubgraphTerm(property, bindings) + " "
                + toSPARQLSubgraphTerm(propertyAtom.getArgument2(), bindings) + " . ";
    }

    /**
     * Transformes the given atom to a equivalent SPARQL expression replacing all variables by their respective bindings.
     * @param atom The atom to transform.
     * @param bindings Bindings for variables.
     * @return Returns the equivalent SPARQL expression with all variables replaced.
     * @throws SPARQLSerializationException Thrown if an error occurs while transforming the atom to its
     * SPARQL equivalent expression.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if the atom contains a variable which is not bound in {@code bindings}.
     */
    private String asSubgraphPattern(Atom atom, Bindings bindings) throws SPARQLSerializationException, SWRLInferenceEngine.UnboundVariableException {
        if(atom instanceof ClassAtom) {
            return getClassAtomAsSubgraphPattern((ClassAtom) atom, bindings);
        } else if(atom instanceof IndividualPropertyAtom) {
            return getIndividualPropertyAtomAsSubgraphPattern((IndividualPropertyAtom) atom, bindings);
        } else if(atom instanceof DatavaluedPropertyAtom) {
            return getDatavaluedPropertyAtomAsSubgraphPattern((DatavaluedPropertyAtom) atom, bindings);
        } else {
            throw new SPARQLSerializationException(atom.getResourceAsString() + " can't be serialized to SPARQL. Unknown type!");
        }
    }

    /**
     * Transforms the given head into a SPARQL update by replacing all variables by their respective bindings and
     * inserts the resulting triples into the triplestore.
     * @param head The head of the rule which variables will be replaced.
     * @param bindings Bindings for variables.
     * @param assertions Atoms that are also true due to axiomatic rules. All variables must be bound by {@code bindings}.
     * @param connection A connection to the triplestore in which the resulting triples should be inserted.
     * @return Returns true if the triplestore was changed as result to the update.
     * @throws SPARQLSerializer.SPARQLSerializationException Thrown if an error occurs while transforming to SPARQL.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if a variable occurs in the {@code head} that is not bound by {@code bindings}.
     * @throws SWRLException Thrown if any error occurs processing the solution.
     * @throws InstantiationException Thrown if the implementation of any built-in could not be instantiated.
     */
    boolean commitHead(AtomList head, Bindings bindings, Collection<Atom> assertions, ObjectConnection connection) throws SWRLException, InstantiationException {
        // Validate that the head is fully SPARQL serializable:
        for (Object atom : head) {
            if(!isSPARQLSerializable(atom)) {
                throw new SPARQLSerializationException(atom.toString() + " is not SPARQL serializable");
            }
        }

        // Create the subpattern group for the data to insert:
        StringBuilder dataBuilder = new StringBuilder("{\n");
        for (Object atom : head) {
            if(atom instanceof Atom) {
                dataBuilder.append(asSubgraphPattern((Atom) atom, bindings))
                        .append("\n");
            } else {
                throw new SWRLException("Atom lists must contain only atoms.");
            }
        }
        for (Atom assertion : assertions) {
            dataBuilder.append(asSubgraphPattern(assertion, bindings));
        }
        dataBuilder.append("}");

        System.out.println("ASK " + dataBuilder); // TODO Remove

        try {
            // Check if the data is already present in the triplestore:
            BooleanQuery askQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, "ASK " + dataBuilder);
            if (!askQuery.evaluate()) {
                System.out.println("INSERT DATA " + dataBuilder); // TODO Remove

                // If the data is not there then insert it:
                Update update = connection.prepareUpdate("INSERT DATA " + dataBuilder.toString());
                update.execute();
                return true;

            } else {
                return false;
            }

        } catch (RepositoryException | MalformedQueryException | UpdateExecutionException | QueryEvaluationException e) {
            throw new SWRLException(e);
        }
    }
}
