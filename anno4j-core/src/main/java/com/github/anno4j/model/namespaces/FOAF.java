package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Friend of a Friend ontology (foaf:).
 * See <a href="http://xmlns.com/foaf/spec/">http://xmlns.com/foaf/spec/</a>
 */
public class FOAF {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://xmlns.com/foaf/0.1/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "foaf";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_Person
     * The Person class represents people. Something is a Person if it is a person. We don't nitpic about whether they're alive, dead, real, or imaginary. The Person class is a sub-class of the Agent class, since all people are considered 'agents' in FOAF.
     */
    public final static String PERSON = NS + "Person";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_Organization
     * The Organization class represents a kind of Agent corresponding to social instititutions such as companies, societies etc.
     */
    public final static String ORGANIZATION = NS + "Organization";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_name
     * The name of something is a simple textual string.
     */
    public final static String NAME = NS + "name";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_page
     * The page property relates a thing to a document about that thing.
     */
    public final static String PAGE = NS + "page";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is (across time and change) at most one individual that ever has any particular value for foaf:mbox.
     */
    public final static String MBOX = NS + "mbox";

    /**
     * Refers to http://xmlns.com/foaf/0.1/mbox_sha1sum
     */
    public final static String MBOX_SHA1SUM = NS + "mbox_sha1sum";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_openid
     * A openid is a property of a Agent that associates it with a document that can be used as an indirect identifier in the manner of the OpenID "Identity URL".
     */
    public final static String OPEN_ID = NS + "openid";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_homepage
     * The homepage property relates something to a homepage about it.
     */
    public final static String HOMEPAGE = NS + "homepage";

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_nick
     * The nick is short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames
     */
    public final static String NICK = NS + "nick";
}
