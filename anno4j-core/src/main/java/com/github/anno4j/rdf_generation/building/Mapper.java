package com.github.anno4j.rdf_generation.building;

import java.util.List;
import java.util.Map;

import com.github.anno4j.rdf_generation.fragments.Fragment;
import com.github.anno4j.rdf_generation.fragments.Fragments;

import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;

public class Mapper {
	
	public static String mapJavaReturn(Integer propID, Map<Integer, String> rangeMap) {
		for (Map.Entry<Integer, String> e : rangeMap.entrySet()) {
			if(e.getKey() == propID) {
				return mapToRDFRange(e.getValue());
			}
		}
		return null;
	}

	private static String mapToRDFRange(String javavalue) { // matched den java-rückgabetyp auf uri für rdf in range
		List<Fragment> fraglist = Fragments.getFragments();
		for(int i = 0; i <  fraglist.size(); i++) {
			if(fraglist.get(i).hasRelationTo(javavalue)){
				return fraglist.get(i).getURI();
			} else { 
				for (Map.Entry<Integer, String> e : Extractor.getClassNames().entrySet()) {
					if(javavalue.endsWith(e.getValue())) {
						for (Map.Entry<Integer, String> e1 : Extractor.getClassValues().entrySet()) {
							if(e.getKey() == e1.getKey()) {
								return e1.getValue(); // selbst definierter Typ zb Player oder Pet
							}
						}
						
					}
				}
			}
		}
		return null;
	}
}
