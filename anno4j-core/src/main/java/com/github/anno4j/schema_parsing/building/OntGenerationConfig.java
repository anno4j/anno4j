package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.schema_parsing.mapping.DatatypeMapper;
import com.github.anno4j.schema_parsing.validation.ValidatorChain;
import org.openrdf.repository.object.LangString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Describes how Java classes should be built for resources.
 * Allows to set preferences for the language used for JavaDocs and
 * identifier names when generating Java classes from RDF.
 */
public class OntGenerationConfig {

    /**
     * Pseudo language tag representing literals without
     * language information.
     */
    public static final String UNTYPED_LITERAL = null;

    /**
     * The preference order for the language of rdfs:label literals to
     * be used for building identifier names.
     * A value of {@link #UNTYPED_LITERAL} stands for a literal without language information,
     * i.e. a untyped literal.
     */
    private List<String> identifierLangPreference = new ArrayList<>();

    /**
     * The preference order for the language of rdfs:comment literals to
     * be used for building the JavaDoc of resource objects.
     * A value of {@link #UNTYPED_LITERAL} stands for a literal without language information,
     * i.e. a untyped literal.
     */
    private List<String> javadocLangPreference = new ArrayList<>();


    /**
     * The validators used to validate setter and adder arguments.
     */
    private ValidatorChain validators;

    /**
     * Used to map RDF datatypes to Java types.
     */
    private Collection<DatatypeMapper> datatypeMappers = new ArrayList<>();

    /**
     * Initializes the configuration with preference for untyped literals
     * for identifiers and JavaDoc.
     * Uses the default validator chain for RDFS (see {@link ValidatorChain#getRDFSDefault()}).
     */
    public OntGenerationConfig() {
        identifierLangPreference.add(UNTYPED_LITERAL);
        javadocLangPreference.add(UNTYPED_LITERAL);
        validators = ValidatorChain.getRDFSDefault();
    }

    /**
     * Sets the preference for languages when building Identifier names.
     * Literals with a language tag occurring in the given list before another are
     * preferred over the latter for generating a Java identifier.
     * The entries in the preference list must be valid language tags as
     * described in <a href="https://tools.ietf.org/html/bcp47">BCP47</a> or {@link #UNTYPED_LITERAL}.
     * Note that the latter represents preference for
     * an untyped literal, i.e. a literal without language information.
     * @param preference The preference sequence to use for identifier names.
     */
    public void setIdentifierLanguagePreference(List<String> preference) {
        identifierLangPreference.clear();
        identifierLangPreference.addAll(preference);
    }

    /**
     * Sets the preference for languages when building Identifier names.
     * Literals with a language tag occurring in the given list before another are
     * preferred over the latter for generating a Java identifier.
     * The entries in the preference list must be valid language tags as
     * described in <a href="https://tools.ietf.org/html/bcp47">BCP47</a> or {@link #UNTYPED_LITERAL}.
     * Note that the latter represents preference for
     * an untyped literal, i.e. a literal without language information.
     * @param preference The preference sequence to use for identifier names.
     */
    public void setIdentifierLanguagePreference(String[] preference) {
        identifierLangPreference.clear();
        identifierLangPreference.addAll(Arrays.asList(preference));
    }

    /**
     * Sets the preference for languages when generating JavaDoc.
     * Literals with a language tag occurring in the given list before another are
     * preferred over the latter for generating a Java identifier.
     * The entries in the preference list must be valid language tags as
     * described in <a href="https://tools.ietf.org/html/bcp47">BCP47</a> or {@link #UNTYPED_LITERAL}.
     * Note that the latter represents preference for
     * an untyped literal, i.e. a literal without language information.
     * @param preference The preference sequence to use for identifier names.
     */
    public void setJavaDocLanguagePreference(List<String> preference) {
        javadocLangPreference.clear();
        javadocLangPreference.addAll(preference);
    }

    /**
     * Sets the preference for languages when building Identifier names.
     * Literals with a language tag occurring in the given list before another are
     * preferred over the latter for generating a Java identifier.
     * The entries in the preference list must be valid language tags as
     * described in <a href="https://tools.ietf.org/html/bcp47">BCP47</a> or {@link #UNTYPED_LITERAL}.
     * Note that the latter represents preference for
     * an untyped literal, i.e. a literal without language information.
     * @param preference The preference sequence to use for identifier names.
     */
    public void setJavaDocLanguagePreference(String[] preference) {
        javadocLangPreference.clear();
        javadocLangPreference.addAll(Arrays.asList(preference));
    }

    /**
     * @return The validator chain used for checking that arguments of resource object set- and add-methods
     * comply to the value space of their datatype.
     */
    public ValidatorChain getValidators() {
        return validators;
    }

    /**
     * @param validators The validator chain used for checking that arguments of resource object set- and add-methods
     * comply to the value space of their datatype.
     */
    public void setValidators(ValidatorChain validators) {
        this.validators = validators;
    }

    /**
     * Checks whether a language <code>lang</code> is preferred over another language
     * <code>otherLang</code> wrt. a preference list <code>preference</code>.
     * Note that the {@link #UNTYPED_LITERAL} value for the language tags is allowed,
     * representing untyped literals.
     * @param lang The language to decide preference for. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @param otherLang The language to compare to. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @return Returns true if <code>lang</code> is preferred over <code>otherLang</code>
     *          or it is in the preference list and <code>otherLang</code> is not.
     *          Returns false if <code>lang</code> is not in the preference list.
     */
    private boolean isLanguagePreferred(String lang, String otherLang, List<String> preference) {
        int langIndex = preference.indexOf(lang);
        int otherLangIndex = preference.indexOf(otherLang);

        if (langIndex != -1) {
            if (otherLangIndex != -1) {
                // Preference decreases from left to right:
                return langIndex < otherLangIndex;
            } else {
                // lang is in the preference list, but the other is not. So lang is preferred:
                return true;
            }

        } else {
            return false; // The language must be in the preference lists
        }
    }

