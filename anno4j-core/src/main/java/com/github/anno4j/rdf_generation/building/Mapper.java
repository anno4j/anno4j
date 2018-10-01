package com.github.anno4j.rdf_generation.building;

import java.util.List;
import java.util.Map;

import com.github.anno4j.rdf_generation.fragments.Fragment;
import com.github.anno4j.rdf_generation.fragments.Fragments;

public class Mapper {
	
	public static String mapReturn(Integer id, Map<Integer, String> returnIriMap) {
		for (Map.Entry<Integer, String> e : returnIriMap.entrySet()) {
			if(e.getKey() == id) {
				return mapValueToReturn(e.getValue(), Extractor.getClassname());
			}
		}
		return null;
	}

	private static String mapValueToReturn(String value, String classname) { // matched den java-rückgabetyp auf uri für rdf in range
		Fragments frag = new Fragments();
		List<Fragment> fraglist = Fragments.getFragments();
		for(int i = 0; i <  fraglist.size(); i++) {
			if(fraglist.get(i).hasRelationTo(value)){
				return fraglist.get(i).getURI();
			} else if(value.endsWith(classname)) { // selber definierter Typ zB Player oder Pet
				return Extractor.getClassvalue(); // nicht geeignet für mehrere klassen
			}
		}
		return null;
	}
}
