package com.github.anno4j.rdf_generation.building;


public class RDFTemplate { //FINAL??

	public static String insertHead() {
		return "<?xml version=''1.0''encoding=''utf-8''?>";
	}

	public static String insertRdf() {
		return "<rdf:RDF ";
	}

	public static String insertNamespaceTemp(int numberNS, String abbreviation, String namespace) {
		return "xmlns:" + insertNamespaces(numberNS, abbreviation, namespace) + ">";
	}
	
	public static String insertNamespaces(int numberNS, String abbreviation, String namespace) {
		return abbreviation + "=''" + namespace + "''";
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

	public static String insertSubclass(String ns, String subclassfragment) {
		return "<rdfs:subClassOf rdfs:resource=''" + ns + subclassfragment + "''/>";
	}

	public static String insertProperty(String methodValue) {
		return "<rdf:Property rdf:about=''" + methodValue + "''>";
	}

	public static String insertEndProperty() {
		return "</rdf:Property>";
	}

	public static String insertType(String resource) {
		return "<rdf:type rdf:resource=''" + resource + "''/>";
	}

	public static String insertDomain(String resource) {
		return "<rdf:domain rdf:resource=''" + resource + "''/>";
	}

	public static String insertRange(String resource) {
		return "<rdf:range rdf:resource=''" + resource + "''/>";
	}

}
