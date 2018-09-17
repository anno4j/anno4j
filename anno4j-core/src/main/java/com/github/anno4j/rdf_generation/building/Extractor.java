package com.github.anno4j.rdf_generation.building;

import java.awt.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.annotations.Iri;

public class Extractor {

	private static ArrayList<String> classIri = new ArrayList<String>();
	private String classcomment;
	private static boolean subclass;
	private static List subclassof;
	private static int id;
	private static Map<Integer, String> idNameMap = new HashMap<Integer, String>();
	private static Map<Integer, String> methodIriMap = new HashMap<Integer, String>();
	private static Map<Integer, String> returnIriMap = new HashMap<Integer, String>();
	private static Map<Integer, String> typeIriMap = new HashMap<Integer, String>();
	static Class<Iri> iri;
	// Key: Uri der Methode selbst bzw der
	// der Methode um sie später
	// als
	// Fragment zu nutzen, Value: Rückgabewert(Werbereich),
	// Definitionsbereich=Uri der Klasse

	public Extractor() {
		classcomment = "";
		subclass = false;
		id = 0;
		iri = Iri.class;
	}

	public static String reflect(Class<?> refclass) {
		
		System.out.println("Is refclass null: " +  refclass == null);

		if (refclass.isAnnotationPresent(iri)) {
			Annotation[] annotations = refclass.getAnnotations();
			for (Annotation anno : annotations) {
				classIri.add(anno.toString());
				System.out.println("Annotations: " + anno);
			}
		}
		if (refclass.getSuperclass() != null) {
			subclass = true;
			subclassof.add(refclass.getSuperclass().toString());
			System.out.println("SubClasses: " + subclassof);

		}

		Method[] methods = refclass.getDeclaredMethods();
		if (methods.length != 0) {
			for (int i = 0; i < methods.length; i++) {
				mapSetup(id++, methods[i]);
			}
		}
		return null;
	}

	private static void mapSetup(int mapID, Method method) {
		idNameMap.put(mapID, method.getName());
		String methodIri = "";
		if (method.isAnnotationPresent(iri)) {
			methodIri = method.getAnnotation(iri).toString();
		}
		methodIriMap.put(mapID, methodIri);
		System.out.println("Iri for Property: " + methodIri);
		
		returnIriMap.put(mapID, method.getReturnType().toString());
		System.out.println("Iri for Range: " + methodIri);
		
		typeIriMap.put(mapID, type);
		System.out.println("Iri for type: " + type);
		
	}
}
