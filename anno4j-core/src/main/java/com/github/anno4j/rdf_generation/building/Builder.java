package com.github.anno4j.rdf_generation.building;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Builder {

	private static String content;

	public static String build() {
		content = RDFTemplate.insertHead() + "\r\n" + "\r\n";
		content += RDFTemplate.insertRdf() + "\r\n";

//		for() {
//			content += RDFTemplate.insertNamespaceTemp(0, "", "") + "\r\n";
//		}
//		content += "\r\n";

		for (Map.Entry<Integer, String> e : Extractor.getClassValues().entrySet()) {
			content += RDFTemplate.insertClass(e.getValue()) + "\r\n";

			for (Entry<Integer, List<String>> e1 : Extractor.getSubClasses().entrySet()) {
				for (int i = 0; i < e1.getValue().size(); i++) {
					content += RDFTemplate.insertSubclass(e1.getValue().get(i)) + "\r\n";
				}
			}
			content += RDFTemplate.insertEndClass() + "\r\n" + "\r\n";
		}

		for (Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()) {
			Integer classID = null;
			content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";
			content += RDFTemplate.insertType("") + "\r\n";

			for (Entry<Integer, Integer> e1 : Extractor.getPropToClassID().entrySet()) {
				if (e1.getKey() == e.getKey()) { // propID von methodIriMap == propI von propToClassIDMap
					classID = e1.getValue(); // e1.getValue ist hier die classID wovon wir das classAnnotValue
														// wollen
					for (Entry<Integer, String> e2 : Extractor.getClassValues().entrySet()) {
						if (e2.getKey() == classID) {
							content += RDFTemplate.insertDomain(e2.getValue()) + "\r\n"; // die passende annot zum key
																							// ins template einfügen
						}
					}
				}
			}

			content += RDFTemplate.insertRange(Mapper.mapJavaReturn(e.getKey(), Extractor.getRangeMap())) + "\r\n";
			content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
		}

		content += RDFTemplate.insertEndRDF();
		return content; // call template und parameter mit Mapper.map(...) sowie aufruf zu den
						// Namespaces
	}
}
