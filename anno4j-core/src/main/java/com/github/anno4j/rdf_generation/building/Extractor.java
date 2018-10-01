package com.github.anno4j.rdf_generation.building;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.annotations.Iri;

import com.fasterxml.jackson.databind.node.IntNode;

public class Extractor {

//	Classes:
	private static int classID;
	private static Map<Integer, String> classNames = new HashMap<Integer, String>();
	private static Map<Integer, String> classValues = new HashMap<Integer, String>();
	private static Map<Integer, List<String>> subClasses = new HashMap<Integer, List<String>>();
	private static List<String> allSubClasses = new ArrayList<>();

//	Properties:
	private static int propID;
	private static Map<Integer, String> idNameMap = new HashMap<Integer, String>(); // id and methodname
	private static Map<Integer, String> methodIriMap = new HashMap<Integer, String>(); // id and "about" of property
	private static Map<Integer, String> rangeMap = new HashMap<Integer, String>(); // id and range (domain as
																					// "about" of class, classvalue)
	private static Map<Integer, String> typeMap = new HashMap<Integer, String>(); // id and type

	public Extractor() {
		classID = 0;
		propID = 0;
	}

	/**
	 * Fügt nach und nach für jede klasse neue werte in den Extractor ein, buildet
	 * am ende
	 * 
	 * @param classes
	 * @return
	 */
	public static String extractMany(List<Class<?>> classes) {
		for (int i = 0; i < classes.size(); i++) {
			setup(classes.get(i));
		}
		return Builder.build();
	}

	/**
	 * Fügt nur eine Klasse in den Extractor ein und buildet
	 * 
	 * @param refclass
	 * @return
	 */
	public static String extractOne(Class<?> refclass) {
		setup(refclass);
		return Builder.build();
	}

	/**
	 * Das eigentliche reflecten und werte einfügen in den Extractor für 1 Klasse
	 * 
	 * @param refclass
	 */
	public static void setup(Class<?> refclass) {
		classID++;
		classNames.put(classID, extractLastName(refclass.getCanonicalName()));
		classValues.put(classID, extractClassAnnotValue(refclass));

		if (refclass.getInterfaces() != null) {
			allSubClasses = LastPackageNames(refclass.getInterfaces());
		}
		subClasses.put(classID, allSubClasses);

		propID++;
		Method[] methods = refclass.getDeclaredMethods();
		if (methods.length != 0) {
			for (int i = 0; i < methods.length; i++) {
				mapSetup(propID, methods[i]);
			}
		}
	}

	/**
	 * Value der Klassenannotation extrahieren
	 * 
	 * @param refclass
	 * @return
	 */
	private static String extractClassAnnotValue(Class<?> refclass) {
		if (refclass.isAnnotationPresent(Iri.class)) {
			return refclass.getAnnotation(Iri.class).value();
//			System.out.println("AnnotationValue: " + classvalue);
//			System.out.println();
		}
		return null;
	}

	/**
	 * 
	 * @param clazz Ein Array aus Klassen, wovon man den letzten Namen bekommt,
	 *              nicht die ganze Packagestruktur
	 * @return
	 */
	private static List<String> LastPackageNames(Class<?>[] clazz) {
		List<String> shortnames = new ArrayList<String>();
		for (int i = 0; i < clazz.length; i++) {
			String name = clazz[i].getCanonicalName();
			shortnames.add(extractLastName(name));
//			System.out.println("SubClasses: " + name.substring(nameindexFirst+1, nameindexLast));
		}
		return shortnames;
	}

	/**
	 * Extrahiert den letzten Teil einer packagestruktur
	 * 
	 * @param name
	 * @return
	 */
	private static String extractLastName(String name) {
		int nameindexFirst = name.lastIndexOf(".");
		int nameindexLast = name.length();
		return name.substring(nameindexFirst + 1, nameindexLast);
	}

	/**
	 * Befüllt die Maps für Properties aber nur für eine Methode mit einer ID
	 * 
	 * @param mapID
	 * @param method
	 */
	private static void mapSetup(int mapID, Method method) {

		String methodIri = "";

		if (method.isAnnotationPresent(Iri.class)) {
			methodIri = method.getAnnotation(Iri.class).value();
		}
		idNameMap.put(mapID, method.getName());
		methodIriMap.put(mapID, methodIri);
		rangeMap.put(mapID, method.getReturnType().toString());
//		typeIriMap.put(mapID, type);

//		 PRINTS:
		System.out.println("idNameMap: " + method.getName());
		System.out.println("methdoIriMap: " + methodIri);
		System.out.println("returnIriMap: " + method.getReturnType().toString());
		System.out.println();
//		System.out.println("typeIriMap: " + type);
	}

	public static int getClassID() {
		return classID;
	}

	public static Map<Integer, String> getClassNames() {
		return classNames;
	}

	public static Map<Integer, String> getClassValues() {
		return classValues;
	}

	public static Map<Integer, List<String>> getSubClasses() {
		return subClasses;
	}

	public static int getPropID() {
		return propID;
	}

	public static Map<Integer, String> getIdNameMap() {
		return idNameMap;
	}

	public static Map<Integer, String> getMethodIriMap() {
		return methodIriMap;
	}

	public static Map<Integer, String> getRangeMap() {
		return rangeMap;
	}

	public static Map<Integer, String> getTypeMap() {
		return typeMap;
	}

}
