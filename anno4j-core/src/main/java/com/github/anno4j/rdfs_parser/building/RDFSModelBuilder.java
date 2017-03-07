package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.util.StronglyConnectedComponents;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.LangString;

import java.io.InputStream;
import java.util.*;

/**
 * Builds an ontology model from RDFS data by inferencing relationships
 * between classes and properties not explicitly stated in the RDF data.
 * Optionally also persists the ontology information to a provided {@link Anno4j}.
 */
class RDFSModelBuilder implements OntologyModelBuilder {

    /**
     * The ontology model without RDFS inference.
     */
    private OntModel model;

    /**
     * The Anno4j instance where RDFS information is persisted to.
     */
    private Anno4j anno4j;

    /**
     * Mapping of resources representing RDFS classes to their extended resource objects.
     */
    private Map<Resource, ExtendedRDFSClazz> clazzes = new HashMap<>();

    /**
     * Mapping of resources representing RDFS properties to their extended resource objects.
     */
    private Map<Resource, ExtendedRDFSProperty> properties = new HashMap<>();

    /**
     * The classes that are direct subclass of rdfs:Class.
     */
    private Collection<ExtendedRDFSClazz> rootClazzes = new HashSet<>();

    /**
     * A report about the validity of the model built by the last call to
     * {@link #build()}.
     */
    private ValidityReport lastValidityReport;

    /**
     * Creates a parser instance with in memory triple store.
     */
    public RDFSModelBuilder() throws RepositoryConfigException, RepositoryException {
        anno4j = new Anno4j();
        model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
    }

