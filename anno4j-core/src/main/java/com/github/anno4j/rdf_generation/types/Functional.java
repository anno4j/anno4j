package com.github.anno4j.rdf_generation.types;

import com.github.anno4j.rdf_generation.namespaces.OWL;

public class Functional implements Type {

	private String name = "Functional";
	
	private String ns = OWL.NS;
			
	private String fragment = "FunctionalProperty";
	
	private String URI = ns + fragment;

	@Override
	public boolean hasRelationTo(String extractType) {
		if(extractType.equals(name)) {
			return true;
		}
		return false;
	}

	@Override
	public String getURI() {
		return URI;
	}
}
