package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDFS;
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
        for (RDFSClazz superClazz : getSuperclazzes()) {
            // Only take owl:Thing (corresponds to ResourceObject) if there is no other superclass:
            if(!superClazz.getResourceAsString().equals(OWL.THING)) {
                // Ignore the subclass relation of the class to itself:
                if(!superClazz.equals(this)) {
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

            // Check if the property is present in any superclass:
            boolean definedInSuper = false;
            for (RDFSClazz superClazz : getSuperclazzes()) {
                // Ignore RDFS classes, because this property may be shifted from rdfs:Class or owl:Thing
                // to a root class that is actually generated:
                definedInSuper |= asBuildableClazz(superClazz).hasPropertyTransitive(property)
                                && !superClazz.getResourceAsString().startsWith(RDFS.NS) && !superClazz.getResourceAsString().startsWith(OWL.NS)
                                && !superClazz.equals(this);
            }

            // Only add the method to the type spec if it was not already defined in a superclass and it is not from special vocab:
            if(!definedInSuper && !isFromSpecialVocabulary(property)) {
                getters.add(buildable.buildGetter(this, config));
                setters.add(buildable.buildSetter(this, config));
                adders.add(buildable.buildAdder(this, config));
                removers.add(buildable.buildRemover(this, config));

                // Generate *All() methods only if cardinality is greater than one:
                Integer cardinality = buildable.getCardinality(this);
                if(cardinality == null || cardinality > 1) {
                    setters.add(buildable.buildVarArgSetter(this, config));
                    adderAll.add(buildable.buildAdderAll(this, config));
                    removerAll.add(buildable.buildRemoverAll(this, config));
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
