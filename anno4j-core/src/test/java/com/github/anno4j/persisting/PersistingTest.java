package com.github.anno4j.persisting;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PersistingTest {

    private static final String CLASS_BACKUP_DIR = System.getProperty("user.home");

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

    @Test
    @Ignore
    // Ignored until fixed
    public void testPresistence() throws Exception {
        // Create an Anno4j instacne without persisting schema annotations:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN(), null, false);

        // Set own directory as preferred lookup locationf or class loader:
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();
        addClassLoaderLookup(connection, CLASS_BACKUP_DIR);

        Annotation annotation = anno4j.createObject(Annotation.class);
        assertNotNull(annotation);

        backupClassFiles(connection, CLASS_BACKUP_DIR);
    }
}
