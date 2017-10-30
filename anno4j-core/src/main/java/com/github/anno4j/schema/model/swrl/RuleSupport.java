package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.repository.RepositoryException;

import java.util.Arrays;
import java.util.List;

@Partial
public abstract class RuleSupport extends ResourceObjectSupport implements Rule {

    /**
     * Sets the body of the SWRL rule. The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     *
     * @param bodyAtoms The atoms of the body.
     */
    @Override
    public void setBody(List<Atom> bodyAtoms) throws RepositoryException {
        if(getBody() == null) {
            setBody(getObjectConnection().createObject(AtomList.class));
        }
        AtomList body = getBody();
        body.clear();
        body.addAll(bodyAtoms);
    }

    /**
     * Sets the body of the SWRL rule. The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     *
     * @param bodyAtoms The atoms of the body.
     */
    @Override
    public void setBody(Atom... bodyAtoms) throws RepositoryException {
        if(getBody() == null) {
            setBody(getObjectConnection().createObject(AtomList.class));
        }
        AtomList body = getBody();
        body.clear();
        body.addAll(Arrays.asList(bodyAtoms));
    }

    /**
     * Sets the head of the SWRL rule.
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     *
     * @param headAtoms The atoms of the rule head.
     */
    @Override
    public void setHead(List<Atom> headAtoms) throws RepositoryException {
        if(getHead() == null) {
            setHead(getObjectConnection().createObject(AtomList.class));
        }
        AtomList head = getHead();
        head.clear();
        head.addAll(headAtoms);
    }

    /**
     * Sets the head of the SWRL rule.
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     *
     * @param headAtoms The atoms of the rule head.
     */
    @Override
    public void setHead(Atom... headAtoms) throws RepositoryException {
        if(getHead() == null) {
            setHead(getObjectConnection().createObject(AtomList.class));
        }
        AtomList head = getHead();
        head.clear();
        head.addAll(Arrays.asList(headAtoms));
    }
}
