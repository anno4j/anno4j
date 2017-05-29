package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.util.StronglyConnectedComponents;
import com.github.anno4j.util.JenaSesameUtils;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.squareup.javapoet.JavaFile;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by fischmat on 19.05.17.
 */
public class OWLJavaFileGenerator implements OntologyModelBuilder, JavaFileGenerator {

    private OntModel model;

    /**
     * The Anno4j instance where RDFS information is persisted to.
     */
    private Anno4j anno4j;

    public OWLJavaFileGenerator() throws RepositoryConfigException, RepositoryException {
        this(new Anno4j());
    }

    public OWLJavaFileGenerator(Anno4j anno4j) {
        this.anno4j = anno4j;

        model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
    }

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param rdfInput An input stream to RDF/XML data.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     */
    @Override
    public void addRDF(InputStream rdfInput, String base) {
        model.read(rdfInput, base);
    }

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param url  An URL to RDF data in RDF/XML format.
     * @param base The base uri to be used when converting relative URI's to absolute URI's.
     */
    @Override
    public void addRDF(String url, String base) {
        model.read(url, base);
    }

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param rdfInput An input stream to the RDF data. Its format is defined by the <code>format</code> parameter.
     * @param base     The base uri to be used when converting relative URI's to absolute URI's.
     * @param format   The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     */
    @Override
    public void addRDF(InputStream rdfInput, String base, String format) {
        model.read(rdfInput, base, format);
    }

    /**
     * Adds RDF statements to the model.
     * A subsequent call to {@link #build()} is required for committing the data to the model.
     *
     * @param url    An URL to RDF data in the specified format.
     * @param base   The base uri to be used when converting relative URI's to absolute URI's.
     * @param format The format of the RDF data. One of "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     */
    @Override
    public void addRDF(String url, String base, String format) {
        model.read(url, base, format);
    }

    /**
     * Adds RDF statements to the underlying model.
     *
     * @param url URL to a RDF/XML file containing the RDF data to be added.
     */
    @Override
    public void addRDF(String url) {
        model.read(url);
    }

