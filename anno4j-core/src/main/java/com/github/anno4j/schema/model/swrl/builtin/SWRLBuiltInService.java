package com.github.anno4j.schema.model.swrl.builtin;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.swrl.BuiltinAtom;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * This singleton class provides functionality for retrieving {@link SWRLBuiltin} types and instances by their IRI.
 * The concrete classes for SWRL built-ins are annotated with {@link SWRLBuiltinIri} specifying the IRI
 * of the built-in. This service scans for such types, for which instances can be retrieved later by their IRI.
 */
public class SWRLBuiltInService {

    /**
     * The singe instance of this object.
     */
    private static SWRLBuiltInService instance;

    /**
     * The built-in types known by this service identified by their IRI.
     */
    private Map<String, Class<SWRLBuiltin>> builtInsByIri = new HashMap<>();

    /**
     * Constructs a new service by scanning the classpath for {@link SWRLBuiltin}s with
     * a {@link SWRLBuiltinIri} annotation.
     * @throws ClassCastException Thrown if any class annotated with {@link SWRLBuiltinIri} is
     * not of type {@link SWRLBuiltin}.
     */
    private SWRLBuiltInService() {


        // Find all annotated built-ins and add them to the internal map:
        for(Class<?> builtinType : getAvailableBuiltIns()) {
            SWRLBuiltinIri builtinIri = builtinType.getAnnotation(SWRLBuiltinIri.class);

            builtInsByIri.put(builtinIri.value(), (Class<SWRLBuiltin>) builtinType);
        }
    }

    private static Collection<Class<?>> getAvailableBuiltIns() {
        // Construct the classpath:
        Set<URL> classpath = new HashSet<>();
        classpath.addAll(ClasspathHelper.forClassLoader());
        classpath.addAll(ClasspathHelper.forJavaClassPath());
        classpath.addAll(ClasspathHelper.forManifest());
        classpath.addAll(ClasspathHelper.forPackage(""));

        // Scanner for types in the classpath:
        Reflections annotatedClasses = new Reflections(new ConfigurationBuilder()
                .setUrls(classpath)
                .useParallelExecutor()
                .filterInputsBy(FilterBuilder.parsePackages("-java, -javax, -sun, -com.sun"))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));

        return annotatedClasses.getTypesAnnotatedWith(SWRLBuiltinIri.class);
    }

    /**
     * Returns the type of the {@link SWRLBuiltin} that corresponds to the given IRI.
     * @param iri The IRI of the built-in to find.
     * @return Returns the built-ins type or null if no built-in is registered for the given IRI.
     */
    public Class<SWRLBuiltin> getBuiltIn(String iri) {
        return builtInsByIri.get(iri);
    }

    /**
     * Instantiates a {@link SWRLBuiltin} object of the type indicated by {@code iri} and with the
     * specified arguments.
     * @param iri The IRI of the built-ins type.
     * @param arguments The arguments of the built-in.
     * @return Returns an instance of the built-in with the specified arguments.
     * @throws InstantiationException Thrown if an error occurs while instantiating the built-in object.
     */
    public SWRLBuiltin getBuiltIn(String iri, List<Object> arguments) throws InstantiationException {
        Class<SWRLBuiltin> type = getBuiltIn(iri); // Get the built-in type

        try {
            Constructor<SWRLBuiltin> constructor = type.getConstructor(List.class);
            return constructor.newInstance(arguments);

        } catch (NoSuchMethodException e) {
            throw new InstantiationException(type.getName() + " must provide an appropriate constructor.");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new InstantiationException("Instance of type " + type.getName() + " couldn't be created. Details:" + e.getMessage());
        }
    }

    /**
     * Instantiates a {@link SWRLBuiltin} object with the type and arguments specified by the atom
     * {@code atom}.
     * @param atom The rule atom which describes the built-in to instantiate.
     * @return Returns an instance of the built-in with the specified arguments.
     * @throws InstantiationException Thrown if an error occurs while instantiating the built-in object.
     */
    public SWRLBuiltin getBuiltIn(BuiltinAtom atom) throws InstantiationException {
        return getBuiltIn(atom.getBuiltinResource().getResourceAsString(), atom.getArguments());
    }

    /**
     * @return Returns an instance of the service.
     */
    public static SWRLBuiltInService getBuiltInService() {
        if(instance == null) {
            instance = new SWRLBuiltInService();
        }

        return instance;
    }

    public void registerBuiltIns(ObjectConnection connection) throws RepositoryException {
        for(Class<?> builtinType : getAvailableBuiltIns()) {
            SWRLBuiltinIri builtInIriAnnotation = builtinType.getAnnotation(SWRLBuiltinIri.class);
            connection.createObject(ResourceObject.class, new URIImpl(builtInIriAnnotation.value()));
        }
    }
}
