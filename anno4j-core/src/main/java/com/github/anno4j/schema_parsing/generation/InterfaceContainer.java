package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSProperty;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Manu on 21/11/16.
 */
public class InterfaceContainer {

    private String namespace;
    private RDFSClazz clazz;
    private List<RDFSProperty> properties;
    private List<RDFSClazz> superClazzes;

    public InterfaceContainer (RDFSClazz clazz, String namespace) {
        this.namespace = namespace;
        this.clazz = clazz;
        this.properties = new LinkedList<>();
        this.superClazzes = new LinkedList<>();
    }

    public JavaFile generateInterface() {

        // Generate methods and add those to the typespec
//        List<RDFSProperty> props = this.generateMethod()

        TypeSpec typeSpec = TypeSpec.classBuilder("SomeName")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethod(main)
                .build();

        return JavaFile.builder("com.package", typeSpec).build();
    }

    public JavaFile generateSupport() {

        return null;
    }

    private List<MethodSpec> generateMethod(RDFSProperty property) {

        // TODO Simplify names, whole URI is to long!
        List<MethodSpec> methods = new LinkedList<MethodSpec>();

        MethodSpec getter = MethodSpec.methodBuilder("get" + property.getResourceAsString())
                .returns(void.class)
                .build();
        methods.add(getter);

        MethodSpec setter = MethodSpec.methodBuilder("set" + property.getResourceAsString())
                .returns(void.class)
                .build();
        methods.add(setter);

        MethodSpec add = MethodSpec.methodBuilder("add" + property.getResourceAsString())
                .returns(void.class)
                .build();
        methods.add(add);

        return methods;
    }

    public JavaFile generate() {
        return null;
    }
}
