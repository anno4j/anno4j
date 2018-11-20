package com.github.anno4j.rdf_generation;

public class ConvertionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConvertionException() {
		super("Convertion Exception");
	}

	public ConvertionException(String fehlermeldung) {
		super(fehlermeldung);
	}
}
