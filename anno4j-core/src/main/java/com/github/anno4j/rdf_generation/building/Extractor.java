package com.github.anno4j.rdf_generation.building;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.annotations.Iri;

public class Extractor {

	private static String classvalue;
	private static String classcomment;
	private static boolean superclassExists;
	private static List<String> subclassof;
	private static int id;
	private static String classname;
	private static Map<Integer, String> idNameMap = new HashMap<Integer, String>(); // id and methodname
	private static Map<Integer, String> methodIriMap = new HashMap<Integer, String>(); // id and "about" of property
	private static Map<Integer, String> returnIriMap = new HashMap<Integer, String>(); // id and range (domain as
																						// "about" of class, classvalue)
	private static Map<Integer, String> typeIriMap = new HashMap<Integer, String>(); // id and type

	public Extractor() {
		classcomment = "";
		superclassExists = false;
		id = 0;
	}

	public static String extractFromList(List<Class<?>> classes) { // passt noch nicht, alle classen später in 1
																	// dokument
		return null;
	}

	public static String extractFrom(Class<?> refclass) {
		
		classname = getClassName(refclass.getCanonicalName());

		if (refclass.isAnnotationPresent(Iri.class)) {
			classvalue = refclass.getAnnotation(Iri.class).value();
//			System.out.println("AnnotationValue: " + classvalue);
//			System.out.println();
		}

		if (refclass.getInterfaces() != null) {
			superclassExists = true;
			subclassof = giveSimpleName(refclass.getInterfaces());
		}

		Method[] methods = refclass.getDeclaredMethods();
		if (methods.length != 0) {
			for (int i = 0; i < methods.length; i++) {
				mapSetup(id++, methods[i]);
			}
		}
		return Builder.build();
	}

	private static String getClassName(String canonicalName) {
		String name = null;
		int nameindexFirst = canonicalName.lastIndexOf(".");
		int nameindexLast = canonicalName.length();
		name = canonicalName.substring(nameindexFirst+1, nameindexLast);
		return name;
	}

	private static List<String> giveSimpleName(Class<?>[] interfaces) {
		List<String> shortnames = new ArrayList<String>();
		for (int i = 0; i < interfaces.length; i++) {
			String name = interfaces[i].getCanonicalName();
			int nameindexFirst = name.lastIndexOf(".");
			int nameindexLast = name.length();
			shortnames.add(name.substring(nameindexFirst+1, nameindexLast));
			
//			System.out.println("SubClasses: " + name.substring(nameindexFirst+1, nameindexLast));
		}
		return shortnames;
	}

	private static void mapSetup(int mapID, Method method) {

		String methodIri = "";

		if (method.isAnnotationPresent(Iri.class)) {
			methodIri = method.getAnnotation(Iri.class).value();
		}
		idNameMap.put(mapID, method.getName());
		methodIriMap.put(mapID, methodIri);
		returnIriMap.put(mapID, method.getReturnType().toString());
//		typeIriMap.put(mapID, type);

//		 PRINTS:
		System.out.println("idNameMap: " + method.getName());
		System.out.println("methdoIriMap: " + methodIri);
		System.out.println("returnIriMap: " + method.getReturnType().toString());
		System.out.println();
//		System.out.println("typeIriMap: " + type);
	}

	public static String getClassvalue() {
		return classvalue;
	}

	public String getClasscomment() {
		return classcomment;
	}

	public static boolean isSubclass() {
		return superclassExists;
	}

	public static List<String> getSubclassof() {
		return subclassof;
	}

	public static int getId() {
		return id;
	}

	public static Map<Integer, String> getIdNameMap() {
		return idNameMap;
	}

	public static Map<Integer, String> getMethodIriMap() {
		return methodIriMap;
	}

	public static Map<Integer, String> getReturnIriMap() {
		return returnIriMap;
	}

	public static Map<Integer, String> getTypeIriMap() {
		return typeIriMap;
	}

	public static String getClassname() {
		return classname;
	}

	public static void setClassnames(String classname) {
		Extractor.classname = classname;
	}
}
