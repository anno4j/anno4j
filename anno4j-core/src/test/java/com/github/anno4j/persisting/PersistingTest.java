package com.github.anno4j.persisting;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the customized class loader of Alibaba.
 */
public class PersistingTest {

    private static final String USER_HOME = System.getProperty("user.home") + File.separator;

    private void addClassLoaderLookup(ObjectConnection connection, String path) throws MalformedURLException {
        ClassFactory classFactory = connection.getObjectFactory().getResolver().getClassFactory();
        classFactory.appendClassLoader(new URLClassLoader(new URL[]{ new File(path).toURI().toURL()}));
    }

    private void backupClassFiles(ObjectConnection connection, String path) throws IOException {
        ClassFactory classFactory = connection.getObjectFactory().getResolver().getClassFactory();
        File targetDir = new File(path);
        targetDir.mkdirs();

        FileUtils.copyDirectory(classFactory.getOutput(), targetDir);
    }

    private void removeDirectory(File directory) throws IOException {
        FileUtils.deleteDirectory(directory);
    }

    @Test
    public void testPresistence() throws Exception {
        // Clean up any old backup directories:
        removeDirectory(new File(USER_HOME + "object"));

        // Create an Anno4j instacne without persisting schema annotations:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN(), null, false);

        // Set own directory as preferred lookup location for class loader:
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();
        addClassLoaderLookup(connection, USER_HOME + "object");

        // Generate bytecode for Annotation:
        Annotation annotation = anno4j.createObject(Annotation.class);
        assertNotNull(annotation);

        // Backup and delete bytecode:
        backupClassFiles(connection, USER_HOME);
        removeDirectory(connection.getObjectFactory().getResolver().getClassFactory().getOutput());

        // Create Annotation again. Should be loaded from backed up directory:
        annotation = anno4j.createObject(Annotation.class);
        assertNotNull(annotation);

        // Clean up:
        removeDirectory(new File(USER_HOME + "object"));
    }
}
