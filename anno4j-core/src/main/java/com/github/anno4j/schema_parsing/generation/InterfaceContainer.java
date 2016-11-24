package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Selector;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSProperty;
import com.github.anno4j.util.IdentifierUtil;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Manu on 21/11/16.
 */
public class InterfaceContainer {

    private String packagePath;
    private String clazz;
    private List<RDFSProperty> properties;
    private List<String> superClazzes;

    public InterfaceContainer (String clazz, String packagePath) {
        this.packagePath = packagePath;
        this.clazz = clazz;
        this.properties = new LinkedList<>();
        this.superClazzes = new LinkedList<>();
    }

    public JavaFile generateInterface() {
        // Generate methods and add those to the typespec
        List<MethodSpec> methods = new LinkedList<>();
        for(RDFSProperty property : properties) {
            methods.addAll(this.generateMethods(property));
        }

        AnnotationSpec annotationIri = AnnotationSpec.builder(Iri.class)
                .addMember("value", "$S", clazz)
                .build();

        List<TypeName> typeNames = new LinkedList<>();
        for(String superClazz : superClazzes) {
            ClassName clazzName = ClassName.get(packagePath, IdentifierUtil.trimNamespace(superClazz));
            typeNames.add(clazzName);
        }

        TypeSpec typeSpec = TypeSpec.interfaceBuilder(IdentifierUtil.trimNamespace(this.clazz))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterfaces(typeNames)
                .addAnnotation(annotationIri)
                .addMethods(methods)
                .build();

        return JavaFile.builder(this.packagePath, typeSpec).build();
    }

    public JavaFile generateSupport() {
        List<MethodSpec> methods = new LinkedList<>();

        ClassName hashSet = ClassName.get("java.util", "HashSet");

        for(RDFSProperty property : properties) {
            String trimmedPropertyName = IdentifierUtil.trimNamespace(property.getResourceAsString());
            ClassName range = ClassName.get(packagePath, IdentifierUtil.trimNamespace(property.getRange().getResourceAsString()));
            TypeName hashSetWithRange = ParameterizedTypeName.get(hashSet, range);

            MethodSpec methodSpec = MethodSpec.methodBuilder("add" + trimmedPropertyName)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(range, "element")
                    .addStatement("$T elements = new $T<>()", hashSetWithRange, hashSet)
                    .beginControlFlow("if(this.get" + trimmedPropertyName + "() != null)")
                    .addStatement("elements.addAll(this.get" + trimmedPropertyName + "())")
                    .endControlFlow()
                    .addStatement("elements.add(element)")
                    .addStatement("this.set" + trimmedPropertyName + "(elements)")
                    .build();

            methods.add(methodSpec);
        }

        List<TypeName> typeNames = new LinkedList<>();
        for(String superClazz : superClazzes) {
            ClassName clazzName = ClassName.get(packagePath, IdentifierUtil.trimNamespace(superClazz) + "Support");
            typeNames.add(clazzName);
        }

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(IdentifierUtil.trimNamespace(this.clazz) + "Support")
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .addAnnotation(Partial.class)
                .addSuperinterface(ClassName.get(packagePath, IdentifierUtil.trimNamespace(clazz)))
                .addMethods(methods);

        if(!typeNames.isEmpty()) {
            typeSpecBuilder.superclass(typeNames.get(0));
        }

        TypeSpec typeSpec = typeSpecBuilder.build();

        return JavaFile.builder(this.packagePath, typeSpec).build();
    }

    private List<MethodSpec> generateMethods(RDFSProperty property) {
        List<MethodSpec> methods = new LinkedList<MethodSpec>();

        String trimmedPropertyName = IdentifierUtil.trimNamespace(property.getResourceAsString());

        // Create classes that are needed for the getters and setters
        ClassName range = ClassName.get(packagePath, IdentifierUtil.trimNamespace(property.getRange().getResourceAsString()));
        ClassName set = ClassName.get("java.util", "Set");
        TypeName setOfRanges = ParameterizedTypeName.get(set, range);

        AnnotationSpec annotationIri = AnnotationSpec.builder(Iri.class)
                .addMember("value", "$S", property.getResourceAsString())
                .build();

        MethodSpec getter = MethodSpec.methodBuilder("get" + trimmedPropertyName)
                .returns(setOfRanges)
                .addAnnotation(annotationIri)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();
        methods.add(getter);

        MethodSpec setter = MethodSpec.methodBuilder("set" + trimmedPropertyName)
                .returns(void.class)
                .addAnnotation(annotationIri)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(setOfRanges, "values")
                .build();
        methods.add(setter);

        MethodSpec add = MethodSpec.methodBuilder("add" + trimmedPropertyName)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(range, "value")
                .build();
        methods.add(add);

        return methods;
    }

    public void addProperty(RDFSProperty property) {
        this.properties.add(property);
    }

    public void addSuperClazz(String superClazz) {
        this.superClazzes.add(superClazz);
    }
}
