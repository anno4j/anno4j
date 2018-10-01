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
				for(int i = 0; i < e1.getValue().size(); i++) {
					content += RDFTemplate.insertClass(e1.getValue().get(i)) + "\r\n";
				}
			}
				content += RDFTemplate.insertEndClass() + "\r\n" + "\r\n";
		}
		
		
		// AB HIER WEITER MIT ANPASSUNG FÜR BUNDLED !
			
			for(Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()){
				content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";
				content += RDFTemplate.insertType("") + "\r\n";
				content += RDFTemplate.insertDomain(Extractor.getClassvalue()) + "\r\n";
				content += RDFTemplate.insertRange(Mapper.mapReturn(e.getKey(), Extractor.getReturnIriMap())) + "\r\n";
				content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
			}
			
		content += RDFTemplate.insertEndRDF();
		return content; //call template und parameter mit Mapper.map(...) sowie aufruf zu den Namespaces
	}
}
