package com.github.anno4j.rdf_generation.types;

import java.util.ArrayList;
import java.util.List;

public class Types {

	private static List<Type> allTypes = new ArrayList<>();
	private static Functional functional;
	
	public static List<Type> getTypes() {
		setTypes();
		return allTypes;
	}

	public static void setTypes() {
		functional = new Functional();
		allTypes.add(functional);
	}

}
