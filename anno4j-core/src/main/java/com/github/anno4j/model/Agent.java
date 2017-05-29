package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.Anno4jNS;
import com.github.anno4j.model.namespaces.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/prov#Agent
 * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
 */
@Iri(Anno4jNS.AGENT)
public interface Agent extends ResourceObject {

    /**
     * Sets new The name of the agent.
     * Refers to http://xmlns.com/foaf/spec/#term_name. The name of the agent.
     * @param name New value of The name of the agent..
     */
    @Iri(FOAF.NAME)
    void setName(String name);

    /**
     * Gets The name of the agent..
     * Refers to http://xmlns.com/foaf/spec/#term_name. The name of the agent.
     *
     * @return Value of The name of the agent..
     */
    @Iri(FOAF.NAME)
    String getName();

    /**
     * Sets the http://xmlns.com/foaf/0.1/nick property.
     *
     * @param nickname  The value of the http://xmlns.com/foaf/0.1/ property.
     */
    @Iri(FOAF.NICK)
    void setNickname(String nickname);

    /**
     * Gets the http://xmlns.com/foaf/0.1/nick property.
     *
     * @return  The value of the http://xmlns.com/foaf/0.1/nick property.
     */
    @Iri(FOAF.NICK)
    String getNickname();

    /**
     * Sets the http://xmlns.com/foaf/0.1/mbox property.
     *
     * @param email The email to set for the http://xmlns.com/foaf/0.1/mbox property.
     */
    @Iri(FOAF.MBOX)
    void setMbox(String email);

    /**
     * Gets the value of the http://xmlns.com/foaf/0.1/mbox property.
     *
     * @return  The value of the http://xmlns.com/foaf/0.1/mbox property.
     */
    @Iri(FOAF.MBOX)
    String getMbox();

    /**
     * Sets the value for the http://xmlns.com/foaf/0.1/mbox_sha1sum property.
     *
     * @param emailSha1 The value to set for the http://xmlns.com/foaf/0.1/mbox_sha1sum property.
     */
    @Iri(FOAF.MBOX_SHA1SUM)
    void setMboxSha1(String emailSha1);

    /**
     * Gets the value of the http://xmlns.com/foaf/0.1/mbox_sha1sum property.
     *
     * @return The value of the http://xmlns.com/foaf/0.1/mbox_sha1sum property.
     */
    @Iri(FOAF.MBOX_SHA1SUM)
    String getMboxSha1();

    /**
     * Sets the value for the http://xmlns.com/foaf/0.1/homepage property.
     *
     * @param homepage  The value to set for the http://xmlns.com/foaf/0.1/homepage property.
     */
    @Iri(FOAF.HOMEPAGE)
    void setHomepage(String homepage);

    /**
     * Gets the value of the http://xmlns.com/foaf/0.1/homepage property.
     *
     * @return  The value of the http://xmlns.com/foaf/0.1/homepage property.
     */
    @Iri(FOAF.HOMEPAGE)
    String getHomepage();
}
