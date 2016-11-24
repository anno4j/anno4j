package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.util.IdentifierUtil;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Container class for schema generation purposes.
 * This class is used to generate the namespace file associated with a given (to be parsed) schema.
 */
public class NamespaceContainer {

    private String namespace;
    private String prefix;
    private String packagePath;

    private List<String> clazzes;
    private List<String> properties;

    public NamespaceContainer(String namespace, String prefix, String packagePath) {
        this.namespace = namespace;
        this.prefix = prefix;
        this.packagePath = packagePath;

        this.clazzes = new LinkedList<String>();
        this.properties = new LinkedList<String>();
    }

    public JavaFile generate() {
        // Constant for namespace
        FieldSpec namespaceField = FieldSpec.builder(String.class, "NS")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", this.namespace)
                .build();

        // Constant for prefix
        FieldSpec prefixField = FieldSpec.builder(String.class, "PREFIX")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer("$S", this.prefix)
                .build();

        // Add all classes
        List<FieldSpec> fields = new LinkedList<>();

        for(String clazz : this.clazzes) {
            String trimmed = IdentifierUtil.trimNamespace(clazz);

            fields.add(FieldSpec.builder(String.class, trimmed.toUpperCase())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                    .initializer("$N + $S" , namespaceField, trimmed).build());
        }

        // Add all properties
        for(String property : this.properties) {
            String trimmed = IdentifierUtil.trimNamespace(property);

            fields.add(FieldSpec.builder(String.class, trimmed.toUpperCase())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                    .initializer("$N + $S" , namespaceField, trimmed).build());
        }

        // Define the TypeSpec
        TypeSpec typeSpec = TypeSpec.classBuilder(prefix.toUpperCase())
                .addModifiers(Modifier.PUBLIC)
                .addField(namespaceField)
                .addField(prefixField)
                .addFields(fields)
                .build();

        return JavaFile.builder(packagePath, typeSpec).build();
    }

    public void addClazz(String clazz) {
        this.clazzes.add(clazz);
    }

    public void addProperty(String property) {
        this.properties.add(property);
    }
}
