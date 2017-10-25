package org.openrdf.repository.object.concepts;

import java.util.Set;

import org.openrdf.annotations.Iri;

/** The class of classes. */
@Iri("http://www.w3.org/2000/01/rdf-schema#Class")
public interface ClassConcept {


	/** The subject is a subclass of a class. */
	@Iri("http://www.w3.org/2000/01/rdf-schema#subClassOf")
	public abstract Set<ClassConcept> getRdfsSubClassOf();

	/** The subject is a subclass of a class. */
	public abstract void setRdfsSubClassOf(Set<ClassConcept> value);

}
