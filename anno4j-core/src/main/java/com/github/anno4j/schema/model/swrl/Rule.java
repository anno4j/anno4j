package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RULEML;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import java.util.List;

/**
 * A SWRL rule are implications from the conjunction of body atoms to the conjunction of
 * head atoms. Thus if all atoms of the body of a rule hold then also all atoms of its head
 * hold.
 */
@Iri(RULEML.IMP)
public interface Rule extends ResourceObject {

    /**
     * The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     * @return Returns the atoms of the body.
     */
    @Iri(RULEML.BODY)
    AtomList getBody();

    /**
     * Sets the body of the SWRL rule. The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     * @param body The atoms of the body. All contained objects must implement
     * {@link Atom} and {@link ResourceObject}.
     */
    @Iri(RULEML.BODY)
    void setBody(AtomList body);

    /**
     * Sets the body of the SWRL rule. The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     * @param bodyAtoms The atoms of the body.
     */
    void setBody(List<Atom> bodyAtoms) throws RepositoryException;

    /**
     * Sets the body of the SWRL rule. The body of SWRL rule consists of the atoms that must hold
     * in order to infer the head (s. {@link #getHead()} of the rule.
     * The body of the rule is considered a conjunction of all its atoms.
     * @param bodyAtoms The atoms of the body.
     */
    void setBody(Atom... bodyAtoms) throws RepositoryException;

    /**
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     * @return Returns the atoms of the rule head.
     */
    @Iri(RULEML.HEAD)
    AtomList getHead();

    /**
     * Sets the head of the SWRL rule.
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     * @param head The atoms of the rule head.
     */
    @Iri(RULEML.HEAD)
    void setHead(AtomList head);

    /**
     * Sets the head of the SWRL rule.
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     * @param headAtoms The atoms of the rule head.
     */
    void setHead(List<Atom> headAtoms) throws RepositoryException;

    /**
     * Sets the head of the SWRL rule.
     * The head of the SWRL rule consists of the atoms that also hold if all atoms in
     * the body (s. {@link #getBody()} hold.
     * The head of the rule is considered a conjunction of all its atoms.
     * @param headAtoms The atoms of the rule head.
     */
    void setHead(Atom... headAtoms) throws RepositoryException;
}
