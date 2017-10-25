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
public interface Person extends Agent {

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @param openID New value of Refers to http:xmlns.comfoafspec#term_openid
     *               openid - An OpenID for an Agent..
     */
    @Deprecated
    @Iri(FOAF.OPEN_ID)
    void setOpenID(String openID);

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     */
    @Deprecated
    @Iri(FOAF.OPEN_ID)
    String getOpenID();
}
