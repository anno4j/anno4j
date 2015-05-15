# anno4j
Read and write API for W3C Open Annotation Data Model (http://www.openannotation.org/spec/core/)


## Configuration



## Persiting

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

## Querying

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
