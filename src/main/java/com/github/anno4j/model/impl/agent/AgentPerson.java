package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.impl.agent.AgentDefault;
import com.github.anno4j.model.ontologies.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://xmlns.com/foaf/spec/#term_Person
 *
 * A person.
 */
@Iri(FOAF.PERSON)
public class AgentPerson extends AgentDefault {

    @Iri(FOAF.MBOX)    private String mbox;
    @Iri(FOAF.OPEN_ID) private String openID;

    public AgentPerson() {};

    public String getMbox() {
        return mbox;
    }

    public void setMbox(String mbox) {
        this.mbox = mbox;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    @Override
    public String toString() {
        return "AgentPerson{" +
                "mbox='" + mbox + '\'' +
                ", openID='" + openID + '\'' +
                ", resource='" + getResource() +
                '}';
    }
}
