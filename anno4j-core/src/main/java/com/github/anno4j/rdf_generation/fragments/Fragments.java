package com.github.anno4j.rdf_generation.fragments;

import java.util.ArrayList;
import java.util.List;

public class Fragments {
	
	private static List<Fragment> fragments;
	private FragBool fragBool;
	private FragByte fragByte;
	private FragChar fragChar;
	private FragDate fragDate;
	private FragFloat fragFloat;
	private FragInteger fragInteger;
	private FragLong fragLong;
	private FragShort fragShort;
	private FragString fragString;
	private FragVoid fragVoid;
	
	
	
	public Fragments() {
		fragments = new ArrayList<Fragment>();
		fragBool = new FragBool();
		fragByte = new FragByte();
		fragChar = new FragChar();
		fragDate = new FragDate();
		fragFloat = new FragFloat();
		fragInteger = new FragInteger();
		fragLong = new FragLong();
		fragShort = new FragShort();
		fragString = new FragString();
		fragVoid = new FragVoid();
		
		fragments.add(fragBool);
		fragments.add(fragByte);
		fragments.add(fragChar);
		fragments.add(fragDate);
		fragments.add(fragFloat);
		fragments.add(fragInteger);
		fragments.add(fragLong);
		fragments.add(fragShort);
		fragments.add(fragString);
		fragments.add(fragVoid);
		
	}
	

	public static List<Fragment> getFragments() {
		return fragments;
	}

}
