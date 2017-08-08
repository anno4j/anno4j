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
     * Identifier names are generated preferably from RDFS labels.
     * If this flag is set the repository will be scanned for possibly conflicting
     * RDFS labels of other resources, i.e. one that may result is the same identifier name.
     * In case of a conflict the identifier builder will try to derive a name from the resources URI.
     * Also see {@link com.github.anno4j.schema_parsing.naming.IdentifierBuilder}.
     */
    private boolean checkRDFSLabelAmbiguity = true;

    /**
     * Identifier names are derived from the resources URI if no RDFS label is applicable.
     * If this flag is set all other resources in the repository are scanned whether their
     * URI could be derived to the same identifier name.
     * In case of such a conflict the identifier will be generated in a unique but less readable
     * way.
     * Also see {@link com.github.anno4j.schema_parsing.naming.IdentifierBuilder}.
     */
    private boolean checkFileOrFragmentAmbiguity = false;

    /**
     * Defines the base package to which the generated package structure should be output to.
     */
    private String basePackage = "";

    /**
     * Whether methods for adding a single value to the value set of an objects property should be generated.
     */
    private boolean doGenerateAdder = true;

    /**
     * Whether methods for adding a multiple values to the value set of an objects property should be generated.
     */
    private boolean doGenerateAdderAll = true;

    /**
     * Whether methods for removing a single value from the value set of an objects property should be generated.
     */
    private boolean doGenerateRemover = true;

    /**
     * Whether methods for removing a multiple values from the value set of an objects property should be generated.
     */
    private boolean doGenerateRemoverAll = true;


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
    public void setIdentifierLanguagePreference(String... preference) {
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
    public void setJavaDocLanguagePreference(String... preference) {
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
    public void setDatatypeMappers(DatatypeMapper... datatypeMappers) {
        this.datatypeMappers = Arrays.asList(datatypeMappers);
    }

    /**
     * Identifier names for a resource are preferably chosen from its RDFS label ({@code rdfs:label}).
     * If the ambiguity check is enabled all other labels will be checked for being possibly in conflict
     * when a identifier name is generated, i.e. would result in the same identifier name.
     * In case of a conflict the identifier is picked from the resources URI (also see
     * {@link #isFileOrFragmentAmbiguityChecked()}) for this.
     * @return Returns true if ambiguity checks will be performed.
     */
    public boolean isRDFSLabelAmbiguityChecked() {
        return checkRDFSLabelAmbiguity;
    }

    /**
     * Identifier names for a resource are preferably chosen from its RDFS label ({@code rdfs:label}).
     * If the ambiguity check is enabled all other labels will be checked for being possibly in conflict
     * when a identifier name is generated, i.e. would result in the same identifier name.
     * In case of a conflict the identifier is picked from the resources URI (also see
     * {@link #isFileOrFragmentAmbiguityChecked()}) for this.
     * @param checkRDFSLabelAmbiguity Whether to perform the above check and fallback mechanism.
     */
    public void setRDFSLabelAmbiguityChecking(boolean checkRDFSLabelAmbiguity) {
        this.checkRDFSLabelAmbiguity = checkRDFSLabelAmbiguity;
    }

    /**
     * Identifier names for a resources are chosen from the suffix of its URI in absence of a RDFS label.
     * If the ambiguity check is enabled all other resources will be checked for their URIs being possibly in
     * conflict with the suffix of the URI of the resource to generate an identifier for.
     * In case of a conflict the identifier is still picked in an unique way, but it may be less readable.
     * Also see {@link com.github.anno4j.schema_parsing.naming.IdentifierBuilder} for this.
     * <br>
     * Example:<br>
     * Consider the class resources {@code http://example.de/ont#Main_Dish} and {@code http://example.de/ont#MainDish}.
     * If the ambiguity check is turned off, the generated classes will both be named {@code MainDish} which
     * will lead to Java compile errors.
     * If the check is enabled the class names will receive an unique number so they can be distinguished.
     *
     * @return Returns whether the above check is turned on.
     */
    public boolean isFileOrFragmentAmbiguityChecked() {
        return checkFileOrFragmentAmbiguity;
    }

    /**
     * Identifier names for a resources are chosen from the suffix of its URI in absence of a RDFS label.
     * If the ambiguity check is enabled all other resources will be checked for their URIs being possibly in
     * conflict with the suffix of the URI of the resource to generate an identifier for.
     * In case of a conflict the identifier is still picked in an unique way, but it may be less readable.
     * Also see {@link com.github.anno4j.schema_parsing.naming.IdentifierBuilder} for this.
     * <br>
     * Example:<br>
     * Consider the class resources {@code http://example.de/ont#Main_Dish} and {@code http://example.de/ont#MainDish}.
     * If the ambiguity check is turned off, the generated classes will both be named {@code MainDish} which
     * will lead to Java compile errors.
     * If the check is enabled the class names will receive an unique number so they can be distinguished.
     *
     * @param checkFileOrFragmentAmbiguity Whether the above check should be turned on.
     */
    public void setFileOrFragmentAmbiguityChecking(boolean checkFileOrFragmentAmbiguity) {
        this.checkFileOrFragmentAmbiguity = checkFileOrFragmentAmbiguity;
    }

    /**
     * Returns the package into which all generated Java files (within their respective packages) are
     * written to.
     * <br><br>
     * Example:<br>
     * If the files should be put in your existing projects package {@code com.yourproject.model}
     * specify the base package that way and {@link com.github.anno4j.schema_parsing.generation.JavaFileGenerator}
     * will write Java files into subpackages (determined by the class URIs) and adopt the package declaration within
     * the source files, e.g. {@code package com.yourproject.model.org.dbpedia}.
     *
     * @return Returns the base package.
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets the package into which all generated Java files (within their respective packages) are
     * written to.
     * <br><br>
     * Example:<br>
     * If the files should be put in your existing projects package {@code com.yourproject.model}
     * specify the base package that way and {@link com.github.anno4j.schema_parsing.generation.JavaFileGenerator}
     * will write Java files into subpackages (determined by the class URIs) and adopt the package declaration within
     * the source files, e.g. {@code package com.yourproject.model.org.dbpedia}.
     *
     * @param basePackage The base package.
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * @return Whether methods for adding a single value to the value set of an objects property should be generated.
     */
    public boolean areAdderMethodsGenerated() {
        return doGenerateAdder;
    }

    /**
     * @param doGenerateAdder Whether methods for adding a single value to the value set of an objects property should be generated.
     */
    public void useAdderMethodGeneration(boolean doGenerateAdder) {
        this.doGenerateAdder = doGenerateAdder;
    }

    /**
     * @return Whether methods for adding a multiple values to the value set of an objects property should be generated.
     */
    public boolean areAdderAllMethodsGenerated() {
        return doGenerateAdderAll;
    }

    /**
     * @param doGenerateAdderAll Whether methods for adding a multiple values to the value set of an objects property should be generated.
     */
    public void useAdderAllMethodGeneration(boolean doGenerateAdderAll) {
        this.doGenerateAdderAll = doGenerateAdderAll;
    }

    /**
     * @return Whether methods for removing a single value from the value set of an objects property should be generated.
     */
    public boolean areRemoverMethodsGenerated() {
        return doGenerateRemover;
    }

    /**
     * @param doGenerateRemover Whether methods for removing a single value from the value set of an objects property should be generated.
     */
    public void useRemoverMethodGeneration(boolean doGenerateRemover) {
        this.doGenerateRemover = doGenerateRemover;
    }

    /**
     * @return Whether methods for removing a multiple values from the value set of an objects property should be generated.
     */
    public boolean areRemoverAllMethodsGenerated() {
        return doGenerateRemoverAll;
    }

    /**
     * @param doGenerateRemoverAll Whether methods for removing a multiple values from the value set of an objects property should be generated.
     */
    public void useRemoverAllMethodGeneration(boolean doGenerateRemoverAll) {
        this.doGenerateRemoverAll = doGenerateRemoverAll;
    }
}
