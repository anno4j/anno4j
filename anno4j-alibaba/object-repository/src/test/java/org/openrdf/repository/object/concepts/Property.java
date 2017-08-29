package org.openrdf.repository.object.concepts;

import java.util.Set;

import org.openrdf.annotations.Iri;

/** The class of RDF properties. */
@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property")
public interface Property {


	/** The subject is an instance of a class. */
	@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
	public abstract Set<ClassConcept> getRdfTypes();

	/** The subject is an instance of a class. */
	public abstract void setRdfTypes(Set<ClassConcept> value);


	/** A domain of the subject property. */
	@Iri("http://www.w3.org/2000/01/rdf-schema#domain")
	public abstract Set<ClassConcept> getRdfsDomains();

	/** A domain of the subject property. */
	public abstract void setRdfsDomains(Set<ClassConcept> value);


	/** A range of the subject property. */
	@Iri("http://www.w3.org/2000/01/rdf-schema#range")
	public abstract Set<ClassConcept> getRdfsRanges();

	/** A range of the subject property. */
	public abstract void setRdfsRanges(Set<ClassConcept> value);


	/** The subject is a subproperty of a property. */
	@Iri("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")
	public abstract Set<Property> getRdfsSubPropertyOf();

	/** The subject is a subproperty of a property. */
	public abstract void setRdfsSubPropertyOf(Set<Property> value);

}
