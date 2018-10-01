package com.github.anno4j.rdf_generation.fragments;

import com.github.anno4j.rdf_generation.namespaces.XSD;

public class FragString implements Fragment {
	
	private final static String ns = XSD.NS;

	private final static String fragment = "string";

	private final static String uri = ns + fragment;

	private final static String javaequiv = "class java.lang.String";

	@Override
	public boolean hasRelationTo(String javaval) {
		if (javaequiv.equals(javaval)) {
			return true;
		}
		return false;
	}

	@Override
	public String getURI() {
		return uri;
	}
}
