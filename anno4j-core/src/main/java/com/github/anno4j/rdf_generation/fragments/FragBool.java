package com.github.anno4j.rdf_generation.fragments;

import com.github.anno4j.rdf_generation.namespaces.XSD;

public class FragBool implements Fragment {
	
	private final static String ns = XSD.NS;

	private final static String fragment = "boolean";

	private final static String uri = ns + fragment;

	private final static String javaequiv = "class java.lang.Boolean";
	private final static String javaequiv2 = "boolean";

	@Override
	public boolean hasRelationTo(String javaval) {
		if (javaequiv.equals(javaval) || javaequiv2.equals(javaval)) {
			return true;
		}
		return false;
	}

	@Override
	public String getURI() {
		return uri;
	}
}
