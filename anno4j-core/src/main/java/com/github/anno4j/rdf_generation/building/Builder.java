package com.github.anno4j.rdf_generation.building;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.anno4j.rdf_generation.ConvertionException;
import com.github.anno4j.rdf_generation.namespaces.RDF;
import com.github.anno4j.rdf_generation.namespaces.RDFS;

public class Builder {

	/**
	 * The output file in "RDF/XML" as a string.
	 */
	private static String content;

	/**
	 * The Logger for printing progress and user messages.
	 */
	private final static Logger logger = LoggerFactory.getLogger(Builder.class);

	/**
	 * Concatenates parts of the RDFTemplate in order to build the output file in
	 * "RDF/XML".
	 * 
	 * @return The output file as a string in "RDF/XML".
	 * @throws IOException
	 * @throws ConvertionException
	 */
	public static String build(boolean oneClassOnly) throws IOException, ConvertionException {
		content = addHead();

		// Insert every class with class annotation, subclassOf and EndClassTag after
		// another.
		for (Map.Entry<Integer, String> e : Extractor.getClassValues().entrySet()) {
			if (e.getValue() == null || e.getValue().equals("")) {
				if (oneClassOnly) {
					throw new ConvertionException("No Generation possible. No @Iri-Annotation found in class.");
				} else {
					logger.debug("No @Iri-Annotation found in a class. File will be generated without that class.");
				}
			} else {
				content += RDFTemplate.insertClass(e.getValue()) + "\r\n";

				for (Entry<Integer, List<String>> e1 : Extractor.getSubClasses().entrySet()) {
					if (e1.getValue() != null) {
						for (int i = 0; i < e1.getValue().size(); i++) {
							if (e.getKey() == e1.getKey()) {
								if (e1.getValue().get(i) != null) {
									content += RDFTemplate.insertSubclass(e1.getValue().get(i)) + "\r\n";
								} else if (e1.getValue().get(i) == null && e1.getValue().size() != 0) {
									logger.debug("An undefined Subclass occured in class " + e.getValue()
											+ ". File will be generated without it.");
								}
							}
						}
					}
				}
				content += RDFTemplate.insertEndClass() + "\r\n" + "\r\n";
			}
		}

		// Insert every property after the other one. Every property contains an
		// annotation value, domain and range.
		for (Entry<Integer, String> e : Extractor.getMethodIriMap().entrySet()) {

			if (e.getValue() == null || e.getValue().equals("")) {
				logger.debug(
						"One of the properites contains no @Iri-Annotation. File will be generated without that property.");
			} else {
				if (!convertProp(e)) {
					logger.debug(
							"Properites of class with no @Iri-Annotation can't be generated. File will be generated without those properties.");

				} else {

					String range = Mapper.mapJavaReturn(e.getKey(), Extractor.getRangeMap());
					if (range == null || range.equals("")) {
						throw new ConvertionException("No Generation possible. A complex datatype could not be found.");
					}
					Integer classID = null;
					content += RDFTemplate.insertProperty(e.getValue()) + "\r\n";
					if (propertyIsSub(e.getKey())) { // ich gebe die ID der methode rein, die einen return wert hat

						// wenn eine klasse ein extends besitzt, also oben im RDFS-Dokument bereits eine
						// subclassof zu Hauptgericht hat
						// dann in der subklasse (evtl auch mehreren suchen, ob methode den selben namen
						// haben, wenn ja -> subPropertyOf Methodenname
						// evtl auch mehrere
						// wenn nein -> nichts

						// System.out.println(e.getValue());
						content += RDFTemplate.insertSubProperty(getSuperpropOfProp(e.getKey())) + "\r\n";
						// System.out.println(getSuperpropOfProp(e.getKey()));
					}

					// In order to get the domain of the property, the classID of the corresponding
					// class needs to be found.
					for (Entry<Integer, Integer> e1 : Extractor.getPropToClassID().entrySet()) {
						// propID of methodIriMap == propID of propToClassIDMap
						if (e1.getKey() == e.getKey()) {
							// e1.getValue is the classID of the class whose annotation value is
							// needed.
							classID = e1.getValue();
							for (Entry<Integer, String> e2 : Extractor.getClassValues().entrySet()) {
								if (e2.getKey() == classID) {
									// insert the corresponding annotation value of the classID into the template.
									content += RDFTemplate.insertDomain(e2.getValue()) + "\r\n";
								}
							}
						}
					}
					content += RDFTemplate.insertRange(range) + "\r\n";
					content += RDFTemplate.insertEndProperty() + "\r\n" + "\r\n";
				}
			}
		}

		content += RDFTemplate.insertEndRDF();
		return content;
	}

