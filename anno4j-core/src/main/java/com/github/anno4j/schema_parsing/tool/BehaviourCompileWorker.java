package com.github.anno4j.schema_parsing.tool;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

class BehaviourCompileWorker implements Runnable {

    public static class ProgressCallback {

    }

    private Anno4j anno4j;

    private ClassLoader jarClassLoader;

    private String className;

    private ProgressCallback progressCallback;

    public BehaviourCompileWorker(Anno4j anno4j, ClassLoader jarClassLoader, String className, ProgressCallback progressCallback) {
        this.anno4j = anno4j;
        this.jarClassLoader = jarClassLoader;
        this.className = className;
        this.progressCallback = progressCallback;
    }

    @Override
    public void run() {
        try {
            Class<?> clazz = jarClassLoader.loadClass(className);

            Iri iriAnnotation;
            if((iriAnnotation = clazz.getAnnotation(Iri.class)) != null) {

                anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(iriAnnotation.value()));

                Transaction transaction = anno4j.createTransaction();
                transaction.begin();
                transaction.createObject(clazz, (Resource) new URIImpl("urn:anno4j:foo"));
                transaction.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
