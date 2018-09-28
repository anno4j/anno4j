package com.github.anno4j.rdf_generation.building;

import java.util.Map.Entry;

public class Builder {
	
	private static String content;

	public static String build() {
		content = RDFTemplate.insertHead() + "\r\n" + "\r\n";
		content += RDFTemplate.insertClass(Extractor.getClassvalue()) + "\r\n";
		
		for(int i = 0; i < Extractor.getSubclassof().size(); i++) {
			content += RDFTemplate.insertSubclass("", Extractor.getSubclassof().get(i)) + "\r\n";
		}
		content += RDFTemplate.insertEndClass() + "\r\n" + "\r\n";
		
			
			for(Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()){
				content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";
				content += RDFTemplate.insertType("") + "\r\n";
				content += RDFTemplate.insertDomain(Extractor.getClassvalue()) + "\r\n";
				content += RDFTemplate.insertRange("") + "\r\n";
				content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
			}
		return content; //call template und parameter mit Mapper.map(...) sowie aufruf zu den Namespaces
	}
}
