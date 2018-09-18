package com.github.anno4j.rdf_generation.building;

import java.util.Map;

public class Mapper {


	public Mapper() {
	}

	public static String mapToRDF(Map<Integer, String> reMap, Map<Integer, String> typeMap) {
		for (Map.Entry<Integer, String> e : reMap.entrySet()) {
			e.getValue();
		}

		return Builder.build();
	}
}
