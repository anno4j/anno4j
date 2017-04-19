package com.github.anno4j.schema;

import arq.iri;
import com.github.anno4j.annotations.*;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.owl.Restriction;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.google.common.collect.Sets;
import org.openrdf.annotations.Iri;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectQuery;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Parses schema annotations and persists the corresponding information
 * in OWL RDF to a connected triplestore.
 */
public class OWLSchemaPersistingManager extends SchemaPersistingManager {

    private static final String QUERY_PREFIX = "PREFIX owl: <" + OWL.NS + "> PREFIX rdfs: <" + RDFS.NS + "> ";

    /**
     * @param connection Connection to the triplestore that should receive schema information.
     */
    public OWLSchemaPersistingManager(ObjectConnection connection) {
        super(connection);
    }

    /**
     * Persists the schema information implied by schema annotations to the default graph of the connected triplestore.
     * Performs a validation that the schema annotations are consistent.
     * @param types The types which methods and field should be scanned for schema information.
     * @throws RepositoryException Thrown if an error occurs while persisting schema information.
     * @throws InconsistentAnnotationException Thrown if the schema annotations are inconsistent.
     * @throws ContradictorySchemaException Thrown if the schema information imposed by annotations contradicts with
     * schema information that is already present in the connected triplestore.
     */
    public void persistSchema(Reflections types) throws RepositoryException, InconsistentAnnotationException, ContradictorySchemaException {
        Collection<AccessibleObject> iriAnnotatedObjects = new HashSet<>();
        // Add methods with @Iri annotation:
        iriAnnotatedObjects.addAll(types.getMethodsAnnotatedWith(Iri.class));
        // Add fields with @Iri annotation:
        iriAnnotatedObjects.addAll(types.getFieldsAnnotatedWith(Iri.class));

        // Check whether schema annotations contradict each other:
        checkSchemaAnnotationConsistency(iriAnnotatedObjects);

        // Check whether schema annotations contradict information that is already in the triplestore:
        validateAgainstExistingSchema(iriAnnotatedObjects);

        // Persist the schema information to the triplestore:
        persistPropertyCharacteristics(iriAnnotatedObjects);
        persistPropertyRestrictions(iriAnnotatedObjects);
    }

    /**
     * Persists OWL schema information about property characteristics to the default graph of the connected
     * triplestore.
     * Thus the following schema annotations are handled by this method:
     * <ul>
     *     <li>{@link Functional}</li>
     *     <li>{@link InverseFunctional}</li>
     *     <li>{@link Symmetric}</li>
     *     <li>{@link Transitive}</li>
     * </ul>
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistPropertyCharacteristics(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        try {
            persistFunctional(annotatedObjects);
            persistInverseFunctional(annotatedObjects);
            persistSymmetric(annotatedObjects);
            persistTransitive(annotatedObjects);
            persistSubPropertyOf(annotatedObjects);
            persistInverseOf(annotatedObjects);
        } catch (UpdateExecutionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Persists OWL schema information about class depending property constraints to the default graph of the connected
     * triplestore.
     * Thus the following schema annotations are handled by this method:
     * <ul>
     *     <li>{@link AllValuesFrom}</li>
     *     <li>{@link SomeValuesFrom}</li>
     *     <li>{@link MinCardinality}</li>
     *     <li>{@link MaxCardinality}</li>
     * </ul>
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistPropertyRestrictions(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        persistAllValuesFrom(annotatedObjects);
        persistSomeValuesFrom(annotatedObjects);
        persistMinCardinality(annotatedObjects);
        persistMaxCardinality(annotatedObjects);
        persistCardinality(annotatedObjects);
    }

    /**
     * Checks whether the schema annotation provided through annotations (at <code>annotatedObjects</code>)
     * contradicts the schema information that is already present in the default graph of the connected triplestore.
     * @param annotatedObjects Methods and fields that have the {@link Iri} annotation and (optional) schema annotations.
     * @throws ContradictorySchemaException Thrown if the schema information imposed by annotations contradicts
     * the schema information that is already present in the connected triplestore.
     * @throws RepositoryException Thrown if an error occurs while querying the connected triplestore.
     */
    private void validateAgainstExistingSchema(Collection<AccessibleObject> annotatedObjects) throws ContradictorySchemaException, RepositoryException {

        // Check owl:allValuesFrom:
        for(AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, AllValuesFrom.class)) {
            AllValuesFrom allValuesFrom = object.getAnnotation(AllValuesFrom.class);
            Set<String> annotatedTargets = getIrisFromObjects(Sets.<AnnotatedElement>newHashSet(allValuesFrom.value()));

            validateRestriction(object, OWL.ALL_VALUES_FROM, annotatedTargets);
        }

