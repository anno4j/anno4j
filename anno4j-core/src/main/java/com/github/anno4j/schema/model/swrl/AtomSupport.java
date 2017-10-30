package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

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

        // Case distinction between different atom types:
        if(this instanceof ClassAtom) {
            Object argument = ((ClassAtom) this).getArgument1();
            if(argument instanceof Variable) {
                variables.add((Variable) argument);
            }

        } else if(this instanceof DatavaluedPropertyAtom) {
            Object argument1 = ((DatavaluedPropertyAtom) this).getArgument1();
            Object argument2 = ((DatavaluedPropertyAtom) this).getArgument2();

            if(argument1 instanceof Variable) {
                variables.add((Variable) argument1);
            }
            if(argument2 instanceof Variable) {
                variables.add((Variable) argument2);
            }

        } else if(this instanceof IndividualPropertyAtom) {
            Object argument1 = ((IndividualPropertyAtom) this).getArgument1();
            Object argument2 = ((IndividualPropertyAtom) this).getArgument2();

            if(argument1 instanceof Variable) {
                variables.add((Variable) argument1);
            }
            if(argument2 instanceof Variable) {
                variables.add((Variable) argument2);
            }

        } else if(this instanceof BuiltinAtom) {
            for (Object argument : ((BuiltinAtom) this).getArguments()) {
                if(argument instanceof Variable) {
                    variables.add((Variable) argument);
                }
            }
        }

        return variables;
    }
}
