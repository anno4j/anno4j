package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds an ontology model from RDFS data by inferencing relationships
 * between classes and properties not explicitly stated in the RDF data.
 * Optionally also persists the ontology information to a provided {@link Anno4j}.
 */
class RDFSModelBuilder
{
    /**
     * Signalizes an error while building the ontology model from a set of RDF statements.
     */
    public class RDFSModelBuildingException extends Exception {
        public RDFSModelBuildingException() {
        }

        public RDFSModelBuildingException(String message) {
            super(message);
        }
    }

    /**
     * The ontology model with underlying RDFS inference.
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
     * Creates a parser instance with in memory triple store.
     */
    public RDFSModelBuilder() throws RepositoryConfigException, RepositoryException {
        anno4j = new Anno4j();
        // Use a RDFS reasoner for inferring implicit knowledge and wrap the inferred model with a ontology view:
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
        Model inferenceModel = ModelFactory.createInfModel(reasoner, ModelFactory.createDefaultModel());
        model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, inferenceModel);
    }

    /**
     * Creates a parser instance. Read and inferred RDFS data will be persisted to the given Anno4j instance.
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
     * Adds RDF statements to the underlying model.
     * @param rdfInput An input stream to RDF/XML data.
     * @param base The base uri to be used when converting relative URI's to absolute URI's.
     */
    public void addRDF(InputStream rdfInput, String base) {
        model.read(rdfInput, base);
    }

    /**
     * Returns a RDFS class object from the used Anno4j instance.
     * If a class object for the given resource was already created then it will be returned.
     * If no such object exists then it will be created using {@link #anno4j}.
     * @param resource The resource the returned object should represent.
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @return The RDFS class object for the given resource.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private ExtendedRDFSClazz createRDFSClazzOnDemand(Resource resource, Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
        ExtendedRDFSClazz clazz;
        if(clazzes.containsKey(resource)) {
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
     * @param resource The resource the returned object should represent.
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @return The RDFS property object for the given resource.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private ExtendedRDFSProperty createRDFSPropertyOnDemand(Resource resource, Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
        ExtendedRDFSProperty property;
        if(properties.containsKey(resource)) {
            property = properties.get(resource);
        } else {
            property = transaction.createObject(ExtendedRDFSProperty.class, resource);
            properties.put(resource, property);
        }
        return property;
    }

    /**
     * Adds RDF statements to the underlying model.
     * @param fileName Path to a RDF/XML file containing the RDF data to be added.
     */
    public void addRDF(String fileName) {
        model.read(fileName);
    }

    /**
     * Returns the extended resource objects of RDFS classes that were found during
     * the last call to {@link #build()}.
     * @return Returns the RDFS classes in the model built.
     */
    public Collection<ExtendedRDFSClazz> getRDFSClazzes() {
        return clazzes.values();
    }

    /**
     * Returns the extended resource objects of RDFS properties that were found during
     * the last call to {@link #build()}.
     * @return Returns the RDFS properties in the model built.
     */
    public Collection<ExtendedRDFSProperty> getProperties() {
        return properties.values();
    }

    /**
     * Returns a validity report for the model build during the last call of {@link #build()}.
     * @return The validatiy report for the model. Use {@link ValidityReport#isValid()} to
     * check if the model built is valid.
     * @throws IllegalStateException Thrown if the model was not previously built.
     */
    public ValidityReport validate() throws IllegalStateException {
        if(model != null) {
            return model.validate();
        } else {
            throw new IllegalStateException("Model has not been built.");
        }
    }

    /**
     * Converts a Jena {@link RDFNode} representing a string literal into a representation
     * valid for Anno4j.
     * @param node The literal node to convert.
     * @return Returns a {@link LangString} if language information is present. Else a
     * {@link String} is returned.
     */
    private CharSequence getStringLiteral(RDFNode node) {
        if(node.isLiteral()) {
            Literal literal = node.asLiteral();
            if(literal.getLanguage() != null && !literal.getLanguage().isEmpty()) {
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
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @throws RepositoryException If an error occurs while creating objects <code>transaction</code>.
     * @throws IllegalAccessException If an error occurs while creating objects <code>transaction</code>.
     * @throws InstantiationException If an error occurs while creating objects <code>transaction</code>.
     */
    private void extractRDFSClazzes(Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
        // Iterate all classes from the inferred ontology
        ExtendedIterator<OntClass> clazzIter = model.listClasses();
        while (clazzIter.hasNext()) {
            OntClass ontClazz = clazzIter.next();
            Resource ontClazzUri = new URIImpl(ontClazz.toString());

            // We're only handling non-property classes here. Those are handled in extractrDFSProperties():
            if(!ontClazz.hasSuperClass(model.createOntResource(RDF.PROPERTY))) {
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
            }
        }
    }

    /**
     * Extracts the RDFS properties from {@link #model} and constructs {@link ExtendedRDFSProperty}
     * resource objects for them, which are stored in {@link #properties}.
     * The resource objects are augmented with rdfs:label, rdfs:comment and rdfs:subPropertyOf information.
     * @param transaction The Anno4j transaction to use when creating resource objects.
     * @throws RepositoryException If an error occurs while creating objects <code>transaction</code>.
     * @throws IllegalAccessException If an error occurs while creating objects <code>transaction</code>.
     * @throws InstantiationException If an error occurs while creating objects <code>transaction</code>.
     */
    private void extractRDFSProperties(Transaction transaction) throws RepositoryException, IllegalAccessException, InstantiationException {
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
            while(domainClazzIter.hasNext()) {
                OntResource ontDomainClazz = domainClazzIter.next();
                Resource ontDomainClazzUri = new URIImpl(ontDomainClazz.toString());

                if(ontDomainClazz instanceof OntClass) {
                    ExtendedRDFSClazz domainClazz = createRDFSClazzOnDemand(ontDomainClazzUri, transaction);
                    property.addDomainClazz(domainClazz);
                }
            }

            /*
            Iterate the range classes specified by the inferred model and add them to the property object.
            Not explicitly specified ranges are not set to rdfs:Class by the reasoner. Thus we have to set it manually:
             */
            ExtendedIterator<? extends OntResource> rangeClazzIter = ontProperty.listRange();
            if(rangeClazzIter.hasNext()) { // range explicitly specified?
                while (rangeClazzIter.hasNext()) {
                    OntResource ontRangeClazz = rangeClazzIter.next();
                    Resource ontRangeClazzUri = new URIImpl(ontRangeClazz.toString());

                    if(ontRangeClazz instanceof OntClass) {
                        ExtendedRDFSClazz rangeClazz = createRDFSClazzOnDemand(ontRangeClazzUri, transaction);
                        property.addRangeClazz(rangeClazz);
                    }
                }
            } else {
                // Add rdfs:Class as the range of the property, i.e. everything:
                property.addRangeClazz(createRDFSClazzOnDemand(new URIImpl(RDFS.CLAZZ), transaction));
            }

            // Store information about superproperties:
            ExtendedIterator<? extends OntProperty> superPropIter = ontProperty.listSuperProperties();
            while (superPropIter.hasNext()) {
                OntProperty ontSuperProp = superPropIter.next();
                ExtendedRDFSProperty superProp = createRDFSPropertyOnDemand(new URIImpl(ontSuperProp.toString()), transaction);

                superProp.addSubProperty(property);
            }
        }
    }

    /**
     * Builds an ontology model for the RDF data added before using <code>addRDF</code> methods.
     * The subclass-, subproperty-, domain and range relationships are inferred and information.
     * is persisted to the underlying {@link Anno4j} instance if the built model is valid.
     * If the resulting model would not be valid, no information is persisted and
     * {@link #validate()} gives more details about the failed validation.
     * @throws RDFSModelBuildingException Thrown if an error occurs during building the model.
     */
    public void build() throws RDFSModelBuildingException {
        try {
            Transaction transaction = anno4j.createTransaction();
            transaction.begin();
            extractRDFSClazzes(transaction);
            extractRDFSProperties(transaction);

            ValidityReport validityReport = validate();
            if(validityReport.isValid()) {
                transaction.commit();
            } else {
                transaction.rollback();
            }

        } catch (RepositoryException | IllegalAccessException | InstantiationException e) {
            throw new RDFSModelBuildingException(e.getMessage());
        }
    }
}
