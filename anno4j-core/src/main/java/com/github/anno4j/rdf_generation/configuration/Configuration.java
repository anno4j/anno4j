package com.github.anno4j.rdf_generation.configuration;

public class Configuration {

	private String outputPath;
	private String serialization;
	private boolean bundled;
	
	public Configuration(String path, String serial, boolean bundled){
		this.outputPath = path;
		this.serialization = serial;
		this.setBundled(bundled);
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
	
}
