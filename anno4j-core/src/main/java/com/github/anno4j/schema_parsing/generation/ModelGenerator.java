package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.Anno4j;
import com.squareup.javapoet.JavaFile;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * Created by Manu on 18/11/16.
 */
public abstract class ModelGenerator {

    private Anno4j anno4j;
    private String packagePath;

    private String namespace;
    private String prefix;

    private NamespaceContainer namespaceContainer;

    public ModelGenerator (Anno4j anno4j, String packagePath, String namespace, String prefix) {
        this.anno4j = anno4j;
        this.packagePath = packagePath;
        this.namespace = namespace;
        this.prefix = prefix;

//        this.namespaceContainer = new NamespaceContainer();
    }

    public abstract void generateModel() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException;

    public Anno4j getAnno4j() {
        return anno4j;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPrefix() {
        return prefix;
    }
}
