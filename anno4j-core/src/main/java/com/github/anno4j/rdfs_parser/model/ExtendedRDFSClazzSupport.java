package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.naming.ClassNameBuilder;
import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazzSupport;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Partial
public abstract class ExtendedRDFSClazzSupport extends RDFSClazzSupport implements ExtendedRDFSClazz {

    private Set<ExtendedRDFSProperty> outgoingProperties = new HashSet<>();

    private Set<ExtendedRDFSProperty> incomingProperties = new HashSet<>();

    private Set<ExtendedRDFSClazz> superClazzes = new HashSet<>();

    @Override
    public Set<ExtendedRDFSProperty> getOutgoingProperties() {
        HashSet<ExtendedRDFSProperty> transitiveOutProps = new HashSet<>();
        transitiveOutProps.addAll(outgoingProperties);
        for (ExtendedRDFSClazz superClazz : superClazzes) {
            transitiveOutProps.addAll(superClazz.getOutgoingProperties());
        }
        return transitiveOutProps;
    }

    @Override
    public void setOutgoingProperties(Set<ExtendedRDFSProperty> props) {
        setOutgoingProperties(props, true);
    }

    @Override
    public void setOutgoingProperties(Set<ExtendedRDFSProperty> props, boolean updateInverse) {
        if(updateInverse) {
            for (ExtendedRDFSProperty prop : props) {
                prop.addDomainClazz(this, false); // Add this class to the domain classes of the property. Don't inverse again.
            }
        }
        outgoingProperties = props;
    }

    @Override
    public Set<ExtendedRDFSProperty> getIncomingProperties() {
        HashSet<ExtendedRDFSProperty> transitiveInProps = new HashSet<>();
        transitiveInProps.addAll(incomingProperties);
        for (ExtendedRDFSClazz superClazz : superClazzes) {
            transitiveInProps.addAll(superClazz.getIncomingProperties());
        }
        return transitiveInProps;
    }

    @Override
    public void setIncomingProperties(Set<ExtendedRDFSProperty> props) {
        setIncomingProperties(props, true);
    }

    @Override
    public void setIncomingProperties(Set<ExtendedRDFSProperty> props, boolean updateInverse) {
        if(updateInverse) {
            for (ExtendedRDFSProperty prop : props) {
                prop.addRangeClazz(this, false); // Add this class to the range classes of the property. Don't inverse again.
            }
        }
        incomingProperties = props;
    }

    @Override
    public void addOutgoingProperty(ExtendedRDFSProperty prop) {
        addOutgoingProperty(prop, true);
    }

    @Override
    public void addOutgoingProperty(ExtendedRDFSProperty prop, boolean updateInverse) {
        if(updateInverse) {
            prop.addDomainClazz(this, false);
        }
        outgoingProperties.add(prop);
    }

    @Override
    public void addIncomingProperty(ExtendedRDFSProperty prop) {
        addIncomingProperty(prop, true);
    }

    @Override
    public void addIncomingProperty(ExtendedRDFSProperty prop, boolean updateInverse) {
        if(updateInverse) {
            prop.addRangeClazz(this, false);
        }
        incomingProperties.add(prop);
    }

    @Override
    public Set<ExtendedRDFSClazz> getSuperclazzes() {
        return superClazzes;
    }

    @Override
    public void setSuperclazzes(Set<ExtendedRDFSClazz> clazzes) {
        superClazzes.clear();
        superClazzes.addAll(clazzes);

        // Set subclass relationship at the parent. (Persisted by Anno4j):
        for (RDFSClazz parent : clazzes) {
            Set<RDFSClazz> sisters = new HashSet<>();
            if (parent.getSubClazzes() != null) {
                sisters.addAll(parent.getSubClazzes());
            }
            sisters.add(this);
            parent.setSubClazzes(sisters);
        }
    }

    @Override
    public void addSuperclazz(ExtendedRDFSClazz clazz) {
        superClazzes.add(clazz);

        // Set subclass relationship at the parent. (Persisted by Anno4j):
        Set<RDFSClazz> sisters = new HashSet<>();
        if (clazz.getSubClazzes() != null) {
            sisters.addAll(clazz.getSubClazzes());
        }
        sisters.add(this);
        clazz.setSubClazzes(sisters);
    }

    @Override
    public boolean hasParent(String resource) {
        Set<String> superClazzResources = new HashSet<>();
        for (ExtendedRDFSClazz clazz : superClazzes) {
            superClazzResources.add(clazz.getResourceAsString());
        }

        if(superClazzResources.contains(resource)) {
            return true;
        } else {
            boolean transitiveParent = false;
            for (ExtendedRDFSClazz superClazz : superClazzes) {
                transitiveParent |= superClazz.hasParent(resource);
            }
            return transitiveParent;
        }
    }

    @Override
    public boolean hasParent(ExtendedRDFSClazz clazz) {
        return hasParent(clazz.getResourceAsString());
    }

    @Override
    public boolean isLiteral() {
        return hasParent(RDFS.LITERAL);
    }

