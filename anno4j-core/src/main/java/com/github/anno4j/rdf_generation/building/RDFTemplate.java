package com.github.anno4j.rdf_generation.building;

/**
 * The Template to generate any RDFS File with as many classes and properties as
 * required.
 * 
 * @author Brinninger Sandra
 *
 */
public class RDFTemplate {

	public static String insertHead() {
		return "<?xml version=''1.0''encoding=''utf-8''?>";
	}

	public static String insertRdf() {
		return "<rdf:RDF ";
	}

	public static String insertNamespaceTemp(String abbreviation, String namespace) {
		return "xmlns:" + insertNamespaces(abbreviation, namespace);
	}

	public static String insertNamespaces(String abbreviation, String namespace) {
		return abbreviation + "=''" + namespace + "''";
	}

	public static String insertRdfEndTag() {
		return ">";
	}
	
	public static String insertEndRDF() {
		return "</rdf:RDF>";
	}

	public static String insertClass(String classValue) {
		return "<rdfs:Class rdf:about=''" + classValue + "''>";
	}

	public static String insertEndClass() {
		return "</rdfs:Class>";
	}

	public static String insertSubclass(String uri) {
		return "<rdfs:subClassOf rdfs:resource=''" + uri + "''/>";
	}

	public static String insertProperty(String methodValue) {
		return "<rdf:Property rdf:about=''" + methodValue + "''>";
	}

	public static String insertEndProperty() {
		return "</rdf:Property>";
	}

	public static String insertDomain(String resource) {
		return "<rdf:domain rdf:resource=''" + resource + "''/>";
	}

	public static String insertRange(String resource) {
		return "<rdf:range rdf:resource=''" + resource + "''/>";
	}

}
