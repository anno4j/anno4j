package com.github.anno4j.rdf_generation.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Extractor {

//hier als Attribute Liste von zu extrahierenden Sprachkonstrukten
	
	public static void reflect(Class<?> refclass) {
		if (refclass.getSuperclass() != null) {
			System.out.println("EXTENDS");
		}
		Method[] methods = refclass.getDeclaredMethods();
		Field[] fields = refclass.getDeclaredFields();
		Annotation[] annotations = refclass.getAnnotations();
		for (Method method : methods) {
			System.out.println("Own declared method of the class'" + refclass.getName() + "' = " + method.getName());
			Class<Deprecated> a = Deprecated.class;
//			Class<MyReflectionAnnot> b = MyReflectionAnnot.class;
			if (method.isAnnotationPresent(a)) {
				System.out.println("This method has following annotation: " + method.getAnnotation(a));
			}
//			if (method.isAnnotationPresent(b)) {
//				MyReflectionAnnot annot = method.getAnnotation(b);
//				System.out.println("This method has following annotation: " + method.getAnnotation(b));
//				System.out.println("And the value is: " + annot.value());
//			}
			System.out.println(method.getReturnType());
		}
		for (Field field : fields) {
			System.out.println("Field of the class'" + refclass.getName() + "' = " + field.getName());
		}
		for (Annotation anno : annotations) {
			System.out.println("Annotations of the class'" + refclass.getName() + "' = " + anno.toString());
		}

	}
	
}