	/**
	 * Checks if an input entry with an property ID shouldn't be converted because
	 * it belongs to a class with no @Iri annotation.
	 * 
	 * @param e The input entry.
	 * @return true, if a property can be converted because its class has a
	 *         present @Iri annotation, false otherwise.
	 */
	private static boolean convertProp(Entry<Integer, String> e) {
		for (Entry<Integer, Integer> eMatch : Extractor.getPropToClassID().entrySet()) {
			if (eMatch.getKey() == e.getKey()) {
				Integer ClassIDtoCheck = eMatch.getValue();
				for (Entry<Integer, String> eCheck : Extractor.getClassValues().entrySet()) {
					if (eCheck.getKey() == ClassIDtoCheck) {
						if (eCheck.getValue() == null || eCheck.getValue().equals("")) {
							return false;
						} else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the Superproperty of the property that belongs to the input property
	 * ID.
	 * 
	 * @param propID the input ID of a property of which the superproperty needs to
	 *               be found.
	 * @return The @Iri annotation value of the superproperty.
	 */
	private static String getSuperpropOfProp(Integer propID) {
		String nameOfProp = getNameOfProp(propID);

//		System.out.println("Name of originalProp: " + getAnnotOfProp(propID));

		if (nameOfProp != null) {
			for (Entry<Integer, Integer> e : Extractor.getPropToClassID().entrySet()) {
				if (propID == e.getKey()) {
					Integer ClassIDOfProp = e.getValue();
					for (Entry<Integer, List<String>> eSuperClass : Extractor.getSubClasses().entrySet()) {
						if (ClassIDOfProp == eSuperClass.getKey()) {
							if (eSuperClass.getValue() != null) {
								for (int i = 0; i < eSuperClass.getValue().size(); i++) {
									String classAnnot = eSuperClass.getValue().get(i);

//									System.out.println("AnnotationValue of Superclass: " + classAnnot);

									for (Entry<Integer, String> e1 : Extractor.getClassValues().entrySet()) {
										if (classAnnot == e1.getValue()) {
											Integer classID = e1.getKey();
											for (Entry<Integer, Integer> e2 : Extractor.getPropToClassID().entrySet()) {
												if (classID == e2.getValue()) {
													Integer superPropID = e2.getKey();
													for (Entry<Integer, String> e3 : Extractor.getIdNameMap()
															.entrySet()) {
														if (superPropID == e3.getKey()) {

//															System.out.println("Name der Subprop: "
//																	+ getAnnotOfProp(e3.getKey()));
//															System.out.println();

															if (e3.getValue().equals(nameOfProp)) {
																return getAnnotOfProp(e3.getKey());
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the @Iri annotation value of the property with the input ID
	 * 
	 * @param propID The ID of the property of which the annotation value shall be
	 *               returned.
	 * @return the annotation value of the property with the corresponding ID.
	 */
	private static String getAnnotOfProp(int propID) {
		for (Entry<Integer, String> e1 : Extractor.getMethodIriMap().entrySet()) {
			if (propID == e1.getKey()) {
				return e1.getValue();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param PropID
	 * @return
	 */
	private static boolean propertyIsSub(Integer PropID) {
		String nameOfProp = getNameOfProp(PropID);
		if (nameOfProp != null) {
			for (Entry<Integer, Integer> e : Extractor.getPropToClassID().entrySet()) {
				if (PropID == e.getKey()) {
					Integer ClassIDOfProp = e.getValue();
					for (Entry<Integer, List<String>> eSuperClass : Extractor.getSubClasses().entrySet()) {
						if (ClassIDOfProp == eSuperClass.getKey()) {
							if (eSuperClass.getValue() != null) {
								for (int i = 0; i < eSuperClass.getValue().size(); i++) {
									String classAnnot = eSuperClass.getValue().get(i);
									for (Entry<Integer, String> e1 : Extractor.getClassValues().entrySet()) {
										if (classAnnot == e1.getValue()) {
											Integer classID = e1.getKey();
											for (Entry<Integer, Integer> e2 : Extractor.getPropToClassID().entrySet()) {
												if (classID == e2.getValue()) {
													Integer SuperPropID = e2.getKey();
													for (Entry<Integer, String> e3 : Extractor.getIdNameMap()
															.entrySet()) {
														if (SuperPropID == e3.getKey()) {
															if (e3.getValue().equals(nameOfProp)) {
																return true;
															}
														}
													}
												}
											}
										}
									}

								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private static String getNameOfProp(Integer propID) {
		for (Entry<Integer, String> e : Extractor.getIdNameMap().entrySet()) {
			if (propID == e.getKey()) {
				return e.getValue();
			}
		}
		return null;
	}

	private static String addHead() {
		String content = RDFTemplate.insertHead() + "\r\n" + "\r\n";
		content += RDFTemplate.insertRdf() + "\r\n";

		content += RDFTemplate.insertNamespaceTemp(RDF.abbrev, RDF.NS);
		content += "\r\n" + RDFTemplate.insertNamespaceTemp(RDFS.abbrev, RDFS.NS);
		content += RDFTemplate.insertRdfEndTag() + "\r\n";
		content += "\r\n";
		return content;
	}
}
