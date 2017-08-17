package com.github.anno4j.schema_parsing.tool;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import org.openrdf.annotations.Iri;

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

            if(clazz.getAnnotation(Iri.class) != null) {
                Transaction transaction = anno4j.createTransaction();
                transaction.begin();
                transaction.createObject(clazz);
                transaction.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
