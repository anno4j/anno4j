package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.*;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.owl.ObjectProperty;
import com.github.anno4j.schema.model.owl.Restriction;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema.model.rdfs.RDFSPropertySupport;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.util.LowestCommonSuperclass;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.*;

/**
 * This support class provides functionality to generate Anno4j schema annotations (see See {@link com.github.anno4j.annotations})
 * from OWL schema information available about this property in the repository.
 */
@Partial
public abstract class PropertySchemaAnnotationSupport extends RDFSPropertySupport implements RDFSProperty {

    /**
     * Builds schema annotations representing the OWL information about this property.
     * If multiple annotations of the same type (e.g. multiple qualified {@link MinCardinality annotations})
     * can be derived, they are embedded in a container annotation (e.g. {@link MinCardinalities}.
     * @param domainClazz The class for which property mapping the annotations should be generated.
     * @param config The configuration object for Java Code generation.
     * @return Returns the schema annotations that could be derived from OWL data present in the repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    Collection<AnnotationSpec> buildSchemaAnnotations(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        Collection<AnnotationSpec> annotations = new HashSet<>();

        // Add characteristic annotations:
        annotations.addAll(buildCharacteristicAnnotations());

        // Add annotations imposing restrictions on the property declared by the given class:
        annotations.addAll(buildRestrictingAnnotations(domainClazz, config));

        // Return annotations embedded in container annotations:
        return embedAnnotations(annotations);
    }

    /**
     * Builds the property characteristic annotations from the OWL information about this property in the repository.
     * A annotation is said to be characteristic if it is independent of the class that declares the property.
     * @return Returns the characteristic schema annotations for this property.
     * The result may contain multiple annotations of the same type.
     * @throws RepositoryException Thrown if an error occurs while information about the property is queried from the
     * repository.
     */
    private Collection<AnnotationSpec> buildCharacteristicAnnotations() throws RepositoryException {
        Collection<AnnotationSpec> annotations = new HashSet<>();

        if(hasType(OWL.FUNCTIONAL_PROPERTY) && hasType(OWL.INVERSE_FUNCTIONAL_PROPERTY)) {
            annotations.add(AnnotationSpec.builder(Bijective.class).build());
        } else if(hasType(OWL.FUNCTIONAL_PROPERTY)) {
            annotations.add(AnnotationSpec.builder(Functional.class).build());
        } else if(hasType(OWL.INVERSE_FUNCTIONAL_PROPERTY)) {
            annotations.add(AnnotationSpec.builder(InverseFunctional.class).build());
        }

        if(hasType(OWL.SYMMETRIC_PROPERTY)) {
            annotations.add(AnnotationSpec.builder(Symmetric.class).build());
        }
        if(hasType(OWL.SYMMETRIC_PROPERTY)) {
            annotations.add(AnnotationSpec.builder(Transitive.class).build());
        }

        // Ignore sub-property relations to special OWL properties and the property itself:
        Collection<RDFSProperty> superProperties = new HashSet<>();
        for (RDFSProperty superProperty : getSuperproperties()) {
            if(!superProperty.getResourceAsString().startsWith(OWL.NS) && !superProperty.equals(this)) {
                superProperties.add(superProperty);
            }
        }
        if(!superProperties.isEmpty()) {
            annotations.add(buildIriArrayAnnotation(SubPropertyOf.class, superProperties));
        }

        if(hasType(OWL.OBJECT_PROPERTY)) {
            // Add the @InverseOf annotation only if there are inverse properties.
            // So get this property as a object property to check:
            ObjectProperty objectProperty;
            try {
                objectProperty = getObjectConnection().findObject(ObjectProperty.class, getResource());
            } catch (QueryEvaluationException e) {
                throw new RepositoryException(e);
            }
            if(!objectProperty.getInverseOf().isEmpty()) {
                annotations.add(buildIriArrayAnnotation(InverseOf.class, objectProperty.getInverseOf()));
            }
        }

        return annotations;
    }

