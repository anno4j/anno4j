package com.github.anno4j.rdf_generation.building;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashPrintJobAttributeSet;

import org.openrdf.annotations.Iri;

import com.github.anno4j.annotations.Bijective;
import com.github.anno4j.annotations.Functional;

public class Extractor {

//	CLASSES:

	/**
	 * The unique ID of every class that will be converted.
	 */
	private static int classID = 0;

	/**
	 * Mapes the classID to the name of the class with the corresponding ID.
	 */
	private static Map<Integer, String> classNames = new HashMap<Integer, String>();

	/**
	 * Maps the classID to the annotation value of the class with the corresponding
	 * ID.
	 */
	private static Map<Integer, String> classValues = new HashMap<Integer, String>();

	/**
	 * Maps the classID to a list of all superclasses of the class with the
	 * corresponding ID.
	 */
	private static Map<Integer, List<String>> subClasses = new HashMap<Integer, List<String>>();

	/**
	 * A list of all superclasses of the currently extracted class.
	 */
	private static List<String> allSubClasses = new ArrayList<>();

//	PROPERTIES:

	/**
	 * The unique ID of every method that will be converted.
	 */
	private static int propID = 0;

	/**
	 * Maps the ID of the method to the ID of the class to which it belongs.
	 */
	private static Map<Integer, Integer> propToClassID = new HashMap<Integer, Integer>();

	/**
	 * Maps the ID of the method to the name of the method with the corresponding
	 * ID.
	 */
	private static Map<Integer, String> idNameMap = new HashMap<Integer, String>();

	/**
	 * Maps the ID of the method to the method annotation value of the method with
	 * the corresponding ID.
	 */
	private static Map<Integer, String> methodIriMap = new HashMap<Integer, String>();

	/**
	 * Maps the ID of the method to the return value of the method with the
	 * corresponding ID.
	 */
	private static Map<Integer, String> rangeMap = new HashMap<Integer, String>();

	/**
	 * Maps the ID of the method to the type of the method with the corresponding
	 * ID.
	 */
	private static Map<Integer, String> typeMap = new HashMap<Integer, String>();
	
	/**
	 * A list that stores all annotations which can be mapped to types of a method.
	 */
	private static List<Class<? extends Annotation>> types = new ArrayList<>();
	private static List<String> shortTypes = new ArrayList<>();


	/**
	 * Adds for every class contained in the classes list its class und method
	 * values to the Extractor. One file which contains all classes is build.
	 * 
	 * @param classes The list of classes the convert to one file.
	 * @return The converted file in "RDF/XML".
	 */
	public static String extractMany(List<Class<?>> classes) {
		setTypes();
		for (int i = 0; i < classes.size(); i++) {
			setup(classes.get(i));
		}
		return Builder.build();
	}

	/**
	 * Adds the class and method values of one class to the Extractor and builds the
	 * file.
	 * 
	 * @param refclass The class to be converted.
	 * @return The converted file in "RDF/XML".
	 */
	public static String extractOne(Class<?> refclass) {
		setTypes();
		setup(refclass);
		return Builder.build();
	}

	/**
	 * By using java reflection a class is analysed and its class and method values
	 * are added to the Extractor. The following values are being extracted:
	 * 
	 * - the name of the class - the Iri-annotation value of a class - all
	 * superclasses of a class - various values concering the methods are set.
	 * 
	 * @param refclass The class that gets analysed via Reflection.
	 */
	public static void setup(Class<?> refclass) {
		classID++;
		classNames.put(classID, extractLastName(refclass.getCanonicalName()));
//		System.out.println("CLASSNAMES: " + classNames);
//		System.out.println();

		classValues.put(classID, extractClassAnnotValue(refclass));
//		System.out.println("CLASSVALUES: " + classValues);
//		System.out.println();

		if (refclass.getInterfaces() != null) {
			allSubClasses = LastPackageNames(refclass.getInterfaces());
		}
		subClasses.put(classID, allSubClasses);
//		System.out.println("Liste alles SubKlassen: ID: "+ getClassID() + " & " + allSubClasses);
//		System.out.println("Subclasses jeder Klasse: " + subClasses);
//		System.out.println();

		Method[] methods = refclass.getDeclaredMethods();
		if (methods.length != 0) {
			for (int i = 0; i < methods.length; i++) {
				methodSetup(propID++, methods[i]);
			}
		}
	}

