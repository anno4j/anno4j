package com.github.anno4j.rdf_generation.building;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.annotations.Iri;

public class Extractor {

	private static String classvalue;
	private static String classcomment;
	private static boolean superclassExists;
	private static Class<?>[] subclassof;
	private static int id;
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

	public static String extractFrom(Class<?> refclass) {

		if (refclass.isAnnotationPresent(Iri.class)) {
			classvalue = refclass.getAnnotation(Iri.class).value();
			System.out.println("AnnotationValue: " + classvalue);
			System.out.println();
		}

		if (refclass.getInterfaces() != null) {
			superclassExists = true;
			subclassof = refclass.getInterfaces();
			
			for(int i = 0; i < subclassof.length; i++) {
				System.out.println("SubClasses: " + subclassof[i]);
			}
			System.out.println();

		}

		Method[] methods = refclass.getDeclaredMethods();
		if (methods.length != 0) {
			for (int i = 0; i < methods.length; i++) {
				mapSetup(id++, methods[i]);
			}
		}
		return Mapper.mapToRDF(returnIriMap, typeIriMap);
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
	
	private static void extractComment(Class<?> refclass) {
		
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

	public static Class<?>[] getSubclassof() {
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
}
