package com.github.anno4j.rdf_generation.fragments;

public class FragChar implements Fragment {
	
	private final static String ns = "";

	private final static String fragment = "";

	private final static String uri = ns + fragment;

	private final static String javaequiv = "class java.lang.Character";
	private final static String javaequiv2 = "char";

	@Override
	public boolean hasRelationTo(String javaval) {
		if (javaequiv.equals(javaval)|| javaequiv2.equals(javaval)) {
			return true;
		}
		return false;
	}

	@Override
	public String getURI() {
		return uri;
	}

}
