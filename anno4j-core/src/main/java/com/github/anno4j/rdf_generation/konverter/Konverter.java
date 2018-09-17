package com.github.anno4j.rdf_generation.konverter;

import com.github.anno4j.rdf_generation.Player;

public class Konverter {

	public static Class<?> classConvertion(String interfaceAsString) {
		// Validierung hier 
		// return interfaceAsString.getClass(); // STIMMT NICHT
		return Player.class;
	}
	
}