    @Override
    public String getJavaPackageName() {
        try {
            return ClassNameBuilder.builder(getResourceAsString()).packageName();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    @Override
    public ClassName getJavaPoetClassName(OntGenerationConfig config) throws URISyntaxException, IdentifierBuilder.NameBuildingException {
        if (getResourceAsString().equals(RDFS.CLAZZ) || getResourceAsString().equals(RDFS.RESOURCE)) {
            return ClassName.get(ResourceObject.class);

        }

        // Capture XSD datatypes:
        switch (getResourceAsString()) {
            case XSD.STRING: return ClassName.get(String.class);
            case XSD.BOOLEAN: return ClassName.get(Boolean.class);
            case XSD.DECIMAL: return ClassName.get(Double.class);
            case XSD.FLOAT: return ClassName.get(Float.class);
            case XSD.DOUBLE: return ClassName.get(Double.class);
            case XSD.INTEGER: return ClassName.get(Integer.class);
            case XSD.LONG: return ClassName.get(Long.class);
            case XSD.INT: return ClassName.get(Integer.class);
            case XSD.SHORT: return ClassName.get(Short.class);
            case XSD.BYTE: return ClassName.get(Byte.class);
        }


        ClassNameBuilder nameBuilder = ClassNameBuilder.builder(getResourceAsString());

        // Identifier building is enhanced by rdfs:label literals:
        if(getLabels() != null && !getLabels().isEmpty()) {
            CharSequence preferredLabel = getPreferredRDFSLabel(config);
            if (preferredLabel != null) {
                nameBuilder = nameBuilder.withRDFSLabel(preferredLabel.toString());
            }
        }

        // Add information about incoming and outgoing properties:
        for (ExtendedRDFSProperty incomingProp : incomingProperties) {
            nameBuilder = nameBuilder.withIncomingProperty(incomingProp);
        }
        for (ExtendedRDFSProperty outgoingProp : outgoingProperties) {
            nameBuilder = nameBuilder.withOutgoingProperty(outgoingProp);
        }

        return nameBuilder.className();
    }

    private CharSequence getPreferredRDFSLabel(OntGenerationConfig config) {
        // Get the label in the preferred language:
        CharSequence preferredLabel = null;
        for (CharSequence label : getLabels()) {
            if(config.isPreferredForIdentifiers(label, preferredLabel)) {
                preferredLabel = label;
            }
        }

        return preferredLabel;
    }

    private CharSequence getPreferredRDFSComment(OntGenerationConfig config) {
        CharSequence preferredComment = null;
        for (CharSequence comment : getComments()) {
            if(config.isPreferredForJavaDoc(comment, preferredComment)) {
                preferredComment = comment;
            }
        }

        return preferredComment;
    }

    @Override
    public boolean hasPropertyTransitive(ExtendedRDFSProperty property) {
        // If the class has the property, immediately return:
        if(getOutgoingProperties().contains(property)) {
            return true;
        }

        // Recursively search in superclasses:
        for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
            if(superClazz.hasPropertyTransitive(property)) {
                return true;
            }
        }

        // The property was neither found in this class or any superclass:
        return false;
    }

    @Override
    public TypeSpec buildTypeSpec(OntGenerationConfig config) throws URISyntaxException {
        // First try to find a name for this class:
        ClassName clazzName;
        try {
            clazzName = getJavaPoetClassName(config);
        } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e1) {
            // On error, try to derive at least a package name from the resource:
            String packageName;
            try {
                packageName = ClassNameBuilder.builder(getResourceAsString())
                                              .packageName();
            } catch (URISyntaxException e2) {
                packageName = ""; // Fall back to default package
            }
            // Generate a unique identifier from the resources id:
            clazzName = ClassName.get(packageName, "UnnamedClass" + getResourceAsString().hashCode());
        }

        Collection<TypeName> superClazzNames = new HashSet<>();
        for (ExtendedRDFSClazz superClazz : superClazzes) {
            try {
                superClazzNames.add(superClazz.getJavaPoetClassName(config));
            } catch (IdentifierBuilder.NameBuildingException e1) {
                String packageName;
                try {
                    packageName = ClassNameBuilder.builder(superClazz.getResourceAsString())
                                                  .packageName();
                } catch (URISyntaxException e2) {
                    packageName = "";
                }
                superClazzNames.add(ClassName.get(packageName, "UnnamedClass" + superClazz.getResourceAsString().hashCode()));
            }
        }

        Collection<MethodSpec> getters = new HashSet<>();
        Collection<MethodSpec> setters = new HashSet<>();
        for (ExtendedRDFSProperty property : getOutgoingProperties()) {
            // Check if the property is present in any superclass:
            boolean definedInSuper = false;
            for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
                definedInSuper |= superClazz.hasPropertyTransitive(property);
            }

            // Only add the method to the type spec if it was not already defined in a superclass:
            if(!definedInSuper) {
                getters.add(property.buildGetter(config));
                setters.add(property.buildSetter(config));
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
                        .addJavadoc(javaDoc.build())
                        .build();
    }
}
