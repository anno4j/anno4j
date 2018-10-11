package com.github.anno4j.rdf_generation.fragments;

import java.util.ArrayList;
import java.util.List;

public class Fragments {
	
	private static List<Fragment> fragments;
	private static FragBool fragBool;
	private static FragByte fragByte;
	private static FragDate fragDate;
	private static FragDouble fragDouble;
	private static FragFloat fragFloat;
	private static FragInteger fragInteger;
	private static FragLong fragLong;
	private static FragShort fragShort;
	private static FragString fragString;
	private static FragVoid fragVoid;
	
	public static List<Fragment> getFragments() {
		setFragments();
		return fragments;
	}

	public static void setFragments() {
		fragments = new ArrayList<Fragment>();
		fragBool = new FragBool();
		fragByte = new FragByte();
		fragDate = new FragDate();
		fragDouble = new FragDouble();
		fragFloat = new FragFloat();
		fragInteger = new FragInteger();
		fragLong = new FragLong();
		fragShort = new FragShort();
		fragString = new FragString();
		fragVoid = new FragVoid();
		
		fragments.add(fragBool);
		fragments.add(fragByte);
		fragments.add(fragDate);
		fragments.add(fragDouble);
		fragments.add(fragFloat);
		fragments.add(fragInteger);
		fragments.add(fragLong);
		fragments.add(fragShort);
		fragments.add(fragString);
		fragments.add(fragVoid);
	}
}
