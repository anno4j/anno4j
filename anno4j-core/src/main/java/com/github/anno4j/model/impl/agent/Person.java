package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.namespaces.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://xmlns.com/foaf/spec/#term_Person
 *
 * A person.
 */
@Iri(FOAF.PERSON)
public class Person extends Agent {

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is (across time and change) at most one individual that ever has any particular value for foaf:mbox.
     */
    @Iri(FOAF.MBOX)    private String mbox;

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_openid
     * openid - An OpenID for an Agent.
     */
    @Iri(FOAF.OPEN_ID) private String openID;

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_nick
     * nick - A short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames).
     */
    @Iri(FOAF.NICK) private String nick;

    /**
     * Standard constructor.
     */
    public Person() {};

    @Override
    public String toString() {
        return "Person{" +
                "resource='" + this.getResource() + "'" +
                ", name='" + this.getName() + "'" +
                ", mbox='" + mbox + '\'' +
                ", openID='" + openID + '\'' +
                ", nick='" + nick + '\'' +
                "}";
    }

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @param openID New value of Refers to http:xmlns.comfoafspec#term_openid
     *               openid - An OpenID for an Agent..
     */
    public void setOpenID(String openID) {
        this.openID = openID;
    }

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     *
     * @param mbox New value of Refers to http:xmlns.comfoafspec#term_mbox
     *             personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     */
    public void setMbox(String mbox) {
        this.mbox = mbox;
    }

    /**
     * Sets new Refers to http://xmlns.com/foaf/spec/#term_nick
     * nick - A short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames).
     *
     * @param nick New value of Refers to http://xmlns.com/foaf/spec/#term_nick
     *              nick - A short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames).
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     */
    public String getMbox() {
        return mbox;
    }

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     */
    public String getOpenID() {
        return openID;
    }

    /**
     * Gets Refers to http://xmlns.com/foaf/spec/#term_nick
     * nick - A nick for the Agent..
     *
     * @return Value of Refers to http://xmlns.com/foaf/spec/#term_nick
     * nick - A nick for the Agent..
     */
    public String getNick() {
        return nick;
    }
}
