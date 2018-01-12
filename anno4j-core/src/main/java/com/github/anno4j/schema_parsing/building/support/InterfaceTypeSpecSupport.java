package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.owl.Restriction;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.HashSet;

/**
 * Support class implementing methods for generating a JavaPoet
 * {@link TypeSpec} of a resource object for this RDFS class.
 */
@Partial
public abstract class InterfaceTypeSpecSupport extends ClazzBuildingSupport implements BuildableRDFSClazz {

    @Override
    public TypeSpec buildTypeSpec(OntGenerationConfig config) throws RepositoryException {
        // First try to find a name for this class:
        ClassName clazzName = getJavaPoetClassName(config);

        Collection<TypeName> superClazzNames = new HashSet<>();
        for (RDFSClazz superClazz : getDirectSuperclazzes()) {
            // Ignore owl:Thing. This is handled below:
            if(!superClazz.getResourceAsString().equals(OWL.THING)) {
                // Ignore the subclass relation of the class to itself and OWL restrictions (handled by annotations):
                if(!superClazz.equals(this) && !(superClazz instanceof Restriction)) {
                    superClazzNames.add(asBuildableClazz(superClazz).getJavaPoetClassName(config));
                }
            }
        }
        // The superclass of every class is owl:Thing (omitted above):
        if(superClazzNames.isEmpty()) {
            try {
                BuildableRDFSClazz owlThing = getObjectConnection().findObject(BuildableRDFSClazz.class, new URIImpl(OWL.THING));
                superClazzNames.add(owlThing.getJavaPoetClassName(config));
            } catch (QueryEvaluationException e) {
                throw new RepositoryException(e);
            }
        }

        Collection<MethodSpec> getters = new HashSet<>();
        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> adderAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        Collection<MethodSpec> removerAll = new HashSet<>();
        for (RDFSProperty property : getOutgoingProperties()) {
            // Get as buildable object:
            BuildableRDFSProperty buildable = asBuildableProperty(property);

            // Don't generate methods for properties from OWL, RDFS...:
            if(!isFromSpecialVocabulary(property)) {
                // Only add the method to the type spec if it was not already defined in a superclass
                // and if it can have values for this class:
                Integer maxCardinality = buildable.getMaximumCardinality(this);
                if(!isDefinedInSuperclass(property) && (maxCardinality == null || maxCardinality > 0)) {

                    getters.add(buildable.buildGetter(this, config));
                    setters.add(buildable.buildSetter(this, config));
                    setters.add(buildable.buildVarArgSetter(this, config));

                    if(config.areAdderMethodsGenerated()) {
                        adders.add(buildable.buildAdder(this, config));
                    }
                    if(config.areRemoverMethodsGenerated()) {
                        removers.add(buildable.buildRemover(this, config));
                    }

                    // Generate *All() methods only if cardinality is greater than one:
                    Integer cardinality = buildable.getCardinality(this);
                    if(cardinality == null || cardinality > 1) {
                        if(config.areAdderAllMethodsGenerated()) {
                            adderAll.add(buildable.buildAdderAll(this, config));
                        }
                        if(config.areRemoverAllMethodsGenerated()) {
                            removerAll.add(buildable.buildRemoverAll(this, config));
                        }
                    }

                // Generate a new (annotated) getter if the property is restricted in this class:
                } else if (needsRedefinition(property)) {
                    getters.add(buildable.buildGetter(this, config));
                }
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