    /**
     * Checks whether a string - with or without language information (see {@link LangString}) -
     * is preferred over another string wrt. the language preference given.
     * @param s The string to decide preference for.
     * @param other The string to compare to.
     * @return Returns true if the language of <code>s</code> is preferred over the one of
     * <code>other</code> or its language is in the preference list and the
     * one of <code>other</code> is not. Returns false if the language of <code>s</code> is not
     * in the preference list.
     */
    private boolean isStringPreferred(CharSequence s, CharSequence other, List<String> preference) {
        String lang, otherLang;

        if(s instanceof LangString) {
            lang = ((LangString) s).getLang();
        } else if(s == null) {
            return false;
        } else {
            lang = UNTYPED_LITERAL;
        }

        if(other instanceof LangString) {
            otherLang = ((LangString) other).getLang();
        } else if(other == null) {
            return preference.contains(lang);
        } else  {
            otherLang = UNTYPED_LITERAL;
        }

        return isLanguagePreferred(lang, otherLang, preference);
    }

    /**
     * Checks whether a language <code>lang</code> is preferred over another language
     * <code>otherLang</code> for building Java identifier names.
     * Note that the {@link #UNTYPED_LITERAL} value for the language tags is allowed,
     * representing untyped literals.
     * @param lang The language to decide preference for. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @param otherLang The language to compare to. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @return Returns true if <code>lang</code> is preferred over <code>otherLang</code>
     *          or it is in the preference list for identifier names and
     *          <code>otherLang</code> is not. Returns false if <code>lang</code> is
     *          not in the preference list.
     */
    public boolean isIdentifierLanguagePreferred(String lang, String otherLang) {
        return isLanguagePreferred(lang, otherLang, identifierLangPreference);
    }

    /**
     * Checks whether a string - with or without language information (see {@link LangString}) -
     * is preferred over another string by the language preference set for building identifier names.
     * @param s The string to decide preference for.
     * @param other The string to compare to.
     * @return Returns true if the language of <code>s</code> is preferred over the one of
     * <code>other</code> or its language is in the preference list for building identifier names and the
     * one of <code>other</code> is not. Returns false if the language of <code>s</code> is not
     * in the preference list.
     */
    public boolean isPreferredForIdentifiers(CharSequence s, CharSequence other) {
        return isStringPreferred(s, other, identifierLangPreference);
    }

    /**
     * Checks whether a language <code>lang</code> is preferred over another language
     * <code>otherLang</code> for building JavaDocs.
     * Note that the {@link #UNTYPED_LITERAL} value for the language tags is allowed,
     * representing untyped literals.
     * @param lang The language to decide preference for. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @param otherLang The language to compare to. Must be a valid language tag
     *             as defined in <a href="https://tools.ietf.org/html/bcp47">BCP47</a>
     *             or {@link #UNTYPED_LITERAL} for unknown language.
     * @return Returns true if <code>lang</code> is preferred over <code>otherLang</code>
     *          or it is in the preference list for JavaDocs and
     *          <code>otherLang</code> is not. Returns false if <code>lang</code> is
     *          not in the preference list.
     */
    public boolean isJavaDocLanguagePreferred(String lang, String otherLang) {
        return isLanguagePreferred(lang, otherLang, javadocLangPreference);
    }

    /**
     * Checks whether a string - with or without language information (see {@link LangString}) -
     * is preferred over another string by the language preference set for building JavaDoc.
     * @param s The string to decide preference for.
     * @param other The string to compare to.
     * @return Returns true if the language of <code>s</code> is preferred over the one of
     * <code>other</code> or its language is in the preference list for building JavaDocs and the
     * one of <code>other</code> is not. Returns false if the language of <code>s</code> is not
     * in the preference list.
     */
    public boolean isPreferredForJavaDoc(CharSequence s, CharSequence other) {
        return isStringPreferred(s, other, javadocLangPreference);
    }

    /**
     * Returns the datatype mappers that are used to map RDF datatypes
     * (subclasses of <code>rdfs:Datatype</code>) to Java types.
     * @return The mappers to be used during Java file generation.
     */
    public Collection<DatatypeMapper> getDatatypeMappers() {
        return datatypeMappers;
    }

    /**
     * Sets the datatype mappers that are used to map RDF datatypes
     * (subclasses of <code>rdfs:Datatype</code>) to Java types.
     * @param datatypeMappers The mappers to be used during Java file generation.
     */
    public void setDatatypeMappers(Collection<DatatypeMapper> datatypeMappers) {
        this.datatypeMappers = datatypeMappers;
    }

    /**
     * Sets the datatype mappers that are used to map RDF datatypes
     * (subclasses of <code>rdfs:Datatype</code>) to Java types.
     * @param datatypeMappers The mappers to be used during Java file generation.
     */
    public void setDatatypeMappers(DatatypeMapper[] datatypeMappers) {
        this.datatypeMappers = Arrays.asList(datatypeMappers);
    }
}
