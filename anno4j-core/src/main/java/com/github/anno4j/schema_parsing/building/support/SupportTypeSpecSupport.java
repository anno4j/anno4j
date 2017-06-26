package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 * Support class implementing methods for generating a JavaPoet
 * {@link TypeSpec} of a resource objects support class for this RDFS class.
 */
@Partial
public abstract class SupportTypeSpecSupport extends ClazzBuildingSupport implements BuildableRDFSClazz {

    /**
     * Checks if <code>property</code> is an outgoing property of any (transitive) superclass
     * of this class, which is not part of the RDFS specification (i.e. has the {@link RDFS#NS namespace}).
     * @param property The property to check for.
     * @return Returns true if and only if <code>property</code> is not an outgoing property of any transitive non-RDFS
     * superclass.
     */
    private boolean isDefinedInSuperclass(RDFSProperty property) throws RepositoryException {
        // Check if the property is present in any (non RDFS) superclass:
        boolean definedInSuper = false;
        for (RDFSClazz superClazz : getSuperclazzes()) {
            definedInSuper |= asBuildableClazz(superClazz).hasPropertyTransitive(property)
                    && !superClazz.getResourceAsString().startsWith(RDFS.NS) && !superClazz.getResourceAsString().startsWith(OWL.NS)
                    && !superClazz.equals(this);
        }
        return definedInSuper;
    }

    @Override
    public TypeSpec buildSupportTypeSpec(OntGenerationConfig config) throws RepositoryException {
        // First try to find a name for this class:
        ClassName interfaceName = getJavaPoetClassName(config);
        ClassName supportClassName = ClassName.get(interfaceName.packageName(), interfaceName.simpleName() + "Support");

        // Partial annotation:
        AnnotationSpec partialAnnotation = AnnotationSpec.builder(Partial.class)
                                                        .build();

        // Generate @Iri annotated fields:
        Collection<FieldSpec> fields = new HashSet<>();
        for (RDFSProperty property : getOutgoingProperties()) {
            // Only generate a field for this property if its not defined in a non-RDFS superclass:
            if(!isDefinedInSuperclass(property) && !isFromSpecialVocabulary(property)) {
                fields.add(asBuildableProperty(property).buildAnnotatedField(this, config));
            }
        }

        // Generate methods:
        Collection<MethodSpec> getters = new HashSet<>();
        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> addersAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        Collection<MethodSpec> removersAll = new HashSet<>();
        for (RDFSProperty property : getOutgoingProperties()) {
            // Only add the method to the type spec if it was not already defined in a superclass:
            if(!isDefinedInSuperclass(property) && !isFromSpecialVocabulary(property)) {
                BuildableRDFSProperty buildable = asBuildableProperty(property);

                getters.add(buildable.buildGetterImplementation(this, config));
                setters.add(buildable.buildSetterImplementation(this, config));
                adders.add(buildable.buildAdderImplementation(this, config));
                removers.add(buildable.buildRemoverImplementation(this, config));

                // Generate *All() methods only if cardinality is greater than one:
                Integer cardinality = buildable.getCardinality(this);
                if(cardinality == null || cardinality > 1) {
                    setters.add(buildable.buildVarArgSetterImplementation(this, config));
                    addersAll.add(buildable.buildAdderAllImplementation(this, config));
                    removersAll.add(buildable.buildRemoverAllImplementation(this, config));
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
                .addFields(fields)
                .addMethods(getters)
                .addMethods(setters)
                .addMethods(adders)
                .addMethods(addersAll)
                .addMethods(removers)
                .addMethods(removersAll)
                .build();
    }
}
