package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSClazzSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for {@link ExtendedRDFSClazz}.
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
    public boolean isDatatype() {
        return hasParent(RDFS.DATATYPE);
    }
}
