package com.github.anno4j.rdf_generation.fragments;

import com.github.anno4j.rdf_generation.namespaces.XSD;

public class FragString implements Fragment {

	private final static String ns = XSD.NS;

	private final static String fragment = "string";

	private final static String uri = ns + fragment;

	private final static String javaequiv = "class java.lang.String";
	private final static String javaequiv2 = "class java.lang.Character";
	private final static String javaequiv3 = "char";
	private final static String javaequiv4 = "class [Ljava.lang.String;";
	private final static String javaequiv5 = "class [Ljava.lang.Character;";
	private final static String javaequiv6 = "class [C";

	@Override
	public boolean hasRelationTo(String javaval) {
		if (javaequiv.equals(javaval) || javaequiv2.equals(javaval) || javaequiv3.equals(javaval)
				|| javaequiv4.equals(javaval) || javaequiv5.equals(javaval) || javaequiv6.equals(javaval)) {
			return true;
		}
		return false;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getJavaEquiv() {
		return "";
	}
}
