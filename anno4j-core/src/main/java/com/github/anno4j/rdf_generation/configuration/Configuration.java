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
	 * True, if the user only wants to generate one output file from one or many
	 * input classes. False, if the user wants to generate as many output files as
	 * input classes.
	 */
	private boolean bundled;

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
	public Configuration(String path, String serial, String pack, boolean bundled) {
		this.outputPath = path;
		this.serialization = serial;
		this.setPack(pack);
		setBundled(analyseBundle(bundled));
	}

	/**
	 * Sets the boolean "bundled" the the exact value. Only if the user entered a
	 * path to a package, the boolean will keep the value the user assigned to it.
	 * If the path leads to only one class, the boolean will always be set to
	 * "false".
	 * 
	 * @param bundled The value the user set for generating one or more output
	 *                files.
	 * @return The value for generating one or more output files after analyzing the
	 *         path of the package or class.
	 */
	private boolean analyseBundle(boolean bundled) {
		if (getPack().endsWith(".")) {
			return bundled;
		} else {
			return false;
		}
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
	 * Returns if there will be only one output file or not.
	 * 
	 * @return True, if only one output file will be generated. False otherwise.
	 */
	public boolean isBundled() {
		return bundled;
	}

	/**
	 * Sets the value of the boolean "bundled".
	 * 
	 * @param bundled If the user wants to generate one output file or more.
	 */
	public void setBundled(boolean bundled) {
		this.bundled = bundled;
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
