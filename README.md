# Anno4j

> This library mainly provides programmatic access to the [W3C Web Annotation Data Model](http://www.w3.org/TR/annotation-model/) (formerly known as the [W3C Open Annotation Data Model](http://www.openannotation.org/spec/core/)) to allow Annotations to be written from and to local or remote SPARQL endpoints. An easy-to-use and extensible Java API allows creation and querying of Annotations even for non-experts. This API is augmented with various supporting functionalities to increase the usability of using the W3C Web Annotations.
> 
> With the last iteration, Anno4j has also been developed to be able to work with generic metadata models. It is now possible to parse a RDFS or OWL Lite schema and generate the respective Anno4j classes on the fly via code generation.

## Build Status
master branch: [![Build Status](https://travis-ci.org/anno4j/anno4j.svg?branch=master)](https://travis-ci.org/anno4j/anno4j) develop branch: [![Build Status](https://travis-ci.org/anno4j/anno4j.svg?branch=develop)](https://travis-ci.org/anno4j/anno4j)

## Table of Content

The use of the Anno4j library and its features is documented in the respective [GitHub Anno4j Wiki](https://github.com/anno4j/anno4j/wiki). Its features are the following:

- Extensible creation of Web/Open Annotations based on Java Annotations syntax (see [Getting Started](https://github.com/anno4j/anno4j/wiki/Getting-started))
- Built-in and predefined implementations for nearly all RDF classes conform to the W3C Web Annotation Data Model
- Created (and annotated) Java POJOs are transformed to RDF and automatically transmitted to local/remote SPARQL 1.1 endpoints using the SPARQL Update functionality
- Querying of annotations with path-based criteria (see [Querying](https://github.com/anno4j/anno4j/wiki/Querying))
    - [x] Basic comparisons like "equal", "greater", and "lower"
    - [x] String comparisons: "equal", "contains", "starts with", and "ends with"
    - [x] Union of different paths
    - [x] Type condition
    - [x] Custom filters
- Addition of custom behaviours of otherwise simple Anno4j classes through partial/support classes (see [Support Classes](https://github.com/anno4j/anno4j/wiki/Support-Classes))
- Input and Output to and from different standardised RDF serialisation standards (see [RDF Input and Output](https://github.com/anno4j/anno4j/wiki/RDF-Input-and-Output))
- Parsing of RDFS or OWL Lite schemata to automatically generate respective Anno4j classes (see [Java File Generation](https://github.com/anno4j/anno4j/wiki/Java-File-Generation))
- Schema/Validation annotations that can be added to Anno4j classes to induce schema-correctness which is indicated at the point of creation (see [Schema Validation](https://github.com/anno4j/anno4j/wiki/Schema-Validation) and [Schema Annotations](https://github.com/anno4j/anno4j/wiki/Schema-Annotations))
- A tool to support the generation of so-called proxy classes, that speed up the creation of instances of large and deep schemata

## Status of Anno4j and the implemented WADM specification

The current version 2.4 of Anno4j supports the [most current W3C recommendation of the Web Annotation Data Model](https://www.w3.org/TR/annotation-model/).

## Development Guidelines

### Snapshot
Each push on the development branch triggers the build of a snapshot version. Snapshots are publicly available:
```xml
	<dependency> 
	<groupId>com.github.anno4j</groupId>
   	<artifactId>anno4j-core</artifactId>
   	<version>X.X.X-SNAPSHOT</version>
	</dependency>
```     

### Compile, Package and Install

Package with:
```
      mvn package
```     

Install to your local repository
```
      mvn install
```     

### Participate
1. Create an issue
2. Fork Anno4j
3. Add features
4. Add JUnit Tests
5. Create pull request to anno4j/develop


### 3rd party integration of custom LDPath expressions

To contribute custom LDPath (test) functions and thereby custom LDPath syntax, the following two classes have to be provided:

1. Step: 

Create a Java class that extends either the *SelectorFunction* class or the *TestFunction* class. This class defines the actual syntax
that has to be injected into the Anno4j evaluation process.

```java
    public class GetSelector extends SelectorFunction<Node> {
    
        @Override
        protected String getLocalName() {
            return "getSelector";
        }
    
        @Override
        public Collection<Node> apply(RDFBackend<Node> backend, Node context, Collection<Node>... args) throws IllegalArgumentException {
            return null;
        }
    
        @Override
        public String getSignature() {
            return "fn:getSelector(Annotation) : Selector";
        }
    
        @Override
        public String getDescription() {
            return "Selects the Selector of a given annotation object.";
        }
    }

``` 

2. Step:

Create a Java class that actually evaluates the newly provided LDPath expression. This class needs
to be flagged with the *@Evaluator* Java annotation. The *@Evaluator* annotation requires the class 
of the description mentioned in the first step. Besides that, the evaluator has to implement either
the *QueryEvaluator* or the *TestEvaluator* interface. Inside the prepared evaluate method, the actual
SPARQL query has to be generated using the Apache Jena framework.

```java
    @Evaluator(GetSelector.class)
    public class GetSelectorFunctionEvaluator implements QueryEvaluator {
        @Override
        public Var evaluate(NodeSelector nodeSelector, ElementGroup elementGroup, Var var, LDPathEvaluatorConfiguration evaluatorConfiguration) {
            Var evaluate = new SelfSelectionEvaluator().evaluate(nodeSelector, elementGroup, var, evaluatorConfiguration);
            Var target = Var.alloc("target");
            Var selector = Var.alloc("selector");
    
            elementGroup.addTriplePattern(new Triple(evaluate.asNode(), new ResourceImpl(OADM.HAS_TARGET).asNode(), target));
            elementGroup.addTriplePattern(new Triple(target.asNode(), new ResourceImpl(OADM.HAS_SELECTOR).asNode(), selector));
            return selector;
        }
    }
``` 

## Contributors

- Kai Schlegel (University of Passau)
- Andreas Eisenkolb (University of Passau)
- Emanuel Berndl (University of Passau)
- Thomas Weißgerber (University of Passau)
- Matthias Fisch (University of Passau)

> This software was partially developed within the [MICO project](http://www.mico-project.eu/) (Media in Context - European Commission 7th Framework Programme grant agreement no: 610480) and the [ViSIT project](http://www.phil.uni-passau.de/dh/projekte/visit/) (Virtuelle Verbund-Systeme und Informations-Technologien für die touristische Erschließung von kulturellem Erbe - Interreg Österreich-Bayern 2014-2020, project code: AB78).

## License
 Apache License Version 2.0 - http://www.apache.org/licenses/LICENSE-2.0
 
