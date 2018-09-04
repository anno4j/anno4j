package com.github.anno4j.rdf_generation;

import java.io.File;
import com.github.anno4j.rdf_generation.generation.FileGenerator;
import com.github.anno4j.rdf_generation.generation.RdfFileGenerator;

public class MainClass {

    public static void main(String[] args){
        String url = "dfasdkfj";
        FileGenerator generator = new RdfFileGenerator();
        generator.addJava(url, "rdf/xml");
        File file = new File(url);

        try {
            generator.generateFile(file);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
