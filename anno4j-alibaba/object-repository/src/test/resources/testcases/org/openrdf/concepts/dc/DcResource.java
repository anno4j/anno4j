package testcases.org.openrdf.concepts.dc;

import java.lang.Object;
import java.lang.String;
import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.annotations.localized;
import org.openrdf.annotations.rdf;
import org.openrdf.concepts.rdfs.Resource;

@rdf("http://www.w3.org/2000/01/rdf-schema#Resource")
public interface DcResource extends Resource {


	/** An entity responsible for making contributions to the
	 * 		content of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/contributor")
	public abstract Set<Object> getDcContributors();

	/** An entity responsible for making contributions to the
	 * 		content of the resource. */
	public abstract void setDcContributors(Set<Object> value);


	/** The extent or scope of the content of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/coverage")
	public abstract Set<String> getDcCoverages();

	/** The extent or scope of the content of the resource. */
	public abstract void setDcCoverages(Set<String> value);


	/** An entity primarily responsible for making the content 
	 * 		of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/creator")
	public abstract Set<Object> getDcCreators();

	/** An entity primarily responsible for making the content 
	 * 		of the resource. */
	public abstract void setDcCreators(Set<Object> value);


	/** A date associated with an event in the life cycle of the
	 * 		resource. */
	@rdf("http://purl.org/dc/elements/1.1/date")
	public abstract Set<XMLGregorianCalendar> getDcDates();

	/** A date associated with an event in the life cycle of the
	 * 		resource. */
	public abstract void setDcDates(Set<XMLGregorianCalendar> value);


	/** An account of the content of the resource. */
	@localized
	@rdf("http://purl.org/dc/elements/1.1/description")
	public abstract String getDcDescription();

	/** An account of the content of the resource. */
	public abstract void setDcDescription(String value);


	/** The physical or digital manifestation of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/format")
	public abstract Set<String> getDcFormats();

	/** The physical or digital manifestation of the resource. */
	public abstract void setDcFormats(Set<String> value);


	/** An unambiguous reference to the resource within a given context. */
	@rdf("http://purl.org/dc/elements/1.1/identifier")
	public abstract Set<String> getDcIdentifiers();

	/** An unambiguous reference to the resource within a given context. */
	public abstract void setDcIdentifiers(Set<String> value);


	/** A language of the intellectual content of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/language")
	public abstract Set<String> getDcLanguages();

	/** A language of the intellectual content of the resource. */
	public abstract void setDcLanguages(Set<String> value);


	/** An entity responsible for making the resource available */
	@rdf("http://purl.org/dc/elements/1.1/publisher")
	public abstract Set<Object> getDcPublishers();

	/** An entity responsible for making the resource available */
	public abstract void setDcPublishers(Set<Object> value);


	/** A reference to a related resource. */
	@rdf("http://purl.org/dc/elements/1.1/relation")
	public abstract Set<Object> getDcRelations();

	/** A reference to a related resource. */
	public abstract void setDcRelations(Set<Object> value);


	/** Information about rights held in and over the resource. */
	@rdf("http://purl.org/dc/elements/1.1/rights")
	public abstract Set<String> getDcRights();

	/** Information about rights held in and over the resource. */
	public abstract void setDcRights(Set<String> value);


	/** A reference to a resource from which the present resource
	 * 		is derived. */
	@rdf("http://purl.org/dc/elements/1.1/source")
	public abstract Set<Object> getDcSources();

	/** A reference to a resource from which the present resource
	 * 		is derived. */
	public abstract void setDcSources(Set<Object> value);


	/** The topic of the content of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/subject")
	public abstract Set<String> getDcSubjects();

	/** The topic of the content of the resource. */
	public abstract void setDcSubjects(Set<String> value);


	/** A name given to the resource. */
	@localized
	@rdf("http://purl.org/dc/elements/1.1/title")
	public abstract String getDcTitle();

	/** A name given to the resource. */
	public abstract void setDcTitle(String value);


	/** The nature or genre of the content of the resource. */
	@rdf("http://purl.org/dc/elements/1.1/type")
	public abstract Set<Object> getDcTypes();

	/** The nature or genre of the content of the resource. */
	public abstract void setDcTypes(Set<Object> value);

}
