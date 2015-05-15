# Anno4j

> This library provides programmatic access to read and write [W3C Web Annotation Data Model](http://www.w3.org/TR/annotation-model/) / [W3C Open Annotation Data Model](http://www.openannotation.org/spec/core/) from and to local/remote SPARQL endpoints. An easy-to-use and extensible Java API allows creation and querying of annotations even for non-experts.  

## Features:

- Extensible creation of Web/Open Annotations based on Java Annotations syntax.
- Built-in and predefined implementations for Body and Targets conform to W3C Web Annotation Data Model and W3C Open Annotation Data Model
- Annotations are transformed to RDF and automatically transmitted to local/remote SPARQL using SPARQL Update functionality
- Querying of annotations with path-based criteria
    - [x] Basic Comparisons like (equal, greater and lower)
    - [x] Union of different paths
    - [x] Type condition
    - [ ] Ordering of results
    - [ ] Custom filters

## Introduction
Anno4j is an Java RDF library to easily cope with annotations conform to the Web Annotation Data Model / Open Annotation Data Model. The library provides an extensible way of creating annotations and defines bundled with different body and target implementations. Annotations are automatically persisted on local or remote connected [SPARQL (SPARQL Protocol and RDF Query Language)](http://www.w3.org/TR/sparql11-overview/) endpoints without having to issue any kind of SPARQL query. Besides the creation of annotations, Anno4j also provides an easy-to-use query API based on the path query language [LDPath](http://marmotta.apache.org/ldpath/).

The Web Annotation Data Model / Open Annotation Data Model specification describes a structured model and format to enable (web) annotations to be shared and reused across different hardware and software platform. The model is based on [RDF (Resource Description Framework)](http://www.w3.org/TR/rdf11-primer/), a standard model for data interchange on the Web.

## Install

## Example

## Getting Started

### Configuration

### Restrictions

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

### Query for annotations

anno4j also allows to query triple stores without writing own SPARQL queries. Therefore it provides hibernate like criteria
queries to query against a particular class. Furthermore anno4j is a so-called fluent interface, that allows method chaining
and therefore helps the user to write readable code.

The following code shows how to get the instance of the query service, which is responsible for all provided querying mechanism:


    QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);

This example code shows, that it is necessary to specify the type of the result set by passing the type (Annotation.class) to the
createQueryService method. After retrieving the QueryService object, often the first thing to do is to add namespaces to 
the QueryService by using the addPrefix method. This function requires two parameters: the prefix and the actual url. The
following code would add an example namespace to the query service.

    queryService.addPrefix("ex", "http://www.example.com/schema#");
    
After adding all needed namespaces to the query service, the next step would be to define some criteria. Therefore the QueryService
object provides several methods to add this constraints. Keeping the Open Annotation Data Model in mind, an annotation object
contains to other objects: the body and the target. In turn, the target can also has a Source or a Selector object. For all of these
objects the query service provides own methods to directly specify criteria on the respective node. The naming convention for the
methods is: set(ObjectName)Criteria. To add a criteria to the annotation body, the *setBodyCriteria* function has to be invoked.

To add criteria the given setter needs at first a string value representing the LD Path. LD Path is a simple path-based query language similar 
to XPath or SPARQL Property Paths that is particularly well-suited for querying and retrieving resources from the Linked Data Cloud by 
following RDF links between resources and servers. For example, the following path query would select the names of 
all friends of the context resource [1]:

    foaf:knows / foaf:name :: xsd:string
    
The next parameter the criteria setter methods need, is the actual constraint as string or as number. The last parameter is the comparison method.
anno4j supports all common comparison methods like:

- Equal (Comparison.EQ)
- Greater than (Comparison.GT)
- Greater than or else (Comparison.GTE)
- Lower than (Comparison.LT)
- Lower than or else (Comparison.LTE)

If the comparison method is note provided, the query service will use Comparison.EQ by default. The following example code shows how the setBodyCriteria
function could be invoked:

    queryService.setBodyCriteria("ex:value", "Example Value", Comparison.EQ);

After adding one or multiple criteria the QueryService can be executed. This means, that the QueryService will automatically create a SPARQL query
according to the users namespaces and criteria and use this query to retrieve the data from the triple store. To achieve this, the execute method has
to be invoked. Because anno4j provides a fluent-interface, the code examples from above can be rewritten to the following code example:

    queryService
        .addPrefix("ex", "http://www.example.com/schema#")
        .setBodyCriteria("ex:value", "Example Value")
        .execute();

## Contributors

- Kai Schlegel (University of Passau)
- Andreas Eisenkolb (University of Passau)
- Emanuel Berndl (University of Passau)

> This software was partially developed within the [MICO project](http://www.mico-project.eu/) (Media in Context - European Commission 7th Framework Programme grant agreement no: 610480).

## License
 Apache License Version 2.0 - http://www.apache.org/licenses/LICENSE-2.0

## References

[1] : [https://code.google.com/p/ldpath/](https://code.google.com/p/ldpath/)