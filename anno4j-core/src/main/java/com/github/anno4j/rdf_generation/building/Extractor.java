package com.github.anno4j.rdf_generation.building;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.annotations.Iri;

import com.github.anno4j.rdf_generation.validation.Validator;

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

	private static String packages;

	/**
	 * Adds for every class contained in the classes list its class und method
	 * values to the Extractor. One file which contains all classes is build.
	 * 
	 * @param classes The list of classes the convert to one file.
	 * @return The converted file in "RDF/XML".
	 * @throws IOException
	 */
	public static String extractMany(List<Class<?>> classes, String packages) throws IOException {
		setPackages(packages);
		for (int i = 0; i < classes.size(); i++) {
			setup(classes.get(i));
		}
		return Builder.build(false);
	}

	/**
	 * Adds the class and method values of one class to the Extractor and builds the
	 * file.
	 * 
	 * @param refclass The class to be converted.
	 * @return The converted file in "RDF/XML".
	 * @throws IOException
	 */
	public static String extractOne(Class<?> refclass, String packages) throws IOException {
		setPackages(packages);
		setup(refclass);
		return Builder.build(true);
	}

	/**
	 * By using java reflection a class is analysed and its class and method values
	 * are added to the Extractor. The following values are being extracted:
	 * 
	 * - the name of the class - the Iri-annotation value of a class - all
	 * superclasses of a class - various values concering the methods are set.
	 * 
	 * @param refclass The class that gets analyzed via Reflection.
	 */
	public static void setup(Class<?> refclass) {
		classID++;
		classNames.put(classID, extractLastName(refclass.getCanonicalName()));
		classValues.put(classID, extractClassAnnotValue(refclass));
		
		
		Class<?>[] clazzes = refclass.getInterfaces();
		if (clazzes != null && clazzes.length > 0) {
			for (int i = 0; i < clazzes.length; i++) {
					allSubClasses = extractClassAnnotValues(clazzes);
			}
			subClasses.put(classID, allSubClasses);
		} else {
			subClasses.put(classID, null);
		}
		
		Method[] methods = refclass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			methodSetup(propID++, methods[i]);
		}
	}

	/**
	 * Extracts the annotation value of every given class contained in the array.
	 * 
	 * @param classes All classes whose annotation values should be returned.
	 * @return all annotation values of @classes
	 */
	private static List<String> extractClassAnnotValues(Class<?>[] classes) {
		List<String> shortnames = new ArrayList<String>();
		for (int i = 0; i < classes.length; i++) {
			shortnames.add(extractClassAnnotValue(classes[i]));
		}
		return shortnames;
	}

	/**
	 * Returns the Iri-annotation value of a certain class
	 * 
	 * @param refclass The class whose annotation value is returned
	 * @return the Iri-annotation value of a class
	 */
	static String extractClassAnnotValue(Class<?> refclass) {
		if (refclass.isAnnotationPresent(Iri.class)) {
			return refclass.getAnnotation(Iri.class).value();
		}
		return null;
	}

	/**
	 * Returns a substring which starts after the last '.' and end at the end of the
	 * string.
	 * 
	 * @param pathname The string of which the substring is needed
	 * @return The substring of "name"
	 */
	public static String extractLastName(String pathname) {
		int nameindexFirst = pathname.lastIndexOf(".");
		int nameindexLast = pathname.length();
		return pathname.substring(nameindexFirst + 1, nameindexLast);
	}

	/**
	 * Sets all method values in the maps above via Java Reflection.
	 * 
	 * The extracted values from the methods are: - The ID of a method and the ID of
	 * the class to which it belongs. - Name of the method - Iri-annotation values
	 * of the method - Return type of the method - The type of the method
	 * 
	 * @param propID
	 * @param method
	 */
	private static void methodSetup(int propID, Method method) {

		String methodIri = "";
		propToClassID.put(propID, getClassID());

		if (method.isAnnotationPresent(Iri.class)) {
			methodIri = method.getAnnotation(Iri.class).value();
		}

		idNameMap.put(propID, method.getName());
		methodIriMap.put(propID, methodIri);
		String returntype = method.getReturnType().toString();

		if (returntype.equals("interface java.util.Set") || returntype.equals("interface java.util.List")) {
			Type type = method.getGenericReturnType();
			ParameterizedType pType = (ParameterizedType) type;
			Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
			rangeMap.put(propID, clazz.toString());
		} else {
			rangeMap.put(propID, method.getReturnType().toString());
		}
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

	public static Map<Integer, Integer> getPropToClassID() {
		return propToClassID;
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

	private static void setAllSubClasses(List<String> aaaallSubClasses) {
		allSubClasses = aaaallSubClasses;
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

	private static void setPackages(String packages2) {
		packages = packages2;
	}

	public static String getPackages() {
		return packages;
	}
}
