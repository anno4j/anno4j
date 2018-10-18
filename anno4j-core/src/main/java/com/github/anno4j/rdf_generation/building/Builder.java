package com.github.anno4j.rdf_generation.building;

import java.io.IOException;
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
	 * @throws IOException
	 */
	public static String build() throws IOException {
		content = addHead();

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

		// Insert every property after the other one. Every property contains an
		// annotation value, domain and range.
		for (Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()) {
			String range = Mapper.mapJavaReturn(e.getKey(), Extractor.getRangeMap());
			if (range != "void") {
				Integer classID = null;
				content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";

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
				if (range != null) {
					content += RDFTemplate.insertRange(range) + "\r\n";
				}
				content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
			}
		}

		content += RDFTemplate.insertEndRDF();
		return content;
	}

	private static String addHead() {
		String content = RDFTemplate.insertHead() + "\r\n" + "\r\n";
		content += RDFTemplate.insertRdf() + "\r\n";

		content += RDFTemplate.insertNamespaceTemp(RDF.abbrev, RDF.NS);
		content += "\r\n" + RDFTemplate.insertNamespaceTemp(RDFS.abbrev, RDFS.NS);
		content += RDFTemplate.insertRdfEndTag() + "\r\n";
		content += "\r\n";
		return content;
	}
}