        // Check owl:someValuesFrom:
        for(AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, SomeValuesFrom.class)) {
            SomeValuesFrom someValuesFrom = object.getAnnotation(SomeValuesFrom.class);
            Set<String> annotatedTargets = getIrisFromObjects(Sets.<AnnotatedElement>newHashSet(someValuesFrom.value()));

            validateRestriction(object, OWL.SOME_VALUES_FROM, annotatedTargets);
        }

        // Check owl:minCardinality:
        for(AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, MinCardinality.class)) {
            MinCardinality minCardinality = object.getAnnotation(MinCardinality.class);

            validateRestriction(object, OWL.MIN_CARDINALITY, Sets.<Integer>newHashSet(minCardinality.value()));
            validateRestriction(object, OWL.CARDINALITY, Sets.<Integer>newHashSet(minCardinality.value()));
        }

        // Check owl:maxCardinality:
        for(AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, MaxCardinality.class)) {
            MaxCardinality maxCardinality = object.getAnnotation(MaxCardinality.class);

            validateRestriction(object, OWL.MAX_CARDINALITY, Sets.<Integer>newHashSet(maxCardinality.value()));
            validateRestriction(object, OWL.CARDINALITY, Sets.<Integer>newHashSet(maxCardinality.value()));
        }
    }

    /**
     * Checks whether the schema annotations are consistent.
     * Validation is performed per property IRI for annotations imposing property characteristics
     * and per property and declaring class for annotations imposing restrictions on properties by class.
     * @param annotatedObjects The {@link Iri} annotated methods and fields to check.
     * @throws InconsistentAnnotationException Thrown if the annotations are found to be inconsistent.
     */
    private void checkSchemaAnnotationConsistency(Collection<AccessibleObject> annotatedObjects) throws InconsistentAnnotationException {
        // Cardinalities must be non-negative:
        for (AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, MinCardinality.class)) {
            Iri iri = object.getAnnotation(Iri.class);
            MinCardinality cardinality = object.getAnnotation(MinCardinality.class);

            if(cardinality.value() < 0) {
                throw new InconsistentAnnotationException("Minimum cardinality of property " + iri.value() +
                        " in " + getDeclaringJavaClazz(object).getName() + " must be non-negative.");
            }
        }
        for (AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, MaxCardinality.class)) {
            Iri iri = object.getAnnotation(Iri.class);
            MaxCardinality maxCardinality = object.getAnnotation(MaxCardinality.class);

            if(maxCardinality.value() < 0) {
                throw new InconsistentAnnotationException("Maximum cardinality of property " + iri.value() +
                        " in " + getDeclaringJavaClazz(object).getName() + " must be non-negative.");
            }

            // The maximum cardinality must also not be less than the minimum cardinality:
            if(object.isAnnotationPresent(MinCardinality.class)) {
                MinCardinality minCardinality = object.getAnnotation(MinCardinality.class);
                if(minCardinality.value() > maxCardinality.value()) {
                    throw new InconsistentAnnotationException("The maximum cardinality of property " + iri.value() +
                            " in " + getDeclaringJavaClazz(object).getName() + " must not be less than the minimum cardinality.");
                }
            }
        }

        // Objects may not have different values for @MinCardinality/@MaxCardinality and @Cardinality annotations:
        for (AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, Cardinality.class)) {
            Cardinality cardinalityAnnotation = object.getAnnotation(Cardinality.class);
            int cardinality = cardinalityAnnotation.value();

            if(object.isAnnotationPresent(MinCardinality.class)) {
                MinCardinality minCardinalityAnnotation = object.getAnnotation(MinCardinality.class);

                if(minCardinalityAnnotation.value() != cardinality) {
                    String propertyIri = getIriFromObject(object);
                    String clazzIri = getIriFromObject(getDeclaringJavaClazz(object));
                    throw new InconsistentAnnotationException("Mapping for property " + propertyIri
                            + " in class mapping for " + clazzIri + " has different values ("
                            + minCardinalityAnnotation.value() + "vs. " + cardinality + ") for @MinCardinality and @Cardinality");
                }
            }
            if(object.isAnnotationPresent(MaxCardinality.class)) {
                MaxCardinality maxCardinalityAnnotation = object.getAnnotation(MaxCardinality.class);

                if(maxCardinalityAnnotation.value() != cardinality) {
                    String propertyIri = getIriFromObject(object);
                    String clazzIri = getIriFromObject(getDeclaringJavaClazz(object));
                    throw new InconsistentAnnotationException("Mapping for property " + propertyIri
                            + " in class mapping for " + clazzIri + " has different values ("
                            + maxCardinalityAnnotation.value() + "vs. " + cardinality + ") for @MaxCardinality and @Cardinality");
                }
            }
        }

        // Being functional implies that (min) cardinality is 0 or 1:
        for (AccessibleObject object : filterObjectsWithAnnotation(annotatedObjects, Functional.class)) {
            Iri iri = object.getAnnotation(Iri.class);

            if (object.isAnnotationPresent(MinCardinality.class)) {
                MinCardinality minCardinality = object.getAnnotation(MinCardinality.class);
                if(minCardinality.value() > 1) {
                    throw new InconsistentAnnotationException("Property " + iri.value() + " in " + getDeclaringJavaClazz(object).getName() +
                            " can not be at the same time functional and have a minimum cardinality of " + minCardinality.value());
                }
            }
            if (object.isAnnotationPresent(Cardinality.class)) {
                Cardinality cardinality = object.getAnnotation(Cardinality.class);
                if(cardinality.value() > 1) {
                    throw new InconsistentAnnotationException("Property " + iri.value() + " in " + getDeclaringJavaClazz(object).getName() +
                            " can not be at the same time functional and have a cardinality of " + cardinality.value());
                }
            }
        }

        // Index for those annotations that impose a property characteristic.
        // Mapping is: Property IRI -> Annotation class -> Annotation:
        Map<String, Map<Class<?>, Annotation>> characteristics = new HashMap<>();
        // Index for those annotation that impose a restriction an a property for a certain class.
        // Mapping is: Property IRI -> Restricted Class -> Annotation Class -> Annotation:
        Map<String, Map<Class<?>, Map<Class<?>, Annotation>>> restrictions = new HashMap<>();

        for (AccessibleObject object : annotatedObjects) {
            String propertyIri = getIriFromObject(object);

            // If this property IRI is encountered the first time, link to empty map:
            if(!characteristics.containsKey(propertyIri)) {
                characteristics.put(propertyIri, new HashMap<Class<?>, Annotation>());
            }
            if(!restrictions.containsKey(propertyIri)) {
                restrictions.put(propertyIri, new HashMap<Class<?>, Map<Class<?>, Annotation>>());
            }


            for(Annotation annotation : object.getAnnotations()) {

                // If the current annotation imposes an characteristic (not class dependent):
                if (isPropertyCharacteristicAnnotation(annotation)) {
                    // Not seen yet for the property? Save it:
                    if(!characteristics.get(propertyIri).containsKey(annotation.getClass())) {
                        characteristics.get(propertyIri).put(annotation.getClass(), annotation);

                    } else { // Seen before? Make sure they are equal:
                        Annotation other = characteristics.get(propertyIri).get(annotation.getClass());
                        if(!schemaAnnotationsEqual(annotation, other)) {
                            throw new InconsistentAnnotationException("Annotation " + annotation.getClass().getSimpleName() +
                                                                " defined in different ways for property " + propertyIri);
                        }
                    }

                // If the current annotation imposes an restriction (class dependent):
                } else if (isPropertyRestrictionAnnotation(annotation)) {
                    Class<?> declaringClass = getDeclaringJavaClazz(object);

                    // If this declaring class is encountered the first time, link to empty map:
                    if (!restrictions.get(propertyIri).containsKey(declaringClass)) {
                        restrictions.get(propertyIri).put(declaringClass, new HashMap<Class<?>, Annotation>());
                    }

                    // Not seen yet for the property and class? Save it:
                    if(!restrictions.get(propertyIri).get(declaringClass).containsKey(annotation.getClass())) {
                        restrictions.get(propertyIri).get(declaringClass).put(annotation.getClass(), annotation);

                    } else { // Seen before? Make sure they are equal:
                        Annotation other = restrictions.get(propertyIri).get(declaringClass).get(annotation.getClass());
                        if(!schemaAnnotationsEqual(annotation, other)) {
                            throw new InconsistentAnnotationException("Annotation " + annotation.getClass().getSimpleName() +
                                                                    " is defined in different ways for property " +
                                                                            propertyIri + " in " + declaringClass.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Filters those object that are annotated with a certain annotation.
     * @param objects The objects to filter.
     * @param annotation The annotation to search for.
     * @return Returns those objects from <code>objects</code> that have the given annotation.
     */
    private Collection<AccessibleObject> filterObjectsWithAnnotation(Collection<AccessibleObject> objects, Class<? extends Annotation> annotation) {
        Collection<AccessibleObject> result = new HashSet<>();
        for(AccessibleObject object : objects) {
            if(object.isAnnotationPresent(annotation)) {
                result.add(object);
            }
        }
        return result;
    }

    /**
     * Returns the IRI that corresponds to the given object by its {@link Iri} annotation.
     * @param annotatedObject The object to get the IRI for.
     * @return Returns the IRI of the object or null if it has no {@link Iri} annotation.
     */
    private String getIriFromObject(AnnotatedElement annotatedObject) {
        if(annotatedObject.isAnnotationPresent(Iri.class)) {
            Iri iriAnnotation = annotatedObject.getAnnotation(Iri.class);
            return iriAnnotation.value();
        } else {
            return null;
        }
    }

    /**
     * Returns the set of IRIs to which the given elements correspond by their {@link Iri} annotation.
     * @param annotatedObjects The objects (e.g. methods, fields) to scan for {@link Iri} annotations.
     * @return Returns the set (no duplicates) of the IRIs with which the objects given are annotated.
     */
    private Set<String> getIrisFromObjects(Collection<? extends AnnotatedElement> annotatedObjects) {
        Set<String> iris = new HashSet<>();
        for(AnnotatedElement object : annotatedObjects) {
            if(object.isAnnotationPresent(Iri.class)) {
                Iri iriAnnotation = object.getAnnotation(Iri.class);
                iris.add(iriAnnotation.value());
            }
        }
        return iris;
    }

    /**
     * Returns the set of IRIs of the given resource objects.
     * @param resourceObjects The resource objects for which the IRIs should be retrieved.
     * @return The IRIs of the given resource objects.
     */
    public Set<String> getIrisFromResourceObjects(Collection<? extends ResourceObject> resourceObjects) {
        Set<String> iris = new HashSet<>();
        for(ResourceObject object : resourceObjects) {
            iris.add(object.getResourceAsString());
        }
        return iris;
    }

    /**
     * Returns the Java resource object that is declaring a method or a field.
     * If the given object is a method the resource object it is declared in is returned.
     * If the given object is a field that is declared in a {@link Partial} class then
     * the resource object superinterface is returned.
     * @param object The object which declaring class to get.
     * @return Returns the class that is declaring the given object or null if the given object
     * is neither a method nor a field or if no resource object interface is found.
     */
    private Class<?> getDeclaringJavaClazz(AccessibleObject object) {
        if(object instanceof Method) {
            return ((Method) object).getDeclaringClass();
        } else if(object instanceof Field) {
            Class<?> declaringClass = ((Field) object).getDeclaringClass();

            if (declaringClass.isAnnotationPresent(Partial.class)) {
                for(Class<?> iface : declaringClass.getInterfaces()) {
                    if (iface.isAnnotationPresent(Iri.class)) {
                        return iface;
                    }
                }
                return null;
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * Checks whether there is already a contradictory restriction of the given type imposed on the property at the
     * particular class <code>propertyObject</code> is defined at.
     * Thus the validation succeeds iff no restriction is present or if there is one which restricts
     * to a subset of <code>allowedValues</code> with regard to the type of restriction.
     * @param propertyObject A {@link Iri} annotated method or field. Its corresponding property is checked.
     * @param restrictionType The type of the restriction to check, e.g. <code>owl:allValuesFrom</code>. Note that this must
     *                        be a fully qualified URI.
     * @param allowedValues The allowed values for the restriction. Thus an already present restriction must not restrict
     *                      to any value that is not in this set.
     * @param <T> The type of the values of the restriction, e.g. {@link Integer} for <code>owl:minCardinality</code>.
     * @throws RepositoryException Thrown if an error occurs while querying the connected triplestore.
     * @throws ContradictorySchemaException Thrown if the validation fails.
     */
    private <T> void validateRestriction(AccessibleObject propertyObject, String restrictionType, Set<T> allowedValues) throws RepositoryException, ContradictorySchemaException {
        // Get IRIs of the property and its declaring class:
        String iri = getIriFromObject(propertyObject);
        String clazz = getIriFromObject(getDeclaringJavaClazz(propertyObject));

        // Prepare a query for the values the restriction points to:
        ObjectQuery query;
        ObjectConnection connection = getConnection();
        try {
            query = connection.prepareObjectQuery(QUERY_PREFIX + " SELECT ?v { ?r a owl:Restriction . " +
                    "?r owl:onClass <" + clazz + "> . " +
                    "?r owl:onProperty <" + iri + "> . " +
                    "?r <" + restrictionType + "> ?v . }");

        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        }

        // Execute the query:
        Set<String> result;
        try {
            result = getIrisFromResourceObjects(query.evaluate(OWLClazz.class).asSet());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Error evaluating query.");
        }

        // Validate that the result is empty (no restriction exists) or that it is a subset of the allowed values:
        if(!result.isEmpty() && !allowedValues.containsAll(result)) {
            throw new ContradictorySchemaException("Restriction of " + iri + " at class " + clazz
                                    + " with values " + allowedValues.toString() +
                                    " is contradictory to already existing restriction to values " + result.toString());
        }
    }

    private  <T> T createObject(Class<T> clazz, Resource id) throws RepositoryException {
        ObjectConnection connection = getConnection();
        ObjectFactory objectFactory = connection.getObjectFactory();

        Resource resource = (id != null) ? id : IDGenerator.BLANK_RESOURCE;

        T object = objectFactory.createObject(resource, clazz);
        return connection.addDesignation(object, clazz);
    }

    /**
     * Tries to find the class resource and returns it.
     * If no class with such an IRI exists then a new one is created and returned.
     * @param clazzIri The IRI of the class to retrieve.
     * @return Returns the {@link OWLClazz} object for the given IRI.
     * @throws RepositoryException Thrown if an error occurs while retrieving/creating the class object.
     */
    private OWLClazz createClazzOnDemand(String clazzIri) throws RepositoryException {
        try {
            OWLClazz clazz = getConnection().findObject(OWLClazz.class, new URIImpl(clazzIri));

            if(clazz == null) {
                return createObject(OWLClazz.class, new URIImpl(clazzIri));
            } else {
                return clazz;
            }

        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns an prepared update to the default graph of the connected triplestore,
     * which will insert statements stating that every given resource is
     * an instance of the given class.
     * Thus for every instance <code>i</code> in <code>instances</code> the following statement will be inserted:<br>
     *     <code>&lt;i&gt; rdf:type &lt;clazz&gt; .</code><br>
     * {@link Update#execute()} must be called afterwards for the update to have effect.
     * @param instances The resources which should all be declared instances of <code>clazz</code>.
     *                  Note that the given resources must be fully qualified URIs.
     * @param clazz The class to which all <code>instances</code> should belong.
     *              Must be a fully qualified URI.
     * @return The prepared update.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws MalformedQueryException Thrown if the generated query is malformed, e.g. because a resource
     * in <code>instances</code> is not a fully qualified URI.
     */
    private Update buildInstanceUpdate(Collection<String> instances, String clazz) throws RepositoryException, MalformedQueryException {
        StringBuilder query = new StringBuilder(QUERY_PREFIX)
                                    .append("INSERT DATA {");
        for(String instance : instances) {
            query.append(" <" + instance + "> a <" + clazz + "> .");
        }
        query.append("}");

        return getConnection().prepareUpdate(query.toString());
    }

    /**
     * Constructs an <code>owl:Restriction</code> instance for the property corresponding to the given object.
     * Only the <code>owl:onClass</code> and <code>owl:onProperty</code> properties are set for this restriction.
     * Note that restrictions constrain properties only in the scope of the class the are defined in.
     * @param propertyObject The {@link Iri} annotated method or field for which the restriction should be created.
     * @return The created restriction object.s
     * @throws RepositoryException Thrown if an error occurs while creating the restriction object.
     */
    private Restriction buildRestrictionForProperty(AccessibleObject propertyObject, String onClazzIri) throws RepositoryException {
        ObjectConnection connection = getConnection();

        // Get the IRI of the object:
        String propertyIri = getIriFromObject(propertyObject);
        // Get the IRI of the class which is declaring the object:
        Class<?> declaringClass = getDeclaringJavaClazz(propertyObject);
        String clazzIri = getIriFromObject(declaringClass);

        // Get the property and class objects from the connection:
        RDFSProperty property;
        OWLClazz clazz;
        try {
            property = connection.findObject(RDFSProperty.class, new URIImpl(propertyIri));
            clazz = connection.findObject(OWLClazz.class, new URIImpl(clazzIri));

            // Create the property and class if they do not exist:
            if(property == null) {
                property = createObject(RDFSProperty.class, new URIImpl(propertyIri));
            }
            if(clazz == null) {
                clazz = createObject(OWLClazz.class, new URIImpl(clazzIri));
            }

        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query.");
        }

        Restriction restriction = createObject(Restriction.class, null);
        restriction.setOnProperty(Sets.<RDFSProperty>newHashSet(property));

        // Set the class against which the restriction is stated, if it is provided:
        if(onClazzIri != null) {
            restriction.setOnClazz(Sets.newHashSet(createClazzOnDemand(onClazzIri)));
        }

        // Set the restriction as superclass of the declaring class:
        try {
            Update query = connection.prepareUpdate(QUERY_PREFIX + "INSERT DATA { " +
                    "   <" + clazz.getResourceAsString() + "> rdfs:subClassOf <" + restriction.getResourceAsString() + "> ." +
                    "}");
            query.execute();

        } catch (MalformedQueryException | UpdateExecutionException e) {
            throw new RepositoryException(e);
        }

        return restriction;
    }

    /**
     * Persists the information that a property is functional to the default graph of the connected triplestore.
     * All properties with {@link Functional} or {@link Bijective} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistFunctional(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @Functional annotation:
        Collection<AccessibleObject> functionalObjects = filterObjectsWithAnnotation(annotatedObjects, Functional.class);
        // All those objects that are declared bijective are also functional:
        functionalObjects.addAll(filterObjectsWithAnnotation(annotatedObjects, Bijective.class));

        // Prepare the update query and execute it:
        try {
            Update update = buildInstanceUpdate(getIrisFromObjects(functionalObjects), OWL.FUNCTIONAL_PROPERTY);
            update.execute();
        } catch (MalformedQueryException e) {
            throw new UpdateExecutionException();
        }
    }

    /**
     * Persists the information that a property is inverse functional to the default graph of the connected triplestore.
     * All properties with {@link InverseFunctional} or {@link Bijective} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistInverseFunctional(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @InverseFunctional annotation:
        Collection<AccessibleObject> inverseFunctionalObjects = filterObjectsWithAnnotation(annotatedObjects, InverseFunctional.class);
        // All those objects that are declared bijective are also inverse functional:
        inverseFunctionalObjects.addAll(filterObjectsWithAnnotation(annotatedObjects, Bijective.class));

        // Prepare the update query and execute it:
        try {
            Update update = buildInstanceUpdate(getIrisFromObjects(inverseFunctionalObjects), OWL.INVERSE_FUNCTIONAL_PROPERTY);
            update.execute();
        } catch (MalformedQueryException e) {
            throw new UpdateExecutionException();
        }
    }

    /**
     * Persists the information that a property is symmetric to the default graph of the connected triplestore.
     * All properties with {@link com.github.anno4j.annotations.Symmetric} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistSymmetric(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @Symmetric annotation:
        Collection<AccessibleObject> symmetricObjects = filterObjectsWithAnnotation(annotatedObjects, Symmetric.class);

        // Prepare the update query and execute it:
        try {
            Update update = buildInstanceUpdate(getIrisFromObjects(symmetricObjects), OWL.SYMMETRIC_PROPERTY);
            update.execute();
        } catch (MalformedQueryException e) {
            throw new UpdateExecutionException();
        }
    }

    /**
     * Persists the information that a property is transitive to the default graph of the connected triplestore.
     * All properties with {@link com.github.anno4j.annotations.Transitive} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistTransitive(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @Transitive annotation:
        Collection<AccessibleObject> transitiveObjects = filterObjectsWithAnnotation(annotatedObjects, Transitive.class);

        // Prepare the update query and execute it:
        try {
            Update update = buildInstanceUpdate(getIrisFromObjects(transitiveObjects), OWL.TRANSITIVE_PROPERTY);
            update.execute();
        } catch (MalformedQueryException e) {
            throw new UpdateExecutionException();
        }
    }

    /**
     * Persists the information that a property is a subproperty of another to the default graph of the connected triplestore.
     * All properties with {@link SubPropertyOf} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistSubPropertyOf(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @Transitive annotation:
        Collection<AccessibleObject> subPropertyObjects = filterObjectsWithAnnotation(annotatedObjects, SubPropertyOf.class);

        for (AccessibleObject object : subPropertyObjects) {
            String iri = getIriFromObject(object);

            SubPropertyOf subPropertyAnnotation = object.getAnnotation(SubPropertyOf.class);

            StringBuilder query = new StringBuilder(QUERY_PREFIX)
                                    .append(" INSERT DATA { ");
            for (String superPropertyIri : subPropertyAnnotation.value()) {
                query.append("<").append(iri).append("> ")
                     .append("<").append(RDFS.SUB_PROPERTY_OF).append("> ")
                     .append("<").append(superPropertyIri).append("> . ");
            }
            query.append("}");

            // Prepare the update query and execute it:
            try {
                Update update = getConnection().prepareUpdate(query.toString());
                update.execute();
            } catch (MalformedQueryException e) {
                throw new UpdateExecutionException();
            }
        }
    }

    /**
     * Persists the information that a property is the inverse of another property to the default graph of the connected triplestore.
     * All properties with {@link InverseOf} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     * @throws UpdateExecutionException Thrown if an error occurred while executing the update.
     */
    private void persistInverseOf(Collection<AccessibleObject> annotatedObjects) throws RepositoryException, UpdateExecutionException {
        // Get those methods and fields that have the @InverseOf annotation:
        Collection<AccessibleObject> inverseOfObjects = filterObjectsWithAnnotation(annotatedObjects, InverseOf.class);

        for (AccessibleObject object : inverseOfObjects) {
            String iri = getIriFromObject(object);

            InverseOf inverseOfAnnotation = object.getAnnotation(InverseOf.class);

            StringBuilder query = new StringBuilder(QUERY_PREFIX)
                    .append(" INSERT DATA { ");
            for (String inversePropertyIri : inverseOfAnnotation.value()) {
                query.append("<").append(iri).append("> owl:inverseOf ")
                        .append("<").append(inversePropertyIri).append("> . ")
                     .append("<").append(inversePropertyIri).append("> owl:inverseOf ")
                     .append("<").append(iri).append("> . ");
            }
            query.append("}");

            // Prepare the update query and execute it:
            try {
                Update update = getConnection().prepareUpdate(query.toString());
                update.execute();
            } catch (MalformedQueryException e) {
                throw new UpdateExecutionException();
            }
        }
    }

    /**
     * Persists the information that a property is restricted to have all values from a specified set of classes.
     * All properties with {@link AllValuesFrom} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistAllValuesFrom(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        // Get those methods and fields that have the @AllValuesFrom annotation:
        Collection<AccessibleObject> allValuesFromObjects = filterObjectsWithAnnotation(annotatedObjects, AllValuesFrom.class);

        for (AccessibleObject object : allValuesFromObjects) {
            // Get the IRIs of the classes defined in the annotation:
            AllValuesFrom allValuesFrom = object.getAnnotation(AllValuesFrom.class);
            Set<String> clazzIris = getIrisFromObjects(Arrays.asList(allValuesFrom.value()));

            Set<OWLClazz> clazzes = new HashSet<>();
            for (String clazzIri : clazzIris) {
                clazzes.add(createClazzOnDemand(clazzIri));
            }

            // Add the classes to the restriction:
            Restriction restriction = buildRestrictionForProperty(object, null);
            restriction.setAllValuesFrom(clazzes);
        }
    }

    /**
     * Persists the information that a property is restricted to have some values from a specified set of classes.
     * All properties with {@link SomeValuesFrom} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistSomeValuesFrom(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        // Get those methods and fields that have the @AllValuesFrom annotation:
        Collection<AccessibleObject> someValuesFromObjects = filterObjectsWithAnnotation(annotatedObjects, SomeValuesFrom.class);

        for (AccessibleObject object : someValuesFromObjects) {
            Restriction restriction = buildRestrictionForProperty(object, null);

            // Get the IRIs of the classes defined in the annotation:
            SomeValuesFrom allValuesFrom = object.getAnnotation(SomeValuesFrom.class);
            Set<String> clazzIris = getIrisFromObjects(Arrays.asList(allValuesFrom.value()));

            Set<OWLClazz> clazzes = new HashSet<>();
            for (String clazzIri : clazzIris) {
                clazzes.add(createClazzOnDemand(clazzIri));
            }

            // Add the classes to the restriction:
            restriction.setSomeValuesFrom(clazzes);
        }
    }

    /**
     * Persists the information that a property is restricted to have a minimum number of values.
     * All properties with {@link MinCardinality} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistMinCardinality(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        // Get those methods and fields that have the @MinCardinality annotation:
        Collection<AccessibleObject> minCardinalityObjects = filterObjectsWithAnnotation(annotatedObjects, MinCardinality.class);

        for (AccessibleObject object : minCardinalityObjects) {
            MinCardinality minCardinalityAnnotation = object.getAnnotation(MinCardinality.class);
            int minCardinality = minCardinalityAnnotation.value();

            String onClazzIri = null;
            if(minCardinalityAnnotation.onClass() != OWLClazz.class) {
                onClazzIri = getIriFromObject(minCardinalityAnnotation.onClass());
            }

            // Add the cardinality to the restriction:
            Restriction restriction = buildRestrictionForProperty(object, onClazzIri);
            restriction.setMinCardinality(Sets.<Integer>newHashSet(minCardinality));
        }
    }

    /**
     * Persists the information that a property is restricted to have a maximum number of values.
     * All properties with {@link MinCardinality} annotation are considered.
     * @param annotatedObjects The {@link Iri} annotated objects that should be considered.
     * @throws RepositoryException Thrown on error regarding the connected triplestore.
     */
    private void persistMaxCardinality(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        // Get those methods and fields that have the @MaxCardinality annotation:
        Collection<AccessibleObject> maxCardinalityObjects = filterObjectsWithAnnotation(annotatedObjects, MaxCardinality.class);

        for (AccessibleObject object : maxCardinalityObjects) {

            MaxCardinality maxCardinalityAnnotation = object.getAnnotation(MaxCardinality.class);
            int minCardinality = maxCardinalityAnnotation.value();

            String onClazzIri = null;
            if(maxCardinalityAnnotation.onClass() != OWLClazz.class) {
                onClazzIri = getIriFromObject(maxCardinalityAnnotation.onClass());
            }

            Restriction restriction = buildRestrictionForProperty(object, onClazzIri);

            // Add the cardinality to the restriction:
            restriction.setMaxCardinality(Sets.<Integer>newHashSet(minCardinality));
        }
    }

    private void persistCardinality(Collection<AccessibleObject> annotatedObjects) throws RepositoryException {
        // Get those methods and fields that have the @Cardinality annotation:
        Collection<AccessibleObject> cardinalityObjects = filterObjectsWithAnnotation(annotatedObjects, Cardinality.class);

        for (AccessibleObject object : cardinalityObjects) {

            Cardinality cardinalityAnnotation = object.getAnnotation(Cardinality.class);
            int cardinality = cardinalityAnnotation.value();

            String onClazzIri = null;
            if(cardinalityAnnotation.onClass() != OWLClazz.class) {
                onClazzIri = getIriFromObject(cardinalityAnnotation.onClass());
            }

            Restriction restriction = buildRestrictionForProperty(object, onClazzIri);
            // Add the cardinality to the restriction:
            restriction.setMaxCardinality(Sets.<Integer>newHashSet(cardinality));
            restriction.setMinCardinality(Sets.<Integer>newHashSet(cardinality));
        }
    }

    /**
     * Checks whether a given annotation is a schema annotation.
     * @param annotation The annotation to check.
     * @return Returns true iff the given annotation is a schema annotation.
     */
    private boolean isSchemaAnnotation(Annotation annotation) {
        return isPropertyCharacteristicAnnotation(annotation)
                || isPropertyRestrictionAnnotation(annotation);
    }

    /**
     * Checks wether the given annotation imposes a property characteristic, i.e. is one of
     * <ul>
     *     <li>{@link Functional}</li>
     *     <li>{@link InverseFunctional}</li>
     *     <li>{@link Symmetric}</li>
     *     <li>{@link Transitive}</li>
     *     <li>{@link InverseOf}</li>
     *     <li>{@link Bijective}</li>
     * </ul>
     * @param annotation The annotation to check.
     * @return Returns true if the annotation imposes a property characteristic.
     */
    private boolean isPropertyCharacteristicAnnotation(Annotation annotation) {
        return annotation instanceof Functional
                || annotation instanceof InverseFunctional
                || annotation instanceof Symmetric
                || annotation instanceof Transitive
                || annotation instanceof SubPropertyOf
                || annotation instanceof InverseOf
                || annotation instanceof Bijective;
    }

    /**
     * Checks whether the given property imposes a restriction on a property for a certain class, i.e. is one of
     * <ul>
     *     <li>{@link AllValuesFrom}</li>
     *     <li>{@link SomeValuesFrom}</li>
     *     <li>{@link MinCardinality}</li>
     *     <li>{@link MaxCardinality}</li>
     * </ul>
     * @param annotation
     * @return
     */
    private boolean isPropertyRestrictionAnnotation(Annotation annotation) {
        return annotation instanceof AllValuesFrom
                || annotation instanceof SomeValuesFrom
                || annotation instanceof MinCardinality
                || annotation instanceof MaxCardinality;
    }

    /**
     * Checks whether two annotations are equal.
     * For some annotations (e.g. {@link AllValuesFrom} an array of values is possible,
     * thus in these cases comparison must be done without consideration of order.
     * @param a1 The first annotation.
     * @param a2 The second annotation.
     * @return Returns true iff both annotations are equivalent.
     */
    private boolean schemaAnnotationsEqual(Annotation a1, Annotation a2) {
        if (a1.getClass().equals(a2.getClass())) {
            // Special cases - Annotations with a array as value:
            if(a1 instanceof SubPropertyOf) {
                return Sets.newHashSet(((SubPropertyOf) a1).value()).equals(Sets.newHashSet(((SubPropertyOf) a2).value()));
            } else if(a1 instanceof AllValuesFrom) {
                return Sets.newHashSet(((AllValuesFrom) a1).value()).equals(Sets.newHashSet(((AllValuesFrom) a2).value()));
            } else if(a1 instanceof SomeValuesFrom) {
                return Sets.newHashSet(((SomeValuesFrom) a1).value()).equals(Sets.newHashSet(((SomeValuesFrom) a2).value()));
            } else if(a1 instanceof InverseOf) {
                return Sets.newHashSet(((InverseOf) a1).value()).equals(Sets.newHashSet(((InverseOf) a2).value()));

            } else {
                return a1.equals(a2);
            }

        } else {
            return false;
        }
    }
}
