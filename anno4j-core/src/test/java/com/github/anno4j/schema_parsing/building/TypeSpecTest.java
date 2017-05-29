package com.github.anno4j.schema_parsing.building;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Class providing convenience functions for testing JavaPoet
 * {@link com.squareup.javapoet.TypeSpec}.
 */
abstract class TypeSpecTest {

    /**
     * Returns the fully qualified names of all superinterfaces of the given type.
     * @param typeSpec The type spec to get the superinterfaces for.
     * @return The names of the types superinterfaces.
     */
    static Set<String> getSuperinterfaceNames(TypeSpec typeSpec) {
        Set<String> superInterfaceNames = new HashSet<>();
        for (TypeName superInterface : typeSpec.superinterfaces) {
            superInterfaceNames.add(superInterface.toString());
        }
        return superInterfaceNames;
    }

    /**
     * Returns the names of all public methods of the given type.
     * @param typeSpec The type to get methods for.
     * @return The method names.
     */
    static Set<String> getMethodNames(TypeSpec typeSpec) {
        Set<String> methodNames = new HashSet<>();
        for (MethodSpec methodSpec : typeSpec.methodSpecs) {
            if(methodSpec.modifiers.contains(Modifier.PUBLIC)) {
                methodNames.add(methodSpec.name);
            }
        }
        return methodNames;
    }

    /**
     * Returns the names of all fields of the given type.
     * @param typeSpec The type to get field names for.
     * @return The field names.
     */
    static Set<String> getFieldNames(TypeSpec typeSpec) {
        Set<String> fieldNames = new HashSet<>();
        for(FieldSpec fieldSpec : typeSpec.fieldSpecs) {
            fieldNames.add(fieldSpec.name);
        }
        return fieldNames;
    }
}
