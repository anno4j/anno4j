package com.github.anno4j.rdf_generation.generation;

import java.io.File;
import java.io.InputStream;

import com.github.anno4j.schema_parsing.building.OWLJavaFileGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfFileGenerator implements FileGenerator {

    /**
     * The logger used for printing progress.
     */
    private final Logger logger = LoggerFactory.getLogger(OWLJavaFileGenerator.class);

    public RdfFileGenerator() {
    }

    @Override
    public void addJava(InputStream javaInput, String format){
    }

    @Override
    public void addJava(String uri, String format){
    }

    @Override
    public void generateFile(File outputDirectory) {

    }
}
