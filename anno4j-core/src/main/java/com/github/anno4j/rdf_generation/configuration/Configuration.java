package com.github.anno4j.rdf_generation.configuration;

public class Configuration {

	private String outputPath;
	private String serialization;
	private boolean bundled;
	private boolean greaterOne;
	private String pack;

	public Configuration(String path, String serial, String pack, boolean bundled) {
		this.outputPath = path;
		this.serialization = serial;
		this.setPack(pack);
		setBundled(analyseBundle(bundled));
	}

	private boolean analyseBundle(boolean bundled) {
		if (getPack().endsWith(".")) {
			return bundled;
		} else {
			return false; // wenn false zurück gegeben wird dann entweder 1 oder n mal einzeldurchlauf,
							// sonst gebündeltes DOkument
		}
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String savePath) {
		this.outputPath = savePath;
	}

	public String getSerialization() {
		return serialization;
	}

	public void setSerialization(String serialization) {
		this.serialization = serialization;
	}

	public boolean isBundled() {
		return bundled;
	}

	public void setBundled(boolean bundled) {
		this.bundled = bundled;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public boolean isGreaterOne() {
		return greaterOne;
	}

	public void setGreaterOne(boolean greaterOne) {
		this.greaterOne = greaterOne;
	}

}
