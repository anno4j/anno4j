# Anno4j

> This library provides programmatic access to read and write [W3C Web Annotation Data Model](http://www.w3.org/TR/annotation-model/) / [W3C Open Annotation Data Model](http://www.openannotation.org/spec/core/) from and to local/remote SPARQL endpoints. An easy-to-use and extensible Java API allows creation and querying of annotations even for non-experts.  

## Build Status
master branch: [![Build Status](https://travis-ci.org/anno4j/anno4j.svg?branch=master)](https://travis-ci.org/anno4j/anno4j) develop branch: [![Build Status](https://travis-ci.org/anno4j/anno4j.svg?branch=develop)](https://travis-ci.org/anno4j/anno4j)

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
    
## Outline
- [Introduction](#introduction)
- [Getting Started](#getting-started)
    - [Install](#install)
    - [Configuration](#configuration)
    - [Create and save annotations](#create-and-save-annotations)
    - [Query for annotations](#query-for-annotations)
    - [Graph Context](#graph-context)
- [Example](#example)
- [Restrictions](#restrictions)
- [Contributors](#contributors)
- [License](#license)

## Introduction
Anno4j is an Java RDF library to easily cope with annotations conform to the Web Annotation Data Model / Open Annotation Data Model. The library provides an extensible way of creating annotations and defines bundled with different body and target implementations. Annotations are automatically persisted on local or remote connected [SPARQL (SPARQL Protocol and RDF Query Language)](http://www.w3.org/TR/sparql11-overview/) endpoints without having to issue any kind of SPARQL query. Besides the creation of annotations, Anno4j also provides an easy-to-use query API based on the path query language [LDPath](http://marmotta.apache.org/ldpath/).

The Web Annotation Data Model / Open Annotation Data Model specification describes a structured model and format to enable (web) annotations to be shared and reused across different hardware and software platform. The model is based on [RDF (Resource Description Framework)](http://www.w3.org/TR/rdf11-primer/), a standard model for data interchange on the Web.

## Getting Started

### Install

1. Add maven dependency (Anno4j is in oss.sonatype.org Repository)
```
      <dependency>
        <groupId>com.github.anno4j</groupId>
        <artifactId>anno4j-core</artifactId>
        <version>1.1.1</version>
      </dependency>
```     
2. Add an empty concept file "org.openrdf.concepts" under your META-INF directory

### Configuration

The central Anno4j object implements the singleton pattern. To get the Anno4j instance, simple call the getInstance() method.

```java
    Anno4j anno4j = Anno4j.getInstance();
```

Default configuration of Anno4j is a local in-memory SPARQL endpoint. Anno4j is based on [Sesame](http://rdf4j.org/). To connect to your local or remote SPARQL endpoint, just create a corresponding repository object (see [here](http://rdf4j.org/sesame/2.7/docs/users.docbook?view#section-repository-api)) and set it in the Anno4j instance.


```java
    anno4j.setRepository(new SPARQLRepository("http://www.mydomain.com/sparql"));
```       

For RDF creation, Anno4j need a central instance for generating unique identifiers. The ID generator needs to implement the com.github.anno4j.persistence.IDGenerator interface. To activate your ID generator, just create a corresponding object and set it in the Anno4j instance.

```java
    anno4j.setIdGenerator(new MyIDGenerator());
```       


### Create and save annotations

Anno4j uses [AliBaba](https://bitbucket.org/openrdf/alibaba/) to provide an easy way to extend the 
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
   
```java
    @Iri(OADM.HAS_BODY)
    private Body body;
```

After annotating all needed attributes, the given object can be persisted using anno4j. The following code shows how this 
can be done:

```java
    // Simple Annotation object
    Annotation annotation = new Annotation();
    annotation.setSerializedAt("07.05.2015");

     // persist annotation
     Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);
```

This would lead to the persistence of the annotation object and all of its annotated attributes to the preset repository.  

### Query for annotations

anno4j also allows to query triple stores without writing own SPARQL queries. Therefore it provides hibernate like criteria
queries to query against a particular class. Furthermore anno4j is a so-called fluent interface, that allows method chaining
and therefore helps the user to write readable code.

The following code shows how to get the instance of the query service, which is responsible for all provided querying mechanism:

```java
    QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
```

This example code shows, that it is necessary to specify the type of the result set by passing the type (Annotation.class) to the
createQueryService method. After retrieving the QueryService object, often the first thing to do is to add namespaces to 
the QueryService by using the addPrefix method, passing the prefix and the actual url:

```java
    queryService.addPrefix("ex", "http://www.example.com/schema#");
```

However, some prefixes are predefined and thereby always available without being specified. These are:

    oa:      <http://www.w3.org/ns/oa#>
    cnt:     <http://www.w3.org/2011/content#>
    dc:      <http://purl.org/dc/elements/1.1/>
    dcterms: <http://purl.org/dc/terms/>
    dctypes: <http://purl.org/dc/dcmitype>
    foaf:    <http://xmlns.com/foaf/0.1/>
    prov:    <http://www.w3.org/ns/prov/>
    rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

After adding all needed namespaces to the query service, the next step would be to define some criteria. Therefore the QueryService
object provides several methods to add this constraints. Keeping the Open Annotation Data Model in mind, an annotation object
contains to other objects: the body and the target. In turn, the target can also has a Source or a Selector object. For all of these
objects the query service provides own methods to directly specify criteria on the respective node. The naming convention for the
methods is: set(ObjectName)Criteria. To add a criteria to the annotation body, the *setBodyCriteria* function has to be invoked.

To add criteria the given setter needs at first a string value representing the [LDPath](http://marmotta.apache.org/ldpath/). LD Path is a simple path-based query language similar 
to XPath or SPARQL Property Paths that is particularly well-suited for querying and retrieving resources from the Linked Data Cloud by 
following RDF links between resources and servers. For example, the following path query would select the names of 
all friends of the context resource:

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

```java
    queryService.setBodyCriteria("ex:value", "Example Value", Comparison.EQ);
```

After adding one or multiple criteria the QueryService can be executed. This means, that the QueryService will automatically create a SPARQL query
according to the users namespaces and criteria and use this query to retrieve the data from the triple store. To achieve this, the execute method has
to be invoked. Because anno4j provides a fluent-interface, the code examples from above can be rewritten to the following code example:

```java
    queryService
        .addPrefix("ex", "http://www.example.com/schema#")
        .setBodyCriteria("ex:value", "Example Value")
        .execute();
```

### Graph Context

You can specify a sub-graph instead of the default graph in the Queryservice or Persistenceservice.

```java

    URI subgraph = new URIImpl("http://www.example.com/subgraph");
    
    QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class, subgraph);
    
    PersistenceService persistenceService = Anno4j.getInstance().createPersistenceService(subgraph);
    
```

## Example

The following will guide through an exemplary process of producing a whole annotation from scratch. The annotation that is
used is conform to the [complete example](http://www.w3.org/TR/2014/WD-annotation-model-20141211/#complete-example) that
is shown at the end of the [Web Annotation Data Model specification](http://www.w3.org/TR/annotation-model/).

Important to note here: As the current status of anno4j does not support multiple instances of some relations (in this example
the body and the motivation), the exemplary annotation does only support one of each. On instances where an entity is not specified
any further, a simple resource URI entity is used (in the example these are *openid1* and *homepage1*).

The first step is to create an annotation, which will be typed accordingly (via the relationship *rdf:type* as an *oa:Annotation*) on its own:

```java
    // Create the base annotation
    Annotation annotation = new Annotation();
```

Then, provenance information is supported for the annotation. The timestamps *oa:annotatedAt* and *oa:serializedAt* are
supported by filling the members of the Annotation class:

```java
    annotation.setAnnotatedAt("2014-09-28T12:00:00Z");
    annotation.setSerializedAt("2013-02-04T12:00:00Z");
```

The motivation is defined by creating a new *Commenting* object, which is then added to the annotation:

```java
    annotation.setMotivatedBy(new Commenting());
```

As the annotation is given by a human being, the provenance feature of an agent, in this case a *foaf:Person*, is utilized.
A *Person* object is created, filled with respective information, and the added to the annotation by setting the *annotatedBy*
field. All of this corresponds to the *agent1* entity of the example, which is connected to the annotation via the relationship
*oa:annotatedBy*.

```java
    // Create the person agent for the annotation
    Person person = new Person();
    person.setName("A. Person");
    person.setOpenID("http://example.org/agent1/openID1");
    
    annotation.setAnnotatedBy(person);
```

In this example, the annotator made use of an homepage to create the annotation. This is implemented by a *prov:SoftwareAgent*
(corresponding to *agent2* in the example), which is also created and then added to the annotation via the field *serializedBy*
(relationship *oa:serializedBy* in the RDF graph).

```java
    // Create the software agent for the annotation
    Software software = new Software();
    software.setName("Code v2.1");
    software.setHomepage("http://example.org/agent2/homepage1");

    annotation.setSerializedBy(software);
```

The next step contains the actual content of the annotation, called the body (bottom left side of the example picture).
As the example is a text annotation, it is typed being a *oa:EmbeddedContent* with the *rdf:format* *"text/plain"*. The
text of the annotation is supported via the *rdf:value* property of the body node, its language is specified by the
relationship *dc:language*.

In anno4j, all this is done by implementing an own body class (extending the abstract class
*Body*). The type of the body is supported in the first line (@Iri("http://www.w3.org/ns/oa#EmbeddedContent")) as a java-annotation,
he respective attributes are implemented using the *@Iri* java-annotation.
See the documentation of the class [here](src/test/java/com/github/anno4j/example/TextAnnotationBody.java).

```java
    @Iri("http://www.w3.org/ns/oa#EmbeddedContent")
    public class TextAnnotationBody extends Body {

        @Iri(DC.FORMAT)   private String format;
        @Iri(RDF.VALUE)   private String value;
        @Iri(DC.LANGUAGE) private String language;

        public TextAnnotationBody() {};

        public TextAnnotationBody(String format, String value, String language) {
            this.format = format;
            this.value = value;
            this.language = language;
        }

        public String getFormat() { return format; }

        public void setValue(String value) { this.value = value; }

        public String getLanguage() { return language; }

        public void setLanguage(String language) { this.language = language; }

        public void setFormat(String format) { this.format = format; }

        public String getValue() { return value; }
    }
```

An instance of this class is then created, filled accordingly, and then added to the annotation as body:

```java
    // Create the body
    TextAnnotationBody body = new TextAnnotationBody("text/plain", "One of my favourite cities", "en");
    annotation.setBody(body);
```

The last thing that has to be added is the target (bottom right side of the picture), the "thing" that the annotation is about. In this case, the target
is specified in a more detailed fashion, as a fragment and not the whole media item is to be selected. This circumstance is
implemented by a combination of specific resource and a selector. A specific resource (which has the *rdf:type* *oa:SpecificResource*)
is an entity, that joins the actual target with its selector. A selector addresses only a spatial or temporal part or
fragment of the given multimedia item. In the case of the example, an *oa:TextPositionSelector* selects a part of the
text that is annotated by stating a start- (relationship *oa:start*) and end position (relationship *oa:end*). Lastly,
the actual target is connected with the specific resource node via an *oa:hasSource* relationship.

In anno4j, a specific resource and a selector has to be created and then joined accordingly:

```java
    // Create the selector
    SpecificResource specificResource = new SpecificResource();

    TextPositionSelector textPositionSelector = new TextPositionSelector(4096, 4104);

    specificResource.setSelector(textPositionSelector);
```

Then, a target is created and then joined with the specific resource node. The last step connects the annotation with
the target. As the target is not specified any further in the example, we make use of a simple resource URI entity.

```java
    // Create the actual target
    StringURLResource source = new StringURLResource("http://example.org/source1");
    specificResource.setSource(source);

    annotation.setTarget(specificResource);
```

The whole example implementation can be seen [here](src/test/java/com/github/anno4j/example/ExampleTest.java).

## Restrictions

For the first version, anno4j does not provide full coverage of the Web Annotation Working Group specification and LD Path for querying.

### Restrictions to the Ontology Model

The current state of anno4j does not support the full functionality that is posed by the specification of the ontology
model used by the Web Annotation Working Group. The restrictions are as follows:

**Multiplicity**

Multiplicity constructs (see [here](http://www.w3.org/TR/2014/WD-annotation-model-20141211/#multiplicity)) are not yet supported.

**Multiple instances**

The specification allows to have multiple instances at certain points, for example an annotation can have multiple bodies,
targets, and/or motivations. This is currently not possible.

**Styles**

Styles, used generally at client side to render annotations accordingly (see [here](http://www.w3.org/TR/2014/WD-annotation-model-20141211/#styles)),
are not yet implemented.

**States**

States are utilised to represent versions of annotations that develop over time (see [here](http://www.w3.org/TR/2014/WD-annotation-model-20141211/#states))
can not be used yet.

### LD Path restrictions

The following selectors are supported for LD Path:
 
**Property Selections**

A path definition selecting the value of a property. Either a URI enclosed in <> or a namespace prefix and a local name separated by :

    <URI> | PREFIX:LOCAL


**Path Traversal**

Traverse a path by following several edges in the RDF graph. Each step is separated by a /.

    PATH / PATH

**Unions**

Several alternative paths can be merged by using a union | between path elements

    PATH | PATH

**Groupings**

Path expressions can be grouped to change precedence or to improve readability by including them in braces:

    ( PATH )

**Value Testing**

The values of selections can be tested and filtered by adding test conditions in square brackets [] after a path selection:

    PATH [TEST]

More precisely, anno4j currently supports only the specific **is-a Test**. 

    PATH[is-a VALUE]
 
    
For the next version of anno4j it is planned to integrate more of these tests. For examples and a detailed documentation visit: [http://marmotta.apache.org/ldpath/language.html](http://marmotta.apache.org/ldpath/language.html).

**Reverse Property Selections**

This is the reverse/inverse operation of the normal Property Selection.

Select all nodes connected to the current node via an incoming link, aka. go the specified link “backwards”:

    ^<URI>

**Recursive Selections**

Recurseive selection will apply an selectore recursively. 

	(<SELECTOR>)*  //  zero-and-more 
	(<SELECTOR>)+  //  one-and-more 



## Contributors

- Kai Schlegel (University of Passau)
- Andreas Eisenkolb (University of Passau)
- Emanuel Berndl (University of Passau)

> This software was partially developed within the [MICO project](http://www.mico-project.eu/) (Media in Context - European Commission 7th Framework Programme grant agreement no: 610480).

## License
 Apache License Version 2.0 - http://www.apache.org/licenses/LICENSE-2.0
 