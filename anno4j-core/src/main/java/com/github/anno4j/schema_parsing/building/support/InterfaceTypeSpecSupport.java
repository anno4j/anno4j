package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;

/**
 * Support class implementing methods for generating a JavaPoet
 * {@link TypeSpec} of a resource object for this RDFS class.
 */
@Partial
public abstract class InterfaceTypeSpecSupport extends ClazzBuildingSupport implements ExtendedRDFSClazz {

    @Override
    public TypeSpec buildTypeSpec(OntGenerationConfig config) {
        // First try to find a name for this class:
        ClassName clazzName = getJavaPoetClassName(config);

        Collection<TypeName> superClazzNames = new HashSet<>();
        for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
            superClazzNames.add(superClazz.getJavaPoetClassName(config));
        }

        Collection<MethodSpec> getters = new HashSet<>();
        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> adderAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        Collection<MethodSpec> removerAll = new HashSet<>();
        for (ExtendedRDFSProperty property : getOutgoingProperties()) {
            // Check if the property is present in any superclass:
            boolean definedInSuper = false;
            for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
                // Ignore RDFS classes, because this property may be shifted from rdfs:Class
                // to a root class that is actually generated:
                definedInSuper |= superClazz.hasPropertyTransitive(property)
                                && !superClazz.getResourceAsString().startsWith(RDFS.NS);
            }

            // Only add the method to the type spec if it was not already defined in a superclass:
            if(!definedInSuper) {
                getters.add(property.buildGetter(config));
                setters.add(property.buildSetter(config));
                adders.add(property.buildAdder(config));
                adderAll.add(property.buildAdderAll(config));
                removers.add(property.buildRemover(config));
                removerAll.add(property.buildRemoverAll(config));
            }
        }

        // JavaDoc of the class:
        CodeBlock.Builder javaDoc = CodeBlock.builder();
        if(getComments() != null && !getComments().isEmpty()) {
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if(preferredComment != null) {
                javaDoc.add(preferredComment.toString());
                javaDoc.add("\n");
            }
        }
        javaDoc.add("Generated class for $L", getResourceAsString());

        // IRI annotation of the class:
        AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                .addMember("value", "$S", getResourceAsString())
                .build();

        return TypeSpec.interfaceBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(iriAnnotation)
                .addSuperinterfaces(superClazzNames)
                .addMethods(getters)
                .addMethods(setters)
                .addMethods(adders)
                .addMethods(adderAll)
                .addMethods(removers)
                .addMethods(removerAll)
                .addJavadoc(javaDoc.build())
                .build();
    }
}