    /**
     * Builds the property annotations that impose a restriction on the property in the context of a certain class
     * (cf. {@code owl:Restriction}).
     * @param domainClazz The class for which method annotations should be generated.
     * @param config The configuration object for Java Code generation.
     * @return Returns the annotations that represent the class dependent schema information about the property.
     * The result may contain multiple annotations of the same type.
     */
    private Collection<AnnotationSpec> buildRestrictingAnnotations(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        Collection<AnnotationSpec> annotations = new HashSet<>();

        for (Restriction restriction : queryRestrictions(domainClazz.getResourceAsString())) {
            if(!restriction.getAllValuesFrom().isEmpty()) {
                annotations.add(buildTypeArrayAnnotation(AllValuesFrom.class, restriction.getAllValuesFrom(), config));
            }
            if(!restriction.getSomeValuesFrom().isEmpty()) {
                annotations.add(buildTypeArrayAnnotation(SomeValuesFrom.class, restriction.getSomeValuesFrom(), config));
            }
            if(!restriction.getMinCardinality().isEmpty() || !restriction.getMinQualifiedCardinality().isEmpty()) {
                int minCardinality;
                if(!restriction.getMinCardinality().isEmpty()) {
                    minCardinality = restriction.getMinCardinality().iterator().next().intValue();
                } else {
                    minCardinality = restriction.getMinQualifiedCardinality().iterator().next().intValue();
                }
                annotations.add(buildQualifiedAnnotation(MinCardinality.class, restriction, minCardinality, config));
            }
            if(!restriction.getMaxCardinality().isEmpty() || !restriction.getMaxQualifiedCardinality().isEmpty()) {
                int maxCardinality;
                if(!restriction.getMaxCardinality().isEmpty()) {
                    maxCardinality = restriction.getMaxCardinality().iterator().next().intValue();
                } else {
                    maxCardinality = restriction.getMaxQualifiedCardinality().iterator().next().intValue();
                }
                annotations.add(buildQualifiedAnnotation(MaxCardinality.class, restriction, maxCardinality, config));
            }
            if(!restriction.getCardinality().isEmpty() || !restriction.getQualifiedCardinality().isEmpty()) {
                int cardinality;
                if(!restriction.getCardinality().isEmpty()) {
                    cardinality = restriction.getCardinality().iterator().next().intValue();
                } else {
                    cardinality = restriction.getQualifiedCardinality().iterator().next().intValue();
                }
                annotations.add(buildQualifiedAnnotation(Cardinality.class, restriction, cardinality, config));
            }
        }

        return annotations;
    }

    /**
     * Queries the restrictions about this property declared for the given class.
     * @param domainIri The IRI of the class in which context to query for restrictions.
     * @return Returns the restrictions for this property at the given class.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private Collection<Restriction> queryRestrictions(String domainIri) throws RepositoryException {
        try {
            return getObjectConnection().prepareObjectQuery(

                    "SELECT ?r {" +
                    "   ?r a owl:Restriction ." +
                    "   <" + domainIri + "> rdfs:subClassOf+ ?r . " +
                    "   ?r owl:onProperty <" + getResourceAsString() + "> ." +
                    "}"

            ).evaluate(Restriction.class).asSet();

        } catch (QueryEvaluationException | MalformedQueryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Embeds the given annotations in container annotations if necessary.
     * If the given collection contains multiple annotations of the same type then those are "wrapped"
     * in the corresponding container annotation (e.g. multiple {@link MinCardinality} annotations are embedded
     * in a single {@link MinCardinalities}).
     * Annotations which type occurs only once are not embedded even if they would be embeddable.
     * @param annotations The annotations to embed if necessary.
     * @return Returns the collection of embedded annotations. If multiple annotations of a non-embeddable type
     * they are considered equivalent and an arbitrary annotation is picked.
     */
    private Collection<AnnotationSpec> embedAnnotations(Collection<AnnotationSpec> annotations) {
        // Map all annotations by their type:
        Map<TypeName, List<AnnotationSpec>> annotationsByType = new HashMap<>();
        for (AnnotationSpec annotation : annotations) {
            // Initialize bucket if not yet exists:
            if(!annotationsByType.containsKey(annotation.type)) {
                annotationsByType.put(annotation.type, new LinkedList<AnnotationSpec>());
            }

            annotationsByType.get(annotation.type).add(annotation);
        }

        // Do the embedding:
        Collection<AnnotationSpec> embedded = new HashSet<>();
        for (TypeName annotationType : annotationsByType.keySet()) {
            // Embed if multiple values of this type exist:
            if(annotationsByType.get(annotationType).size() > 1) {
                Collection<AnnotationSpec> values = annotationsByType.get(annotationType);

                AnnotationSpec container = null;
                if(annotationType.equals(ClassName.get(MinCardinality.class))) {
                    embedded.add(buildContainer(MinCardinalities.class, values));
                } else if(annotationType.equals(ClassName.get(MaxCardinality.class))) {
                    embedded.add(buildContainer(MaxCardinalities.class, values));
                } else if(annotationType.equals(ClassName.get(Cardinality.class))) {
                    embedded.add(buildContainer(Cardinalities.class, values));
                } else {
                    // Type not embeddable, only add first one (equivalence assumption):
                    embedded.add(annotationsByType.get(annotationType).get(0));
                }

            } else {
                // If there's only a single annotation of this type, no embedding necessary:
                embedded.addAll(annotationsByType.get(annotationType));
            }
        }

        return embedded;
    }

    /**
     * Returns whether the resource is of a certain type given by its IRI.
     * @param typeIri The IRI of the type to check for.
     * @return Returns true iff the resource is of the above type.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private boolean hasType(String typeIri) throws RepositoryException {
        try {
            ObjectConnection connection = getObjectConnection();
            return connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                    "ASK {" +
                            "   <" + getResourceAsString() + "> a <" + typeIri + "> . " +
                            "}"
            ).evaluate();

        } catch (QueryEvaluationException | MalformedQueryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Builds an annotation which {@code value} is an array of the IRIs (as strings) of the given resources.
     * @param annotationType The type of the annotation to generate.
     * @param resources The resources which IRIs should be part of the array.
     * @return The JavaPoet annotation specification of the above annotation.
     */
    private AnnotationSpec buildIriArrayAnnotation(Class<?> annotationType, Collection<? extends ResourceObject> resources) {
        CodeBlock.Builder inner = CodeBlock.builder().add("{");
        Iterator<? extends ResourceObject> resourceIter = resources.iterator();
        while (resourceIter.hasNext()) {
            inner.add("$S", resourceIter.next().getResourceAsString());

            if(resourceIter.hasNext()) {
                inner.add(", ");
            }
        }
        inner.add("}");

        return AnnotationSpec.builder(annotationType)
                             .addMember("value", inner.build())
                             .build();
    }

