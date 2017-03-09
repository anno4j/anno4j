package com.github.anno4j.rdfs_parser.generation;

import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.validation.ValidatorChain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by fischmat on 08.03.17.
 */
public class RDFSJavaFileGeneratorTest {

    private static JavaSourceFileLoader loader;

    private static File outputDir;

    private static File workingDir;

    @Before
    public void setUp() throws Exception {
        File dependencyJar = findDependencyJar();
        if(dependencyJar == null) {
            File anno4jRootPom = new File(System.getProperty("user.dir") + "/../pom.xml");
            assertTrue(anno4jRootPom.exists());
            SystemCommand.runCommand("mvn -f " + anno4jRootPom.getAbsolutePath() + " -Dmaven.test.skip=true package assembly:single");
        }

        loader = new JavaSourceFileLoader();
        dependencyJar = findDependencyJar();
        assertNotNull(dependencyJar);
        loader.addDependency(dependencyJar.getAbsolutePath());

        outputDir = new File(getClass().getSimpleName() + "_output_" + System.currentTimeMillis());
        workingDir = new File(getClass().getSimpleName() + "_working_" + System.currentTimeMillis());
    }

    private static File findDependencyJar() {
        File target = new File("target");
        File[] subFiles = target.listFiles();
        if(subFiles != null) {
            for (File subFile : subFiles) {
                if(subFile.getName().matches("anno4j-core-(.*)-jar-with-dependencies.jar")) {
                    return subFile;
                }
            }
        }
        return null;
    }

    private static void deleteRecursive(File dir) {
        File[] subFiles = dir.listFiles();
        if(subFiles != null) {
            for (File subFile : subFiles) {
                if(subFile.isFile()) {
                    subFile.delete();
                } else {
                    deleteRecursive(subFile);
                }
            }
        }
        dir.delete();
    }

    @After
    public void tearDown() throws Exception {
        deleteRecursive(outputDir);
        deleteRecursive(workingDir);
    }

    @Test
    public void generateJavaFiles() throws Exception {
        URL vehicleOntUrl = getClass().getClassLoader().getResource("vehicle.rdf.xml");
        InputStream vehicleOntStream = new FileInputStream(vehicleOntUrl.getFile());

        OntGenerationConfig config = new OntGenerationConfig();
        config.setIdentifierLanguagePreference(new String[] {"en"});
        config.setJavaDocLanguagePreference(new String[] {"en"});
        config.setValidators(ValidatorChain.getRDFSDefault());

        JavaFileGenerator generator = new RDFSJavaFileGenerator();
        generator.addRDF(vehicleOntStream, "http://example.de/ont#");

        generator.generateJavaFiles(config, outputDir);

        Map<String, Class<?>> loadedClazzNames = loader.compileAndLoad(outputDir, workingDir);

        assertNotNull(loadedClazzNames);
        assertTrue(loadedClazzNames.containsKey("de.example.Vehicle"));
        assertTrue(loadedClazzNames.containsKey("de.example.Car"));
        assertTrue(loadedClazzNames.containsKey("de.example.Truck"));
        assertTrue(loadedClazzNames.containsKey("de.example.Home"));

        loader.addDependency(workingDir.getAbsolutePath());
        File runtimeTestsDir = new File(getClass().getClassLoader().getResource("runtime_tests").getFile());
        loadedClazzNames = loader.compileAndLoad(runtimeTestsDir, workingDir);
        assertTrue(loadedClazzNames.size() > 1);
        assertTrue(loadedClazzNames.containsKey("runtime.VehicleOntologyRuntimeTest"));
        assertTrue(loadedClazzNames.containsKey("runtime.VehicleSupportImpl"));

        Class<?> runtimeTestClass = loadedClazzNames.get("runtime.VehicleOntologyRuntimeTest");
        Object runtimeTest = runtimeTestClass.newInstance();
        Method testMethod = runtimeTestClass.getDeclaredMethod("run");
        testMethod.invoke(runtimeTest);
    }

}