package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;

/**
 * Support class implementing methods for generating a JavaPoet
 * {@link TypeSpec} of a resource objects support class for this RDFS class.
 */
@Partial
public abstract class SupportTypeSpecSupport extends ClazzBuildingSupport implements BuildableRDFSClazz {

    @Override
    public TypeSpec buildSupportTypeSpec(OntGenerationConfig config) throws RepositoryException {
        // First try to find a name for this class:
        ClassName interfaceName = getJavaPoetClassName(config);
        ClassName supportClassName = ClassName.get(interfaceName.packageName(), interfaceName.simpleName() + "Support");

        // Partial annotation:
        AnnotationSpec partialAnnotation = AnnotationSpec.builder(Partial.class)
                                                        .build();

        // Generate methods:
        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> addersAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        Collection<MethodSpec> removersAll = new HashSet<>();
        for (RDFSProperty property : getOutgoingProperties()) {
            // Only add the method to the type spec if it was not already defined in a superclass (unless it requires redefinition):
            if(!isDefinedInSuperclass(property) && !isFromSpecialVocabulary(property)) {
                BuildableRDFSProperty buildable = asBuildableProperty(property);
                setters.add(buildable.buildVarArgSetterImplementation(this, config));

                if(config.areAdderMethodsGenerated()) {
                    adders.add(buildable.buildAdderImplementation(this, config));
                }
                if(config.areRemoverMethodsGenerated()) {
                    removers.add(buildable.buildRemoverImplementation(this, config));
                }

                // Generate *All() methods only if cardinality is greater than one:
                Integer cardinality = buildable.getCardinality(this);
                if(cardinality == null || cardinality > 1) {
                    if(config.areAdderAllMethodsGenerated()) {
                        addersAll.add(buildable.buildAdderAllImplementation(this, config));
                    }
                    if(config.areRemoverAllMethodsGenerated()) {
                        removersAll.add(buildable.buildRemoverAllImplementation(this, config));
                    }
                }
            }
        }

        // JavaDoc of the class:
        CodeBlock.Builder javaDoc = CodeBlock.builder();
        javaDoc.add("Support class for {@link $T}", interfaceName);

        // The superclass is SchemaSanitizingObjectSupport, so prepare it:
        ClassName resourceObjectSupport = ClassName.get(SchemaSanitizingObjectSupport.class);

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
                .addMethods(removersAll)
                .build();
    }
}
