package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;

/**
 * Support class implementing methods for generating a JavaPoet
 * {@link TypeSpec} of a resource objects support class for this RDFS class.
 */
@Partial
public abstract class SupportTypeSpecSupport extends ClazzBuildingSupport implements ExtendedRDFSClazz {

    @Override
    public TypeSpec buildSupportTypeSpec(OntGenerationConfig config) {
        // First try to find a name for this class:
        ClassName interfaceName = getJavaPoetClassName(config);
        ClassName supportClassName = ClassName.get(interfaceName.packageName(), interfaceName.simpleName() + "Support");

        // Partial annotation:
        AnnotationSpec partialAnnotation = AnnotationSpec.builder(Partial.class)
                                                        .build();

        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> addersAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        for (ExtendedRDFSProperty property : getOutgoingProperties()) {
            // Check if the property is present in any superclass:
            boolean definedInSuper = false;
            for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
                definedInSuper |= superClazz.hasPropertyTransitive(property);
            }

            // Only add the method to the type spec if it was not already defined in a superclass:
            if(!definedInSuper) {
                setters.add(property.buildSetterImplementation(config));
                adders.add(property.buildAdderImplementation(config));
                addersAll.add(property.buildAdderAllImplementation(config));
                removers.add(property.buildRemoverImplementation(config));
            }
        }

        // JavaDoc of the class:
        CodeBlock.Builder javaDoc = CodeBlock.builder();
        javaDoc.add("Support class for {@link $T}", interfaceName);

        ClassName resourceObjectSupport = ClassName.get(ResourceObjectSupport.class);

        return TypeSpec.classBuilder(supportClassName)
                .addJavadoc(javaDoc.build())
                .addAnnotation(partialAnnotation)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(resourceObjectSupport)
                .addSuperinterface(interfaceName)
                .addMethods(setters)
                .addMethods(adders)
                .addMethods(addersAll)
                .addMethods(removers)
                .build();
    }
}
