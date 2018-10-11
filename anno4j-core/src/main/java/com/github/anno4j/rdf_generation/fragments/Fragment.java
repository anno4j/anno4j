package com.github.anno4j.rdf_generation.fragments;

public interface Fragment {

	public boolean hasRelationTo(String javaval);
	
	public String getURI();

	public String getJavaEquiv();
	
}
