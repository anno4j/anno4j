package com.github.anno4j.compiletool;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 * Represents the task of creating an object for a certain concept and thus generating the required proxies.
 */
class ProxyCompileWorker implements Runnable {

    /**
     * Used for displaying progress to the user.
     */
    public static class ProgressCallback {

        /**
         * Number of finished tasks.
         */
        private int finishedTasksCount;

        /**
         * Total number of tasks to process.
         */
        private int totalTasksCount;

        /**
         * @param totalTasksCount Total number of tasks to process.
         */
        public ProgressCallback(int totalTasksCount) {
            this.totalTasksCount = totalTasksCount;
        }

        /**
         * Displays progress information to stdout.
         * @param worker The worker that finished.
         */
        public synchronized void update(ProxyCompileWorker worker) {
            Class<?> clazz;
            try {
                clazz = worker.getProcessedClass();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            finishedTasksCount++;
            float percent = Math.round((finishedTasksCount/(float) totalTasksCount)*10000f)/100f;
            System.out.print("(" + percent + "%)");
            Iri iriAnnotation = clazz.getAnnotation(Iri.class);
            if(iriAnnotation != null) {
                System.out.println(" Built proxies for " + clazz.getName() + " (" + iriAnnotation.value() + ")");
            } else {
                System.out.println();
            }
        }
    }

    private Anno4j anno4j;

    private ClassLoader jarClassLoader;

    private String className;

    private ProgressCallback progressCallback;

    public ProxyCompileWorker(Anno4j anno4j, ClassLoader jarClassLoader, String className, ProgressCallback progressCallback) {
        this.anno4j = anno4j;
        this.jarClassLoader = jarClassLoader;
        this.className = className;
        this.progressCallback = progressCallback;
    }

    @Override
    public void run() {
        try {
            Class<?> clazz = getProcessedClass();

            Iri iriAnnotation;
            if((iriAnnotation = clazz.getAnnotation(Iri.class)) != null) {

                anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(iriAnnotation.value()));

                Transaction transaction = anno4j.createTransaction();
                transaction.begin();
                transaction.createObject(clazz, (Resource) new URIImpl("urn:anno4j:foo"));
                transaction.rollback();
            }

            if (progressCallback != null) {
                progressCallback.update(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Anno4j getAnno4j() {
        return anno4j;
    }

    public ClassLoader getJarClassLoader() {
        return jarClassLoader;
    }

    public String getClassName() {
        return className;
    }

    public ProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public Class<?> getProcessedClass() throws ClassNotFoundException {
        return jarClassLoader.loadClass(className);
    }
}
