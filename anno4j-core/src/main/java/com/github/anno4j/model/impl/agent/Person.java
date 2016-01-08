package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.namespaces.FOAF;
import java.io.ByteArrayOutputStream;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * Conforms to http://xmlns.com/foaf/spec/#term_Person
 *
 * A person.
 */
@Iri(FOAF.PERSON)
public abstract class Person implements Agent {

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @param openID New value of Refers to http:xmlns.comfoafspec#term_openid
     *               openid - An OpenID for an Agent..
     */
    @Iri(FOAF.OPEN_ID)
    public abstract void setOpenID(String openID);

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     *
     * @param mbox New value of Refers to http:xmlns.comfoafspec#term_mbox
     *             personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     */
    @Iri(FOAF.MBOX)
    public abstract void setMbox(String mbox);

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_mbox
     * personal mailbox - A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that there is across time and change at most one individual that ever has any particular value for foaf:mbox..
     */
    @Iri(FOAF.MBOX)
    public abstract String getMbox();

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_openid
     * openid - An OpenID for an Agent..
     */
    @Iri(FOAF.OPEN_ID)
    public abstract String getOpenID();

    /**
     * Sets new Refers to http://xmlns.com/foaf/spec/#term_nick
     * nick - A short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames).
     *
     * @param nick New value of Refers to http://xmlns.com/foaf/spec/#term_nick
     *              nick - A short informal nickname characterizing an agent (includes login identifiers, IRC and other chat nicknames).
     */
    @Iri(FOAF.NICK)
    public abstract void setNick(String nick);

    /**
    * Gets Refers to http://xmlns.com/foaf/spec/#term_nick
    * nick - A nick for the Agent..
    *
    * @return Value of Refers to http://xmlns.com/foaf/spec/#term_nick
    * nick - A nick for the Agent..
    */
    @Iri(FOAF.NICK)
    public abstract String getNick();

    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            RDFWriter writer = Rio.createWriter(format, out);
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

        } catch (RepositoryException | RDFHandlerException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
