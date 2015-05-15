# anno4j - Read & Write W3C Web Annotation Data Model / W3C Open Annotation Data Model

> This library provides programmatic access to read and write [W3C Web Annotation Data Model](http://www.w3.org/TR/annotation-model/) / [W3C Open Annotation Data Model](http://www.openannotation.org/spec/core/) from and to local/remote SPARQL endpoints. An easy-to-use and extensible Java API allows creation and querying of annotations even for non-experts.  

Features:

- Extensible creation of Web/Open Annotations based on Java Annotations syntax.
- Built-in and predefined implementations for Body and Targets conform to W3C Web Annotation Data Model and W3C Open Annotation Data Model
- Annotations are transformed to RDF and automatically transmitted to local/remote SPARQL using SPARQL Update functionality
- Querying of annotations with path-based criteria

## Introduction
Anno4j is an Java RDF library to easily cope with annotations conform to the Web Annotation Data Model / Open Annotation Data Model. The library provides an extensible way of creating annotations and defines bundled with different body and target implementations. Annotations are automatically persisted on local or remote connected [SPARQL (SPARQL Protocol and RDF Query Language)](http://www.w3.org/TR/sparql11-overview/) endpoints without having to issue any kind of SPARQL query. Besides the creation of annotations, Anno4j also provides an easy-to-use query API based on the path query language [LDPath](http://marmotta.apache.org/ldpath/).

The Web Annotation Data Model / Open Annotation Data Model specification describes a structured model and format to enable (web) annotations to be shared and reused across different hardware and software platform. The model is based on [RDF (Resource Description Framework)](http://www.w3.org/TR/rdf11-primer/), a standard model for data interchange on the Web.

## Install

## Example

## Getting Started

### Configuration

### Create and save annotations

anno4j uses [AliBaba](https://bitbucket.org/openrdf/alibaba/) to provide an easy way to extend the 
W3C Open Annotation Data Model by simply annotating Plain Old Java Objects (POJOs) with the *@IRI* Java annotation 
(example see: com.github.anno4j.model.impl.annotation.AnnotationDefault.java). To indicate for example that a given 
POJO is a *Annotation*, adding @Iri(OADM.ANNOTATION) directly above the class declaration is enough, where OADM.ANNOTATION is
a predefined constant for the iri: *http://www.w3.org/ns/oa#Annotation* (Other predefined namespaces are 
declared in the *com.github.anno4j.model.ontologie package*). This would lead to the triple when persisting the 
example class using anno4j:

    <http://example.org/exampleAnnotation> rdf:type <http://www.w3.org/ns/oa#Annotation>
 
Declaring the rdf:type of an object is an exceptional case. To specify all other triples, the *@Iri* annotation has to be
added directly above the class attributes that should be stored in the repository. An example for that is the triple:

    <http://example.org/exampleAnnotation> <http://www.w3.org/ns/oa#hasBody> <http://example.org/exampleBody>
    
To specify this triple the attribute body of the *http://www.w3.org/ns/oa#Annotation* simply needs the *@Iri(OADM.HAS_BODY)*
annotation:
    
    @Iri(OADM.HAS_BODY)
    private Body body;

After annotating all needed attributes, the given object can be persisted using anno4j. The following code shows how this 
can be done:


    // Simple Annotation object
    Annotation annotation = new Annotation();
    annotation.setSerializedAt("07.05.2015");

     // persist annotation
     Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

This would lead to the persistence of the annotation object and all of its annotated attributes to the preset repository.  

### Queryi for annotations

anno4j also allows to query triple stores without writing own SPARQL queries. Therefore it provides hibernate like criteria
queries to query against a particular class. Furthermore anno4j is a so-called fluent interface, that allows method chaining
and therefore helps the user to write readable code.

    QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);

- How to Query
    - Fluent interface API (grob beschreiben)
    - shortcut methoden vorstellen
    - how to add prefixes
    - execute
    
    - parameter beschreiben
       - LDPath short introduction
       - verschiedenen Selectortypen 
         
## Contributors

- Kai Schlegel (University of Passau)
- Andreas Eisenkolb (University of Passau)
- Emanuel Berndl (University of Passau)

> This software was partially developed within the [MICO project](http://www.mico-project.eu/) (Media in Context - European Commission 7th Framework Programme grant agreement no: 610480).

## License
 Apache License Version 2.0 - http://www.apache.org/licenses/LICENSE-2.0