    /**
     * Returns the most specific common superclass of all classes
     * defined as the domain of this property.
     *
     * @return The most specific common superclass.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    BuildableRDFSClazz findSingleDomainClazz() throws RepositoryException {
        Collection<BuildableRDFSClazz> domains = new HashSet<>();
        for (RDFSClazz range : getDomains()) {
            domains.add((BuildableRDFSClazz) range);
        }
        return LowestCommonSuperclass.getLowestCommonSuperclass(domains);
    }

    /**
     * Constructs a container annotation of the given type and content.
     * @param containerType The type of the container annotation.
     * @param content The annotations to be contained.
     * @return Returns the container annotation specification.
     */
    private static AnnotationSpec buildContainer(Class<?> containerType, Collection<AnnotationSpec> content) {
        // Build content of container annotation as string:
        CodeBlock.Builder inner = CodeBlock.builder().add("{");

        Iterator<AnnotationSpec> contentIterator = content.iterator();
        while (contentIterator.hasNext()) {
            inner.add("$L", contentIterator.next());

            if(contentIterator.hasNext()) {
                inner.add(", ");
            }
        }
        inner.add("}");

        return AnnotationSpec.builder(containerType)
                            .addMember("value", inner.build())
                            .build();
    }

    /**
     * Generates an annotation which {@code value} is an array of the Java classes ({@link Class}) generated for
     * the given RDFS classes, e.g. {@code MyAnnotation({MyClazz1.class, MyClazz2.class, ...})}.
     * @param annotationType The type of the annotation to generate.
     * @param types The RDFS classes which corresponding Java classes should be part of the array.
     * @param config The configuration used for generating Java classes.
     * @return Returns the JavaPoet specification of the above annotation.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private AnnotationSpec buildTypeArrayAnnotation(Class<?> annotationType, Collection<? extends RDFSClazz> types, OntGenerationConfig config) throws RepositoryException {
        // Build inner content of the annotation:
        CodeBlock.Builder inner = CodeBlock.builder();
        inner.add("{");
        Iterator<? extends RDFSClazz> typeIterator = types.iterator();

        while(typeIterator.hasNext()) {
            inner.add("$T.class", getJavaClassName(typeIterator.next(), config));

            if(typeIterator.hasNext()) {
                inner.add(", ");
            }
        }

        inner.add("}");

        return AnnotationSpec.builder(annotationType)
                             .addMember("value", inner.build())
                             .build();
    }

    /**
     * Creates a schema annotation which imposes a constraint relative to a certain OWL class,
     * i.e. the annotation has a {@code onClass} parameter, which will receive the type of the
     * {@link Restriction#getOnClazz()} member.
     * The latter annotation parameter is omitted if the given OWL restriction does not impose
     * a constraint regarding a certain class ({@code owl:onClass}).
     * See {@link MinCardinality}, {@link MaxCardinality}, {@link Cardinality}.
     * @param annotationType The type of the annotation to create.
     * @param restriction The restriction which may impose a constraint regarding a certain class.
     * @param value The value to set for the created annotations {@code value} parameter.
     * @param config A configuration object specifying how Java class names are created for OWL classes.
     * @param <T> The type of the value.
     * @return Returns the created JavaPoet annotation specification.
     * @throws RepositoryException Thrown if an error occurs while querying required information from the repository.
     */
    private <T> AnnotationSpec buildQualifiedAnnotation(Class<?> annotationType, Restriction restriction, T value, OntGenerationConfig config) throws RepositoryException {
        AnnotationSpec.Builder annotation = AnnotationSpec.builder(annotationType)
                .addMember("value", value.toString());

        if(!restriction.getOnClazz().isEmpty()) {
            OWLClazz onClazz = restriction.getOnClazz().iterator().next();
            annotation.addMember("onClass", "$T.class", getJavaClassName(onClazz, config));
        }

        return annotation.build();
    }

    /**
     * Returns the Java class name for the given class according to the passed configuration.
     * @param clazz The class for which a Java class name should be found.
     * @param config The configuration specifying how the class name is determined.
     * @return Returns the JavaPoet specification of the classes class name.
     * @throws RepositoryException Thrown if an error occurs while querying necessary information about the class
     * from the repository.
     */
    private ClassName getJavaClassName(RDFSClazz clazz, OntGenerationConfig config) throws RepositoryException {
        BuildableRDFSClazz buildable;
        try {
            buildable = getObjectConnection().findObject(BuildableRDFSClazz.class, clazz.getResource());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
        return buildable.getJavaPoetClassName(config);
    }
}
