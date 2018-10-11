package com.github.anno4j.rdf_generation.configuration;

public class Configuration {

	/**
	 * The path where the generated RDFS file should be stored.
	 */
	private String outputPath;

	/**
	 * The serialization of the RDFS file.
	 */
	private String serialization;

	/**
	 * The path of the package or class the user wants to convert to a RDFS file.
	 */
	private String pack;

	/**
	 * The constructor which sets all the configurations.
	 * 
	 * @param path    The path where the output file should be stored.
	 * @param serial  The serialization of the file.
	 * @param pack    The path to the package or class to be converted.
	 * @param bundled If there will be only one output file or not.
	 */
	public Configuration(String path, String serial, String pack) {
		this.outputPath = path;
		this.serialization = serial;
		this.setPack(pack);
	}

	/**
	 * Returns the output path where the file will be stored.
	 * 
	 * @return The output path where the file will be stored.
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * Returns the required serialization of the output file.
	 * 
	 * @return The required serialization of the output file.
	 */
	public String getSerialization() {
		return serialization;
	}

	/**
	 * Returns the path where the package or class to be converted is stored.
	 * 
	 * @return The path where the package or class to be converted is stored.
	 */
	public String getPack() {
		return pack;
	}

	/**
	 * Sets the path where the package or class to be converted is stored.
	 * 
	 * @param pack The path where the package or class to be converted is stored.
	 */
	public void setPack(String pack) {
		this.pack = pack;
	}

}
