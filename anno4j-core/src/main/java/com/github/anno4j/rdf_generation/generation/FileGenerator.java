package com.github.anno4j.rdf_generation.generation;

import org.openrdf.repository.RepositoryException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileGenerator {

    class FileGenerationException extends Exception {

        public FileGenerationException() {}

        public FileGenerationException(Throwable cause) {
            super(cause);
        }

        public FileGenerationException(String message) { super(message); }
    }

    class InvalidOntologyException extends FileGenerationException {

        public InvalidOntologyException() {}

        public InvalidOntologyException(String message) {
            super(message);
        }
    }

    void generateFile(String path) throws FileGenerator.FileGenerationException, IOException, RepositoryException;;
}
