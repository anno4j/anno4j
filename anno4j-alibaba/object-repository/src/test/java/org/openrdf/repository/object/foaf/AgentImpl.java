package org.openrdf.repository.object.foaf;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.concepts.Agent;

/** An agent (eg. person, group, software or physical artifact). */
@Iri("http://xmlns.com/foaf/0.1/Agent")
public class AgentImpl implements Serializable, Agent {

	/** http://xmlns.com/foaf/0.1/aimChatID */
	private Set<Object> aimChatID = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/birthday */
	private Object birthday;

	/** http://xmlns.com/foaf/0.1/fundedBy */
	private Set<Object> fundedBy = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/gender */
	private String gender;

	/** http://xmlns.com/foaf/0.1/icqChatID */
	private Set<Object> icqChatID = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/jabberID */
	private Set<Object> jabberID = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/logo */
	private Set<Object> logo = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/made */
	private Set<Object> made = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/maker */
	private Set<Agent> maker = new HashSet<Agent>();

	/** http://xmlns.com/foaf/0.1/mbox */
	private Set<Object> mbox = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/mbox_sha1sum */
	private Set<Object> mbox_sha1sum = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/msnChatID */
	private Set<Object> msnChatID = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/name */
	private Set<String> name = new HashSet<String>();

	/** http://xmlns.com/foaf/0.1/theme */
	private Set<Object> theme = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/yahooChatID */
	private Set<Object> yahooChatID = new HashSet<Object>();



	/** An AIM chat ID */
	public Set<Object> getFoafAimChatIDs() {
		return aimChatID;
	}

	/** An AIM chat ID */
	public void setFoafAimChatIDs(Set<Object> value) {
		this.aimChatID = value;
	}


	/** The  birthday of this Agent, represented in mm-dd string form, eg. '12-31'. */
	public Object getFoafBirthday() {
		return birthday;
	}

	/** The  birthday of this Agent, represented in mm-dd string form, eg. '12-31'. */
	public void setFoafBirthday(Object value) {
		this.birthday = value;
	}


	/** An organization funding a project or person. */
	public Set<Object> getFoafFundedBy() {
		return fundedBy;
	}

	/** An organization funding a project or person. */
	public void setFoafFundedBy(Set<Object> value) {
		this.fundedBy = value;
	}


	/** The gender of this Agent (typically but not necessarily 'male' or 'female'). */
	public String getFoafGender() {
		return gender;
	}

	/** The gender of this Agent (typically but not necessarily 'male' or 'female'). */
	public void setFoafGender(String value) {
		this.gender = value;
	}


	/** An ICQ chat ID */
	public Set<Object> getFoafIcqChatIDs() {
		return icqChatID;
	}

	/** An ICQ chat ID */
	public void setFoafIcqChatIDs(Set<Object> value) {
		this.icqChatID = value;
	}


	/** A jabber ID for something. */
	public Set<Object> getFoafJabberIDs() {
		return jabberID;
	}

	/** A jabber ID for something. */
	public void setFoafJabberIDs(Set<Object> value) {
		this.jabberID = value;
	}


	/** A logo representing some thing. */
	public Set<Object> getFoafLogos() {
		return logo;
	}

	/** A logo representing some thing. */
	public void setFoafLogos(Set<Object> value) {
		this.logo = value;
	}


	/** Something that was made by this agent. */
	public Set<Object> getFoafMades() {
		return made;
	}

	/** Something that was made by this agent. */
	public void setFoafMades(Set<Object> value) {
		this.made = value;
	}


	/** An agent that made this thing. */
	public Set<Agent> getFoafMakers() {
		return maker;
	}

	/** An agent that made this thing. */
	public void setFoafMakers(Set<Agent> value) {
		this.maker = value;
	}


	/** A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that  there is (across time and change) at most one individual that ever has any particular value for foaf:mbox. */
	public Set<Object> getFoafMboxes() {
		return mbox;
	}

	/** A personal mailbox, ie. an Internet mailbox associated with exactly one owner, the first owner of this mailbox. This is a 'static inverse functional property', in that  there is (across time and change) at most one individual that ever has any particular value for foaf:mbox. */
	public void setFoafMboxes(Set<Object> value) {
		this.mbox = value;
	}


	/** The sha1sum of the URI of an Internet mailbox associated with exactly one owner, the  first owner of the mailbox. */
	public Set<Object> getFoafMbox_sha1sums() {
		return mbox_sha1sum;
	}

	/** The sha1sum of the URI of an Internet mailbox associated with exactly one owner, the  first owner of the mailbox. */
	public void setFoafMbox_sha1sums(Set<Object> value) {
		this.mbox_sha1sum = value;
	}


	/** An MSN chat ID */
	public Set<Object> getFoafMsnChatIDs() {
		return msnChatID;
	}

	/** An MSN chat ID */
	public void setFoafMsnChatIDs(Set<Object> value) {
		this.msnChatID = value;
	}


	/** A name for some thing. */
	public Set<String> getFoafNames() {
		return name;
	}

	/** A name for some thing. */
	public void setFoafNames(Set<String> value) {
		this.name = value;
	}


	/** A theme. */
	public Set<Object> getFoafThemes() {
		return theme;
	}

	/** A theme. */
	public void setFoafThemes(Set<Object> value) {
		this.theme = value;
	}


	/** A Yahoo chat ID */
	public Set<Object> getFoafYahooChatIDs() {
		return yahooChatID;
	}

	/** A Yahoo chat ID */
	public void setFoafYahooChatIDs(Set<Object> value) {
		this.yahooChatID = value;
	}

}