    /**
     * Creates a parser instance. Read and inferred RDFS data will be persisted to the given Anno4j instance.
     *
     * @param anno4j The Anno4j instance to work on.
     *               It will contain all inferred ontology information after a call to {@link #build()}.
     */
    public RDFSModelBuilder(Anno4j anno4j) {
        this.anno4j = anno4j;

        // Use a RDFS reasoner for inferring implicit knowledge and wrap the inferred model with a ontology view:
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
        Model inferenceModel = ModelFactory.createInfModel(reasoner, ModelFactory.createDefaultModel());
        model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, inferenceModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(InputStream rdfInput, String base) {
        model.read(rdfInput, base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(String url, String base) {
        model.read(url, base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(InputStream rdfInput, String base, String format) {
        model.read(rdfInput, base, format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(String url, String base, String format) {
        model.read(url, base, format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(String url) {
        model.read(url);
    }

    /**
     * Returns a RDFS class object from the used Anno4j instance.
     * If a class object for the given resource was already created then it will be returned.
     * If no such object exists then it will be created using {@link #anno4j}.
     *
     * @param resource    The resource the returned object should represent.
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @return The RDFS class object for the given resource.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private ExtendedRDFSClazz createRDFSClazzOnDemand(Resource resource, Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
        ExtendedRDFSClazz clazz;
        if (clazzes.containsKey(resource)) {
            clazz = clazzes.get(resource);
        } else {
            clazz = transaction.createObject(ExtendedRDFSClazz.class, (Resource) resource);
            clazzes.put(resource, clazz);
        }
        return clazz;
    }

    /**
     * Returns a RDFS property object from the used Anno4j instance.
     * If a property object for the given resource was already created then it will be returned.
     * If no such object exists then it will be created using {@link #anno4j}.
     *
     * @param resource    The resource the returned object should represent.
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @return The RDFS property object for the given resource.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private ExtendedRDFSProperty createRDFSPropertyOnDemand(Resource resource, Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
        ExtendedRDFSProperty property;
        if (properties.containsKey(resource)) {
            property = properties.get(resource);
        } else {
            property = transaction.createObject(ExtendedRDFSProperty.class, resource);
            properties.put(resource, property);
        }
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ExtendedRDFSClazz> getClazzes() {
        return clazzes.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ExtendedRDFSProperty> getProperties() {
        return properties.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidityReport validate() throws IllegalStateException {
        if (lastValidityReport != null) {
            return lastValidityReport;
        } else {
            throw new IllegalStateException("Model has not been built.");
        }
    }

    /**
     * Converts a Jena {@link RDFNode} representing a string literal into a representation
     * valid for Anno4j.
     *
     * @param node The literal node to convert.
     * @return Returns a {@link LangString} if language information is present. Else a
     * {@link String} is returned.
     */
    private CharSequence getStringLiteral(RDFNode node) {
        if (node.isLiteral()) {
            Literal literal = node.asLiteral();
            if (literal.getLanguage() != null && !literal.getLanguage().isEmpty()) {
                return new LangString(literal.getString(), literal.getLanguage());
            } else { // Untyped literal:
                return literal.getString();
            }
        } else {
            return null;
        }
    }

    /**
     * Extracts the RDFS classes from {@link #model} and constructs {@link ExtendedRDFSClazz}
     * resource objects for them, which are stored in {@link #clazzes}.
     * The resource objects are augmented with rdfs:label, rdfs:comment and rdfs:subClassOf information.
     *
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @param model The ontology model from which the {@link ExtendedRDFSClazz}es should be extracted.
     * @throws RepositoryException    If an error occurs while creating objects <code>transaction</code>.
     * @throws IllegalAccessException If an error occurs while creating objects <code>transaction</code>.
     * @throws InstantiationException If an error occurs while creating objects <code>transaction</code>.
     */
    private void extractRDFSClazzes(Transaction transaction, OntModel model) throws RepositoryException, IllegalAccessException, InstantiationException {
        // Iterate all classes from the inferred ontology
        ExtendedIterator<OntClass> clazzIter = model.listClasses();
        while (clazzIter.hasNext()) {
            OntClass ontClazz = clazzIter.next();
            Resource ontClazzUri = new URIImpl(ontClazz.toString());

            // We're only handling non-property classes here. Those are handled in extractrDFSProperties():
            if (!ontClazz.hasSuperClass(model.createOntResource(RDF.PROPERTY))) {
                // Get the clazz by its resource or create a new Anno4j instance on demand if not yet existing:
                ExtendedRDFSClazz clazz = createRDFSClazzOnDemand(ontClazzUri, transaction);

                // Add rdfs:label and rdfs:comment information:
                ExtendedIterator<RDFNode> labelIter = ontClazz.listLabels(null);
                while (labelIter.hasNext()) {
                    clazz.addLabel(getStringLiteral(labelIter.next()));
                }
                ExtendedIterator<RDFNode> commentIter = ontClazz.listComments(null);
                while (commentIter.hasNext()) {
                    clazz.addComment(getStringLiteral(commentIter.next()));
                }

                // Iterate the direct superclasses of this class and create on demand:
                ExtendedIterator<OntClass> superClazzIter = ontClazz.listSuperClasses(true);
                while (superClazzIter.hasNext()) {
                    OntClass ontSuperClazz = superClazzIter.next();
                    Resource ontSuperClazzUri = new URIImpl(ontSuperClazz.toString());

                    // Add as superclass:
                    ExtendedRDFSClazz superClazz = createRDFSClazzOnDemand(ontSuperClazzUri, transaction);
                    clazz.addSuperclazz(superClazz);
                }

                // Check if the class is a root class:
                boolean isRootClass = clazz.getSuperclazzes().isEmpty();
                if (!clazz.getSuperclazzes().isEmpty()) {
                    // It is a root class if its single superclass is rdfs:Class or rdfs:Resource:
                    ExtendedRDFSClazz firstSuper = clazz.getSuperclazzes().iterator().next();
                    isRootClass |= clazz.getSuperclazzes().size() == 1
                            && (firstSuper.getResourceAsString().equals(RDFS.CLAZZ)
                            || firstSuper.getResourceAsString().equals(RDFS.RESOURCE));
                }
                if (isRootClass) {
                    rootClazzes.add(clazz);
                }
            }
        }
    }

    /**
     * Extracts the RDFS properties from {@link #model} and constructs {@link ExtendedRDFSProperty}
     * resource objects for them, which are stored in {@link #properties}.
     * The resource objects are augmented with rdfs:label, rdfs:comment and rdfs:subPropertyOf information.
     *
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @param model The ontology model from which the {@link ExtendedRDFSClazz}es should be extracted.
     * @throws RepositoryException    If an error occurs while creating objects <code>transaction</code>.
     * @throws IllegalAccessException If an error occurs while creating objects <code>transaction</code>.
     * @throws InstantiationException If an error occurs while creating objects <code>transaction</code>.
     */
    private void extractRDFSProperties(Transaction transaction, OntModel model) throws RepositoryException, IllegalAccessException, InstantiationException {
        ExtendedIterator<OntProperty> propertyIter = model.listOntProperties();
        while (propertyIter.hasNext()) {
            OntProperty ontProperty = propertyIter.next();
            Resource ontPropertyUri = new URIImpl(ontProperty.toString());

            ExtendedRDFSProperty property = createRDFSPropertyOnDemand(ontPropertyUri, transaction);

            // Add rdfs:label and rdfs:comment information:
            ExtendedIterator<RDFNode> labelIter = ontProperty.listLabels(null);
            while (labelIter.hasNext()) {
                property.addLabel(getStringLiteral(labelIter.next()));
            }
            ExtendedIterator<RDFNode> commentIter = ontProperty.listComments(null);
            while (commentIter.hasNext()) {
                property.addComment(getStringLiteral(commentIter.next()));
            }

            ExtendedIterator<? extends OntResource> domainClazzIter = ontProperty.listDomain();
            if (domainClazzIter.hasNext()) {
                while (domainClazzIter.hasNext()) {
                    OntResource ontDomainClazz = domainClazzIter.next();
                    Resource ontDomainClazzUri = new URIImpl(ontDomainClazz.toString());

                    if (ontDomainClazz instanceof OntClass) {
                        ExtendedRDFSClazz domainClazz = createRDFSClazzOnDemand(ontDomainClazzUri, transaction);
                        property.addDomainClazz(domainClazz);
                    }
                }
            } else {
                // No domain specified for this property.
                // Add all root classes as the properties domain (everything not from RDF(S)):
                if (!property.getResourceAsString().startsWith(RDFS.NS) && !property.getResourceAsString().startsWith(RDF.NS)) {
                    for (ExtendedRDFSClazz rootClazz : rootClazzes) {
                        property.addDomainClazz(rootClazz);
                    }
                }
            }

            /*
            Iterate the range classes specified by the inferred model and add them to the property object.
            Not explicitly specified ranges are not set to rdfs:Class by the reasoner. Thus we have to set it manually:
             */
            ExtendedIterator<? extends OntResource> rangeClazzIter = ontProperty.listRange();
            if (rangeClazzIter.hasNext()) { // range explicitly specified?
                while (rangeClazzIter.hasNext()) {
                    OntResource ontRangeClazz = rangeClazzIter.next();
                    Resource ontRangeClazzUri = new URIImpl(ontRangeClazz.toString());

                    if (ontRangeClazz instanceof OntClass) {
                        ExtendedRDFSClazz rangeClazz = createRDFSClazzOnDemand(ontRangeClazzUri, transaction);
                        property.addRangeClazz(rangeClazz);
                    }
                }
            } else {
                // No range specified for this property.
                // Add rdfs:Class as the range of the property, i.e. everything:
                property.addRangeClazz(createRDFSClazzOnDemand(new URIImpl(RDFS.CLAZZ), transaction));
            }

            // Store information about superproperties:
            ExtendedIterator<? extends OntProperty> superPropIter = ontProperty.listSuperProperties();
            while (superPropIter.hasNext()) {
                OntProperty ontSuperProp = superPropIter.next();
                ExtendedRDFSProperty superProp = createRDFSPropertyOnDemand(new URIImpl(ontSuperProp.toString()), transaction);
                property.addSuperproperty(superProp);
            }
        }
    }

    /**
     * Copies the subclass- and property relationships of some classes to another class.
     * The former classes (which are considered equivalent to <code>target</code>)
     * are removed from the {@link OntModel} they belong to.
     * @param target The class which should receive the ontology information of all classes
     *               in <code>equivalents</code>.
     * @param equivalents The classes from which ontology information is copied and which will
     *                    be removed from the ontology model they belong to.
     */
    private void mergeOntClasses(OntClass target, Collection<OntClass> equivalents) {
        for(OntClass equivalentClazz : equivalents) {
            // For all (direct) outgoing properties of the equivalent class,
            // exchange the equivalent class with the target in the domain specification:
            ExtendedIterator<OntProperty> propertyIter = equivalentClazz.listDeclaredProperties(true);
            while (propertyIter.hasNext()) {
                OntProperty property = propertyIter.next();
                property.removeDomain(equivalentClazz);
                property.addDomain(target);
            }

            // Add the superclasses of the equivalent class to the target class:
            ExtendedIterator<OntClass> superClazzIter = equivalentClazz.listSuperClasses(true);
            while(superClazzIter.hasNext()) {
                target.addSuperClass(superClazzIter.next());
            }

            // Remove the class from the OntModel it belongs to:
            equivalentClazz.remove();
        }
    }

    /**
     * Extracts the ontology classes from a {@link OntModel} where this is not directly possible
     * with {@link OntModel#listClasses()}, because the hierarchy may contain cycles.
     * Ignores blank node classes.
     * @param model The model from which to extract classes.
     * @return The classes from the model.
     * @throws RDFSModelBuildingException Thrown if the model is found invalid.
     */
    private Collection<OntClass> getOntClazzesFromModel(OntModel model) throws RDFSModelBuildingException {
        Collection<OntClass> clazzes = new HashSet<>();

        Property subClassOf = model.createProperty(RDFS.SUB_CLASS_OF);
        Property rdfType = model.createProperty(RDF.TYPE);
        OntClass rdfsClazz = model.createClass(RDFS.CLAZZ);

        ResIterator subjectIter = model.listSubjectsWithProperty(subClassOf);
        while (subjectIter.hasNext()) {
            com.hp.hpl.jena.rdf.model.Resource subject = subjectIter.next();

            // By specification the subjects of rdfs:subClassOf have rdf:type rdfs:Class:
            model.add(new StatementImpl(subject, rdfType, rdfsClazz));

            OntClass clazz = model.getOntClass(subject.getURI());

            if(clazz != null) {
                clazzes.add(clazz);
            } else if (!subject.isAnon()){ // Ignore blank nodes
                throw new RDFSModelBuildingException(subject.toString() + " denotes a subject of rdfs:subClassOf statement, but is not a class.");
            }
        }

        NodeIterator objectIter = model.listObjectsOfProperty(subClassOf);
        while (objectIter.hasNext()) {
            RDFNode object = objectIter.next();
            if(object.isResource()) {
                // By specification the objects of rdfs:subClassOf have rdf:type rdfs:Class:
                model.add(new StatementImpl((com.hp.hpl.jena.rdf.model.Resource) object, rdfType, rdfsClazz));

                OntClass clazz = model.getOntClass(((com.hp.hpl.jena.rdf.model.Resource) object).getURI());

                if(clazz != null) {
                    clazzes.add(clazz);
                } else if (!object.isAnon()){ // Ignore blank nodes
                    throw new RDFSModelBuildingException(object.toString() + " denotes a object of rdfs:subClassOf statement, but is not a class.");
                }
            } else {
                throw new RDFSModelBuildingException(object.toString() + " denotes a object of rdfs:subClassOf statement, but is not a resource.");
            }
        }
        return clazzes;
    }

    private void normalizeRDFSEquivalence() throws RDFSModelBuildingException {
        /*
        Classes are considered equivalent by the RDFS specification if their rdfs:subClassOf
        relationship is cyclic.
        This property of being strongly connected implies a equivalence relationship and
        the strongly connected components (SCC) are the equivalence classes.
        Find the SCCs of the inheritance trees in the model:
         */

        /*
        Get the classes of the model.
        The listClasses() method can't be used, because it returns empty if a cycle is contained.
        Find the classes explicitly:
         */
        Collection<OntClass> seeds = getOntClazzesFromModel(model);

        Collection<Collection<OntClass>> sccs = StronglyConnectedComponents.findSCCs(seeds);

        for (Collection<OntClass> scc : sccs) {
            // By definition a node is always strongly connected to itself.
            // Only consider SCCs with more than one components:
            if(scc.size() > 1) {
                // All equivalent classes must be merged into a single one. Take the first:
                Iterator<OntClass> iter = scc.iterator();
                OntClass sccRoot = iter.next();

                // All others are considered equivalent and will be removed by mergeRDFSClasses():
                Collection<OntClass> equivalentClazzes = new HashSet<>();
                while(iter.hasNext()) {
                    equivalentClazzes.add(iter.next());
                }

                // Merge the classes into the root class:
                mergeOntClasses(sccRoot, equivalentClazzes);
            }
        }
    }

    /**
     * Builds an ontology model for the RDF data added before using <code>addRDF</code> methods.
     * The subclass-, subproperty-, domain and range relationships are inferred and information
     * is persisted to the underlying {@link Anno4j} instance if the built model is valid.
     * If the resulting model would not be valid, no information is persisted and
     * {@link #validate()} gives more details about the failed validation.
     *
     * @throws OntologyModelBuilder.RDFSModelBuildingException Thrown if an error occurs during building the model.
     */
    @Override
    public void build() throws OntologyModelBuilder.RDFSModelBuildingException {

        // First merge equivalent classes (the Jena reasoner doesn't do that):
        normalizeRDFSEquivalence();

        // Use a RDFS reasoner for inferring implicit knowledge and wrap the inferred model with a ontology view:
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
        Model inferenceModel = ModelFactory.createInfModel(reasoner, model);
        OntModel inferenceOntModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, inferenceModel);

        try {
            Transaction transaction = anno4j.createTransaction();
            transaction.begin();

            extractRDFSClazzes(transaction, inferenceOntModel);
            extractRDFSProperties(transaction, inferenceOntModel);
            normalizeRDFSEquivalence();

            lastValidityReport = inferenceOntModel.validate();
            if (lastValidityReport.isValid()) {
                transaction.commit();
            } else {
                transaction.rollback();
            }

        } catch (RepositoryException | IllegalAccessException | InstantiationException e) {
            throw new OntologyModelBuilder.RDFSModelBuildingException(e.getMessage());
        }
    }
}