	/**
	 * Returns the Iri-annotation value of a certain class
	 * 
	 * @param refclass The class whose annotation value is returned
	 * @return the Iri-annotation value of a class
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
	 * Returns a list of classes where only the names of the classes are contained,
	 * not the whole package structure.
	 * 
	 * @param clazz The array containing all the classes
	 * @return A list of all the classes names
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
	 * Returns a substring which starts after the last '.' and end at the end of
	 * the string.
	 * 
	 * @param name The string of which the substring is needed
	 * @return The substring of "name"
	 */
	private static String extractLastName(String name) {
		int nameindexFirst = name.lastIndexOf(".");
		int nameindexLast = name.length();
		return name.substring(nameindexFirst + 1, nameindexLast);
	}

	/**
	 * Sets all method values in the maps above via Java Reflection.
	 * 
	 * The extracted values from the methods are:
	 * - The ID of a method and the ID of the class to which it belongs.
	 * - Name of the method
	 * - Iri-annotation values of the method
	 * - Return type of the method
	 * - The type of the method
	 * 
	 * @param propID
	 * @param method
	 */
	private static void methodSetup(int propID, Method method) {

//		System.out.println("propID: " + propID);
//		System.out.println();

		String methodIri = "";

		propToClassID.put(propID, getClassID());
//		System.out.println("PropID to ClassID: " + propToClassID);
//		System.out.println();

		if (method.isAnnotationPresent(Iri.class)) {
			methodIri = method.getAnnotation(Iri.class).value();
		}
		idNameMap.put(propID, method.getName());
		methodIriMap.put(propID, methodIri);
		rangeMap.put(propID, method.getReturnType().toString());
		typeMap.put(propID, extractType(getTypes(), method));

//		 PRINTS:
//		System.out.println("idNameMap: " + method.getName());
//		System.out.println("methdoIriMap: " + methodIri);
//		System.out.println("returnIriMap: " + method.getReturnType().toString());
//		System.out.println();
//		System.out.println("typeIriMap: " + type);
	}

	private static String extractType(List<Class<? extends Annotation>> types, Method method) {
		for(int i = 0; i < types.size(); i++) {
			Annotation[] annotations = method.getAnnotations();
			for(int j = 0; j < annotations.length; j++) {
				if(annotations[j].annotationType().toString().equals(types.get(i).toString())) {
					String type =  types.get(i).toString();
					shortTypes.add(extractLastName(type));
					return extractLastName(type);
				}
			}
		}
		return null;
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

	public static Map<Integer, Integer> getPropToClassID() {
		return propToClassID;
	}
	
	public static List<Class<? extends Annotation>> getTypes() {
		return types;
	}
	
	public static void setTypes() {
		types.add(Functional.class);
		types.add(Bijective.class);
	}
	

	public static void setClassID(int classID) {
		Extractor.classID = classID;
	}

	public static void setClassNames(Map<Integer, String> classNames) {
		Extractor.classNames = classNames;
	}

	public static void setClassValues(Map<Integer, String> classValues) {
		Extractor.classValues = classValues;
	}

	public static void setSubClasses(Map<Integer, List<String>> subClasses) {
		Extractor.subClasses = subClasses;
	}

	public static void setAllSubClasses(List<String> allSubClasses) {
		Extractor.allSubClasses = allSubClasses;
	}

	public static void setPropID(int propID) {
		Extractor.propID = propID;
	}

	public static void setPropToClassID(Map<Integer, Integer> propToClassID) {
		Extractor.propToClassID = propToClassID;
	}

	public static void setIdNameMap(Map<Integer, String> idNameMap) {
		Extractor.idNameMap = idNameMap;
	}

	public static void setMethodIriMap(Map<Integer, String> methodIriMap) {
		Extractor.methodIriMap = methodIriMap;
	}

	public static void setRangeMap(Map<Integer, String> rangeMap) {
		Extractor.rangeMap = rangeMap;
	}

	public static void setTypeMap(Map<Integer, String> typeMap) {
		Extractor.typeMap = typeMap;
	}

	public static void setTypes(List<Class<? extends Annotation>> types) {
		Extractor.types = types;
	}

	public static void setShortTypes(List<String> shortTypes) {
		Extractor.shortTypes = shortTypes;
	}

	public static void clear() {
		setClassID(0);
		setClassNames(new HashMap<Integer, String>());
		setClassValues(new HashMap<Integer, String>());
		setSubClasses(new HashMap<Integer, List<String>>());
		setAllSubClasses(new ArrayList<String>());
		setPropID(0);
		setPropToClassID(new HashMap<Integer, Integer>());
		setIdNameMap(new HashMap<Integer, String>());
		setMethodIriMap(new HashMap<Integer, String>());
		setRangeMap(new HashMap<Integer, String>());
		setTypeMap(new HashMap<Integer, String>());
	}


}
