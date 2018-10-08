package com.github.anno4j.rdf_generation.types;

public interface Type {

	public boolean hasRelationTo(String javaval);

	public String getURI();

}
