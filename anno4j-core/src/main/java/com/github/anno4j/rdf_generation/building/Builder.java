package com.github.anno4j.rdf_generation.building;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.anno4j.rdf_generation.namespaces.RDF;
import com.github.anno4j.rdf_generation.namespaces.RDFS;

public class Builder {

	/**
	 * The output file in "RDF/XML" as a string.
	 */
	private static String content;

	/**
	 * Concatenates parts of the RDFTemplate in order to build the output file in
	 * "RDF/XML".
	 * 
	 * @return The output file as a string in "RDF/XML".S
	 */
	public static String build() {
		content = RDFTemplate.insertHead() + "\r\n" + "\r\n";
		content += RDFTemplate.insertRdf() + "\r\n";

		content += RDFTemplate.insertNamespaceTemp(RDF.abbrev, RDF.NS);
		content += "\r\n" + RDFTemplate.insertNamespaceTemp(RDFS.abbrev, RDFS.NS);

//		for() {
//			content += RDFTemplate.insertNamespaceTemp(0, "", "") + "\r\n";
//		}

		content += RDFTemplate.insertRdfEndTag() + "\r\n";
		content += "\r\n";

		// Insert every class with class annotation, subclassOf and EndClassTag after
		// another.
		for (Map.Entry<Integer, String> e : Extractor.getClassValues().entrySet()) {
			content += RDFTemplate.insertClass(e.getValue()) + "\r\n";

			for (Entry<Integer, List<String>> e1 : Extractor.getSubClasses().entrySet()) {
				for (int i = 0; i < e1.getValue().size(); i++) {
					if (e.getKey() == e1.getKey()) {
						content += RDFTemplate.insertSubclass(e1.getValue().get(i)) + "\r\n";
					}
				}
			}
			content += RDFTemplate.insertEndClass() + "\r\n" + "\r\n";
		}

		// Insert every property after the otherone. Every property contains an
		// annotation value, type,
		// domain and range.
		for (Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()) {
			Integer classID = null;
			content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";

			// If a type is set for this method, it is printed into the template
			for (Entry<Integer, String> e1 : Extractor.getTypeMap().entrySet()) {
				if (e.getKey() == e1.getKey() && e1.getValue() != null) {
					content += RDFTemplate.insertType(Mapper.mapType(e1.getValue())) + "\r\n";
				}
			}

			// In order to get the domain of the property, the classID of the corresponding
			// class needs to be found.
			for (Entry<Integer, Integer> e1 : Extractor.getPropToClassID().entrySet()) {
				// propID of methodIriMap == propID of propToClassIDMap
				if (e1.getKey() == e.getKey()) {
					// e1.getValue is the classID of the class whose annotation value is
					// needed.
					classID = e1.getValue();
					for (Entry<Integer, String> e2 : Extractor.getClassValues().entrySet()) {
						if (e2.getKey() == classID) {
							// insert the corresponding annotation value of the classID into the template.
							content += RDFTemplate.insertDomain(e2.getValue()) + "\r\n";
						}
					}
				}
			}

			// In order to get the range of the porperty, mapping needs to be done to handle
			// primitive datatyped differentely than complex ones.
			content += RDFTemplate.insertRange(Mapper.mapJavaReturn(e.getKey(), Extractor.getRangeMap())) + "\r\n";
			content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
		}

		content += RDFTemplate.insertEndRDF();
		return content;
	}
}