    /**
     * Returns the buildable named resource objects of RDFS classes that were found during
     * the last call to {@link #build()}.
     *
     * @return Returns the RDFS classes in the model built.
     */
    @Override
    public Collection<BuildableRDFSClazz> getClazzes() throws RepositoryException {
        try {
            ObjectConnection connection = anno4j.getObjectRepository().getConnection();
            ObjectQuery query = connection.prepareObjectQuery(
                    "SELECT DISTINCT ?c {" +
                            "   ?c rdfs:subClassOf+ owl:Thing . " +
                            "   FILTER( isIRI(?c) )" +
                            "}"
            );

            return query.evaluate(BuildableRDFSClazz.class).asSet();

        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns buildable named resource objects of RDFS classes that were found during
     * the last call to {@link #build()} which are pairwise distinct,
     * i.e. that are not declared equivalent.
     * @return All pairwise distinct named classes in the repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public Collection<BuildableRDFSClazz> getDistinctClasses() throws RepositoryException {
        try {
            ObjectConnection connection = anno4j.getObjectRepository().getConnection();
            ObjectQuery query = connection.prepareObjectQuery(
                    "SELECT DISTINCT ?c {\n" +
                    "   ?c rdfs:subClassOf+ owl:Thing . \n" +
                    "   MINUS {\n" +
                    "       ?e owl:equivalentClass ?c . \n" +
                    "       FILTER(str(?e) < str(?c))\n" + // Impose order on equivalence. Pick only first lexicographical
                    "   }\n" +
                    "   FILTER( isIRI(?c) )\n" +
                    "}"
            );

            return query.evaluate(BuildableRDFSClazz.class).asSet();

        } catch (MalformedQueryException | QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns the extended resource objects of RDFS properties that were found during
     * the last call to {@link #build()}.
     *
     * @return Returns the RDFS properties in the model built.
     */
    @Override
    public Collection<BuildableRDFSProperty> getProperties() throws RepositoryException {
        return anno4j.findAll(BuildableRDFSProperty.class);
    }

    /**
     * Returns a validity report for the model build during the last call of {@link #build()}.
     *
     * @return The validity report for the model. Use {@link ValidityReport#isValid()} to
     * check if the model built is valid.
     * @throws IllegalStateException Thrown if the model was not previously built.
     */
    @Override
    public ValidityReport validate() {
        return model.validate();
    }

    /**
     * Builds an ontology model for the RDF data added before using <code>addRDF</code> methods.
     * After a call to this method, the classes and properties in the model can be queried
     * using {@link #getClazzes()} and {@link #getProperties()} respectively.
     *
     * @throws RDFSModelBuildingException Thrown if an error occurs during building the model.
     */
    @Override
    public void build() throws RDFSModelBuildingException {
        // Validate the model constructed so far:
        if(!model.validate().isValid()) {
            throw new RDFSModelBuildingException("The model is not valid.");
        }

        // Copy statements from model to Anno4j:
        try {
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDF.TYPE));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDFS.LABEL));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDFS.COMMENT));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDFS.RANGE));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDFS.DOMAIN));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(RDFS.SUB_CLASS_OF));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(OWL.EQUIVALENT_CLASS));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(OWL.DISJOINT_WITH));
            anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl(OWL.COMPLEMENT_OF));
            anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(OWL.THING));
            anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(OWL.NOTHING));
            anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl(OWL.CLAZZ));


            StmtIterator statementIter = model.listStatements();
            while (statementIter.hasNext()) {
                Statement jenaStatement = statementIter.nextStatement();
                anno4j.getObjectRepository()
                        .getConnection()
                        .add(JenaSesameUtils.asSesameStatement(jenaStatement));
            }

            anno4j.getObjectRepository().getConnection().prepareUpdate(
                    "INSERT {" +
                            "  ?c a rdfs:Class . " +
                            "} WHERE {" +
                            "   { ?p rdfs:domain ?c . } UNION { ?p rdfs:range ?c . } UNION {?c2 rdfs:subClassOf ?c . } UNION {?c rdfs:subClassOf ?c2 . }" +
                            "   UNION { ?c rdfs:subClassOf+ owl:Thing . }" +
                            "}"
            ).execute();
            anno4j.getObjectRepository().getConnection().prepareUpdate(
                    "INSERT {" +
                            "  ?p a rdf:Property . " +
                            "} WHERE {" +
                            "   { ?p a owl:DatatypeProperty . } UNION { ?p a owl:ObjectProperty . } " +
                            "}"
            ).execute();
            // Set owl:Thing as the domain of all properties which have none specified:
            anno4j.getObjectRepository().getConnection().prepareUpdate(
                    "INSERT {" +
                            "   ?p rdfs:domain owl:Thing . " +
                            "} WHERE {" +
                            "   ?p a rdf:Property . " +
                            "   FILTER NOT EXISTS {" +
                            "      ?p rdfs:domain ?c . " +
                            "   }" +
                            "}"
            ).execute();

            normalizeRDFSEquivalence();

        } catch (RepositoryException | IllegalAccessException | InstantiationException | MalformedQueryException | UpdateExecutionException e) {
            throw new RDFSModelBuildingException(e);
        }
    }

    private void normalizeRDFSEquivalence() throws RepositoryException {
        for(Collection<RDFSClazz> scc : StronglyConnectedComponents.findSCCs(anno4j.findAll(RDFSClazz.class))) {
            if(scc.size() > 1) {
                Iterator<RDFSClazz> sccIterator = scc.iterator();
                RDFSClazz root = sccIterator.next(); // Keep the first class, so skip it
                while (sccIterator.hasNext()) {
                    RDFSClazz clazz = sccIterator.next();
                    ObjectConnection connection = anno4j.getObjectRepository().getConnection();

                    try {
                        // Copy statements where the equivalent class is the subject:
                        connection.prepareUpdate(
                        "INSERT {" +
                                "   <" + root.getResourceAsString() + "> ?p ?o ." +
                                "} WHERE {" +
                                "   <" + clazz.getResourceAsString() + "> ?p ?o ." +
                                "}"
                        ).execute();
                        // Copy statements where the equivalent class is the object:
                        connection.prepareUpdate(
                        "INSERT {" +
                                "   ?s ?p <" + root.getResourceAsString() + "> ." +
                                "} WHERE {" +
                                "   ?s ?p <" + clazz.getResourceAsString() + "> ." +
                                "}"
                        ).execute();


                        // Remove the statments involving the class copied from:
                        connection.prepareUpdate(
                                "DELETE WHERE {" +
                                "   <" + clazz.getResourceAsString() +"> ?p ?o ." +
                                "}"
                        ).execute();
                        connection.prepareUpdate(
                        "DELETE WHERE {" +
                                "   ?s ?p <" + clazz.getResourceAsString() +"> ." +
                                "}"
                        ).execute();

                    } catch (UpdateExecutionException | MalformedQueryException e) {
                        throw new RepositoryException(e);
                    }
                }
            }
        }
    }

    /**
     * Checks if the resource is from a standard vocabulary, e.g. RDF or RDFS.
     *
     * @param resource The resource to check.
     * @return Whether the resource is from a special vocabulary.
     */
    private boolean isFromSpecialVocabulary(ResourceObject resource) {
        return resource.getResourceAsString().startsWith(RDF.NS)
                || resource.getResourceAsString().startsWith(RDFS.NS)
                || resource.getResourceAsString().startsWith(XSD.NS)
                || resource.getResourceAsString().startsWith(OWL.NS);
    }

    @Override
    public ObjectConnection getConnection() throws RepositoryException {
        return anno4j.getObjectRepository().getConnection();
    }


    @Override
    public void generateJavaFiles(OntGenerationConfig config, File outputDirectory) throws JavaFileGenerationException, IOException, RepositoryException {
        // Check if the output directory is actually a directory:
        if (!outputDirectory.exists()) {
            // Try to create it:
            if (!outputDirectory.mkdirs()) {
                throw new JavaFileGenerationException("The output directory " + outputDirectory.getAbsolutePath() + " could not be created.");
            }
        } else if (outputDirectory.isFile()) {
            throw new JavaFileGenerationException(outputDirectory.getAbsolutePath() + " must be a directory.");
        }

        // Process the model:
        try {
            build();
        } catch (RDFSModelBuildingException e) {
            throw new JavaFileGenerationException(e);
        }

        // Check if the model is valid:
        if(!validate().isValid()) {
            throw new JavaFileGenerationException("The built model is invalid!");
        }

        for (BuildableRDFSClazz clazz : getDistinctClasses()) {
            // Don't output files for classes that are from RDF/RDFS/... vocab and not for literal types:
            if (!isFromSpecialVocabulary(clazz) && !clazz.isLiteral()) {

                String clazzPackage = clazz.getJavaPackageName();

                JavaFile resourceObjectFile = JavaFile.builder(clazzPackage, clazz.buildTypeSpec(config))
                        .build();

                JavaFile supportFile = JavaFile.builder(clazzPackage, clazz.buildSupportTypeSpec(config))
                        .build();

                resourceObjectFile.writeTo(outputDirectory);
                supportFile.writeTo(outputDirectory);
            }
        }
    }
}
