package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
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
public abstract class SupportTypeSpecSupport extends ClazzBuildingSupport implements ExtendedRDFSClazz {

    /**
     * Checks if <code>property</code> is an outgoing property of any (transitive) superclass
     * of this class, which is not part of the RDFS specification (i.e. has the {@link RDFS#NS namespace}).
     * @param property The property to check for.
     * @return Returns true if and only if <code>property</code> is not an outgoing property of any transitive non-RDFS
     * superclass.
     */
    private boolean isDefinedInSuperclass(ExtendedRDFSProperty property) {
        // Check if the property is present in any (non RDFS) superclass:
        boolean definedInSuper = false;
        for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
            definedInSuper |= superClazz.hasPropertyTransitive(property)
                    && !superClazz.getResourceAsString().startsWith(RDFS.NS);
        }
        return definedInSuper;
    }

    /**
     * Generates a protected utility method for a resource object support class,
     * which returns the {@link ResourceObject} instance to which the behaviour belongs to.
     * The signature of the generated method is
     * <code>private Object _isResourceObjectMethodPresent() throws {@link RepositoryException}</code>.
     * @return The JavaPoet method specification of the utility method.
     */
    private MethodSpec buildResourceObjectGetter() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("_getResourceObject");

        ClassName repositoryException = ClassName.get(RepositoryException.class);

        // The JavaDoc of the method:
        CodeBlock javaDoc = CodeBlock.of("Returns the resource object to which this behaviour belongs to." + System.lineSeparator() +
                                         "@return The instance this behaviour belongs to." + System.lineSeparator() +
                                         "@throws $T Thrown if the resource object could not be retrieved.", repositoryException);

        methodBuilder.addStatement("return getObjectConnection().getObject(getResourceAsString())");

        return methodBuilder.addModifiers(Modifier.PROTECTED)
                            .returns(ClassName.get(Object.class))
                            .addJavadoc(javaDoc)
                            .addException(repositoryException)
                            .build();
    }

    /**
     * Generates a private utility method for a resource object support class,
     * which calls a <strong>public</strong> method of the resource object by name with a given argument at runtime via reflection.
     * The method exits without a call if there is no public method with the given name.
     * The signature of the generated method is
     * <code>private void _invokeResourceObjectMethodIfExists(String, Object) throws {@link RuntimeException}</code>.
     * @return The JavaPoet method specification of the generated method.
     */
    private MethodSpec buildMethodInvocator() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("_invokeResourceObjectMethodIfExists");

        // Prepare the parameters of the method:
        ParameterSpec methodName = ParameterSpec.builder(ClassName.get(String.class), "methodName").build();
        ParameterSpec argument = ParameterSpec.builder(ClassName.get(Object.class), "arg").build();

        // Prepare exception that is thrown:
        ClassName runtimeException = ClassName.get(RuntimeException.class);
        ClassName repositoryException = ClassName.get(RepositoryException.class);
        ClassName illegalAccessException = ClassName.get(IllegalAccessException.class);
        ClassName invocationTargetException = ClassName.get(InvocationTargetException.class);

        // The JavaDoc of the method:
        CodeBlock javaDoc = CodeBlock.of("Checks whether a method with a given name is defined for " +
                        "the resource object this support class belongs to and invokes it passing the given argument." + System.lineSeparator() +
                        "This method is used to reset subproperties at runtime." + System.lineSeparator() +
                        "@param $N The name of the method." + System.lineSeparator() +
                        "@param $N The single argument that is passed to the method." + System.lineSeparator() +
                        "@throws $T Thrown if an error occurs while retrieving the object or its methods.",
                methodName, argument, runtimeException);

        // In the methods code, first get the resource object the generated support class belongs to at runtime,
        // then get the Class description of it:
        methodBuilder
                // Definition of variables:
                .addStatement("$T resourceObject = null", ClassName.get(Object.class))
                .addStatement("$T objectClazz = null", ClassName.get(Class.class))

                // Try block to get the values:
                .beginControlFlow("try")
                .addStatement("resourceObject = this._getResourceObject()")
                .addStatement("objectClazz = resourceObject.getClass()")
                .endControlFlow() // End try
                .beginControlFlow("catch($T exc)", repositoryException)
                .addStatement("throw new $T(exc.getMessage())", runtimeException)
                .endControlFlow(); // End catch

        // Search for the method and invoke it:
        methodBuilder
                .beginControlFlow("try")
                .beginControlFlow("for($T method : objectClazz.getMethods())", ClassName.get(Method.class))
                .beginControlFlow("if(method.getName().equals($N))", methodName)
                .addStatement("method.invoke(resourceObject, $N)", argument)
                .addStatement("break")
                .endControlFlow() // End if
                .endControlFlow() // End for
                .endControlFlow() // End try
                .beginControlFlow("catch($T | $T exc)", illegalAccessException, invocationTargetException)
                .addStatement("throw new $T(exc.getMessage())", runtimeException)
                .endControlFlow(); // End catch

        return methodBuilder.addModifiers(Modifier.PRIVATE)
                            .addParameter(methodName)
                            .addParameter(argument)
                            .returns(void.class)
                            .addJavadoc(javaDoc)
                            .addException(runtimeException)
                            .build();
    }

    @Override
    public TypeSpec buildSupportTypeSpec(OntGenerationConfig config) {
        // First try to find a name for this class:
        ClassName interfaceName = getJavaPoetClassName(config);
        ClassName supportClassName = ClassName.get(interfaceName.packageName(), interfaceName.simpleName() + "Support");

        // Partial annotation:
        AnnotationSpec partialAnnotation = AnnotationSpec.builder(Partial.class)
                                                        .build();

        // Generate @Iri annotated fields:
        Collection<FieldSpec> fields = new HashSet<>();
        for (ExtendedRDFSProperty property : getOutgoingProperties()) {
            // Only generate a field for this property if its not defined in a non-RDFS superclass:
            if(!isDefinedInSuperclass(property)) {
                fields.add(property.buildAnnotatedField(config));
            }
        }

        // Generate methods:
        Collection<MethodSpec> getters = new HashSet<>();
        Collection<MethodSpec> setters = new HashSet<>();
        Collection<MethodSpec> adders  = new HashSet<>();
        Collection<MethodSpec> addersAll  = new HashSet<>();
        Collection<MethodSpec> removers = new HashSet<>();
        Collection<MethodSpec> removersAll = new HashSet<>();
        for (ExtendedRDFSProperty property : getOutgoingProperties()) {
            // Only add the method to the type spec if it was not already defined in a superclass:
            if(!isDefinedInSuperclass(property)) {
                getters.add(property.buildGetterImplementation(config));
                setters.add(property.buildSetterImplementation(config));
                adders.add(property.buildAdderImplementation(config));
                addersAll.add(property.buildAdderAllImplementation(config));
                removers.add(property.buildRemoverImplementation(config));
                removersAll.add(property.buildRemoverAllImplementation(config));
            }
        }

        // JavaDoc of the class:
        CodeBlock.Builder javaDoc = CodeBlock.builder();
        javaDoc.add("Support class for {@link $T}", interfaceName);

        // The superclass is ResourceObjectSupport, so prepare it:
        ClassName resourceObjectSupport = ClassName.get(ResourceObjectSupport.class);

        return TypeSpec.classBuilder(supportClassName)
                .addJavadoc(javaDoc.build())
                .addAnnotation(partialAnnotation)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(resourceObjectSupport)
                .addSuperinterface(interfaceName)
                .addFields(fields)
                .addMethod(buildResourceObjectGetter())
                .addMethod(buildMethodInvocator())
                .addMethods(getters)
                .addMethods(setters)
                .addMethods(adders)
                .addMethods(addersAll)
                .addMethods(removers)
                .addMethods(removersAll)
                .build();
    }
}
