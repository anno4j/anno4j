package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.swrl.*;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import org.openrdf.repository.object.LangString;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Some SWRL atoms can be directly translated into SPARQL queries.
 * This class provides functionality to extract the SPARQL serializable part of an execution plan
 * and to translate it it into a SPARQL query.
 * This way candidate bindings for a SWRL rule body can be determined.
 */
class SPARQLSerializer {

    /**
     * Exception signalizing that an error occurred while serializing an
     * execution plan to a SPARQL query.
     */
    public class SPARQLSerializationException extends SWRLException {
        public SPARQLSerializationException() {
        }

        public SPARQLSerializationException(String message) {
            super(message);
        }

        public SPARQLSerializationException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Returns if the given atoms can be translated to SPARQL.
     * This is the case for class/role atoms and some built-ins (those implementing {@link SPARQLSerializable}).
     * The built-in implementation is determined using {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService}.
     * @param atom The atom to check.
     * @return Returns true iff the given atom can be serialized to SPARQL.
     * @throws InstantiationException Thrown if the atom is a built-in and its implementation could not be instantiated.
     */
    boolean isSPARQLSerializable(Object atom) throws InstantiationException {
        // An atom is serializable if its a class/role atom or a built-in that's serializable:
        boolean serializable = atom instanceof ClassAtom
                || atom instanceof IndividualPropertyAtom
                || atom instanceof DatavaluedPropertyAtom;

        if(atom instanceof BuiltinAtom) {
            SWRLBuiltInService service = SWRLBuiltInService.getBuiltInService();
            SWRLBuiltin builtin = service.getBuiltIn((BuiltinAtom) atom);

            serializable |= builtin instanceof SPARQLSerializable;
        }

        return serializable;
    }

    /**
     * Returns the longest prefix of the given plan for which every atom suffices {@link #isSPARQLSerializable(Object)}.
     * @param atoms The execution plan.
     * @return Returns the longest prefix of SPARQL serializable atoms.
     * @throws InstantiationException Thrown if the implementation of any built-in could not be instantiated.
     */
    List<Atom> longestSPARQLSerializablePrefix(List<Atom> atoms) throws InstantiationException {
        List<Atom> prefix = new LinkedList<>();

        boolean isSPARQLSerializable;
        ListIterator<Atom> i = atoms.listIterator();
        do {
            Object item = i.next();

            isSPARQLSerializable = isSPARQLSerializable(item);

            if(isSPARQLSerializable) {
                prefix.add((Atom) item);
            }
        } while (isSPARQLSerializable && i.hasNext()); // Terminate if first non-serializable atom is reached

        return prefix;
    }

    /**
     * Returns the longest prefix of the given plan for which every atom suffices {@link #isSPARQLSerializable(Object)}.
     * @param atoms The execution plan.
     * @return Returns the longest prefix of SPARQL serializable atoms.
     * @throws InstantiationException Thrown if the implementation of any built-in could not be instantiated.
     */
    List<Atom> longestSPARQLSerializablePrefix(AtomList atoms) throws InstantiationException {
        return longestSPARQLSerializablePrefix(atoms.asList());
    }

    /**
     * Translates the given object to a SPARQL term.
     * For variables their {@link Variable#getVariableName()} will be chosen, preceded by a "?".
     * Resources are translated to the SPARQL notation with surrounding "&lt;&gt;".
     * Language tagged strings are translated with their language info and numeric literals without quotes.
     * All other types will be treated as untyped literals using their {@link Object#toString()}.
     * @param o The object to serialize to SPARQL.
     * @return The SPARQL serialization of the object.
     */
    String toSPARQLSubgraphTerm(Object o) {
        if(o instanceof Variable) { // Object is either a variable:
            Variable variable = (Variable) o;
            return "?" + variable.getVariableName();

        } else if(o instanceof ResourceObject) { // or a resource:
            ResourceObject resourceObject = (ResourceObject) o;
            return "<" + resourceObject + ">";

        } else { // or it's a literal:

            // Serialize strings directly in the subgraph pattern:
            if(o instanceof CharSequence) {
                String s = "\"" + o.toString() + "\"";
                if (o instanceof LangString) {
                    // Return literal with language tag (e.g. "hello"@en):
                    return s + "@" + ((LangString) o).getLang();
                } else { // If not a rdf:langString then return as untyped literal:
                    return s;
                }
            } else if(o instanceof Number || o instanceof Boolean) {
                return o.toString();
            } else {
                return "\"" + o.toString() + "\"";
            }
        }
    }
}
