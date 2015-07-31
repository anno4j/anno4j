package org.openrdf.repository.object.foaf;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.concepts.Person;

/** A person. */
@Iri("http://xmlns.com/foaf/0.1/Person")
public class PersonImpl extends AgentImpl implements Serializable, Person {

	/** http://xmlns.com/foaf/0.1/currentProject */
	private Set<Object> currentProject = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/family_name */
	private Set<Object> family_name = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/firstName */
	private Set<String> firstName = new HashSet<String>();

	/** http://xmlns.com/foaf/0.1/geekcode */
	private Set<Object> geekcode = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/knows */
	private Set<Person> knows = new HashSet<Person>();

	/** http://xmlns.com/foaf/0.1/myersBriggs */
	private Set<Object> myersBriggs = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/pastProject */
	private Set<Object> pastProject = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/plan */
	private Set<Object> plan = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/surname */
	private Set<Object> surname = new HashSet<Object>();

	/** http://xmlns.com/foaf/0.1/topic_interest */
	private Set<Object> topic_interest = new HashSet<Object>();



	/** A current project this person works on. */
	public Set<Object> getFoafCurrentProjects() {
		return currentProject;
	}

	/** A current project this person works on. */
	public void setFoafCurrentProjects(Set<Object> value) {
		this.currentProject = value;
	}


	/** The family_name of some person. */
	public Set<Object> getFoafFamily_names() {
		return family_name;
	}

	/** The family_name of some person. */
	public void setFoafFamily_names(Set<Object> value) {
		this.family_name = value;
	}


	/** The first name of a person. */
	public Set<String> getFoafFirstNames() {
		return firstName;
	}

	/** The first name of a person. */
	public void setFoafFirstNames(Set<String> value) {
		this.firstName = value;
	}


	/** A textual geekcode for this person, see http://www.geekcode.com/geek.html */
	public Set<Object> getFoafGeekcodes() {
		return geekcode;
	}

	/** A textual geekcode for this person, see http://www.geekcode.com/geek.html */
	public void setFoafGeekcodes(Set<Object> value) {
		this.geekcode = value;
	}


	/** A person known by this person (indicating some level of reciprocated interaction between the parties). */
	public Set<Person> getFoafKnows() {
		return knows;
	}

	/** A person known by this person (indicating some level of reciprocated interaction between the parties). */
	public void setFoafKnows(Set<Person> value) {
		this.knows = value;
	}


	/** A Myers Briggs (MBTI) personality classification. */
	public Set<Object> getFoafMyersBriggs() {
		return myersBriggs;
	}

	/** A Myers Briggs (MBTI) personality classification. */
	public void setFoafMyersBriggs(Set<Object> value) {
		this.myersBriggs = value;
	}


	/** A project this person has previously worked on. */
	public Set<Object> getFoafPastProjects() {
		return pastProject;
	}

	/** A project this person has previously worked on. */
	public void setFoafPastProjects(Set<Object> value) {
		this.pastProject = value;
	}


	/** A .plan comment, in the tradition of finger and '.plan' files. */
	public Set<Object> getFoafPlans() {
		return plan;
	}

	/** A .plan comment, in the tradition of finger and '.plan' files. */
	public void setFoafPlans(Set<Object> value) {
		this.plan = value;
	}


	/** The surname of some person. */
	public Set<Object> getFoafSurnames() {
		return surname;
	}

	/** The surname of some person. */
	public void setFoafSurnames(Set<Object> value) {
		this.surname = value;
	}


	/** A thing of interest to this person. */
	public Set<Object> getFoafTopic_interests() {
		return topic_interest;
	}

	/** A thing of interest to this person. */
	public void setFoafTopic_interests(Set<Object> value) {
		this.topic_interest = value;
	}

}
