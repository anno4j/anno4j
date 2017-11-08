package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Support class for {@link Atom}.
 */
@Partial
public abstract class AtomSupport extends ResourceObjectSupport implements Atom {

    @Override
    public Collection<Variable> getVariables() {
        Set<Variable> variables = new HashSet<>();

        // Get this object as resource object, so we can distinguish by type:
        ResourceObject that;
        try {
            that = getObjectConnection().findObject(ResourceObject.class, getResource());
        } catch (RepositoryException | QueryEvaluationException e) {
            throw new RuntimeException(e);
        }

        // Case distinction between different atom types:
        if(that instanceof ClassAtom) {
            Object argument = ((ClassAtom) that).getArgument1();
            if(argument instanceof Variable) {
                variables.add((Variable) argument);
            }

        } else if(that instanceof DatavaluedPropertyAtom) {
            Object argument1 = ((DatavaluedPropertyAtom) that).getArgument1();
            Object argument2 = ((DatavaluedPropertyAtom) that).getArgument2();

            if(argument1 instanceof Variable) {
                variables.add((Variable) argument1);
            }
            if(argument2 instanceof Variable) {
                variables.add((Variable) argument2);
            }

        } else if(that instanceof IndividualPropertyAtom) {
            Object argument1 = ((IndividualPropertyAtom) that).getArgument1();
            Object argument2 = ((IndividualPropertyAtom) that).getArgument2();

            if(argument1 instanceof Variable) {
                variables.add((Variable) argument1);
            }
            if(argument2 instanceof Variable) {
                variables.add((Variable) argument2);
            }

        } else if(that instanceof BuiltinAtom) {
            for (Object argument : ((BuiltinAtom) that).getArguments()) {
                if(argument instanceof Variable) {
                    variables.add((Variable) argument);
                }
            }
        }

        return variables;
    }
}
