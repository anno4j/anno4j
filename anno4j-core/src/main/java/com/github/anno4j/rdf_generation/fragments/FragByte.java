package com.github.anno4j.rdf_generation.fragments;

import com.github.anno4j.rdf_generation.namespaces.XSD;

public class FragByte implements Fragment{
	
	private final static String ns = XSD.NS;

	private final static String fragment = "byte";

	private final static String uri = ns + fragment;

	private final static String javaequiv = "class java.lang.Byte";
	private final static String javaequiv2 = "byte";

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
