Object Repository
=================

 The Object Repository is an extension to the Sesame RDF Repository that
 allows an RDF store to function as an object store. It maps Java objects
 to and from RDF resources and OWL classes to Java classes in a
 non-intrusive manner that enables developers to work with resources
 stored in an RDF Repository as objects. The Object Repository may also
 optionally be configured with a BLOB store, to store information-resources.

 Sesame Repositories can be created using the console. Use the connect
 command to set the data directory before creating a repository using the
 create command. Once the repository has been created it can be accessed
 in Java through the RepositoryProvider's getRepositoryManager(dataDir)
 method, which takes the directory location that was used in
 the connect command of the console. Then the repository can be accessed
 using the getRepository(id) method of the returned RepositoryManager.

 The ObjectRepository must be created through the
 ObjectRepositoryFactory, using the createRepository method, passing an
 existing Repository. Once the ObjectRepository is created it is like
 other Sesame RDF Repositories, with full triple access, but it returns an
 ObjectConnection in the getConnection method. The ObjectConnection is an
 extension of the RepositoryConnection and includes additional methods
 for working with objects and information-resources. These objects are actually
 just proxies to the ObjectConnection that returned them. However, before
 objects can be returned, object classes must first be created and registered.

 To create classes for the ObjectRepository add the @Iri annotation to
 all classes and fields (or interfaces and property methods)
 that should be stored in the repository. Then
 create an empty 'META-INF/org.openrdf.concepts' file in the root
 directory (or JAR) of the annotated classes. Once the
 classes have been created, as shown in Figure 5,
 they can be used with new ObjectRepositories.
 
Figure 5. A Class Compatible with the ObjectRepository

    // Document.java
    import org.openrdf.annotations.Iri;

    @Iri(Document.NS + "Document")
    public class Document {
      public static final String NS = "http://example.com/rdf/2012/gs#";

      @Iri(NS + "title") String title;

      public String getTitle() {
        return title;
      }
      public void setTitle(String title) {
        this.title = title;
      }
    }
 
 To add an object to the ObjectRepository, create an ObjectConnection and
 call the addObject method (as shown in Figure 6). This method will recursively
 add all other objects referenced from annotated fields. The addObject method can
 either automatically create a unique identifier for the object (that
 might change over time), or add the object using a provided identifier,
 called a URI. If the object implements the interface RDFObject, the identifier
 can also be provided using the getResource method. Note that the getConnection
 method of RDFObject can simple return null by default since it will be
 overridden when retrieved from the store.

 It is recommended to use a URI for any object that might
 need to be referenced directly or has a conceptual identity. For all
 other objects, such as anonymous collections, an automatic identifier
 may be good enough.

 To retrieve an existing object, use the getObject(Class, Resource)
 method of the ObjectConnection. The method accepts a URI or an anonymous
 identifier. An anonymous identifier maybe different for different
 ObjectConnections and should only be used within a single
 ObjectConnection. A URI, however, will never change and can be used in any
 connection. Once the ObjectConnection is closed, the objects it returned
 must be discarded.

 Removing an object is more difficult, as every property of the object
 will need to be removed, by setting the fields or properties to null.
 Furthermore, the type of the object must also be removed from the
 repository, this can be done using the removeDesignation method of the
 ObjectConnection.

Figure 6. Using an ObjectConnection

    // create a repository
    Repository store = new SailRepository(new MemoryStore());
    store.initialize();
    
    // wrap in an object repository
    ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
    ObjectRepository repository = factory.createRepository(store);
    
    // create a Document
    Document doc = new Document();
    doc.setTitle("Getting Started");
    
    // add a Document to the repository
    ObjectConnection con = repository.getConnection();
    ValueFactory vf = con.getValueFactory();
    URI id = vf.createURI("http://example.com/data/2012/getting-started");
    con.addObject(id, doc);
    
    // retrieve a Document by id
    Document doc = con.getObject(Document.class, id);
    
    // remove a Document from the repository
    Document doc = con.getObject(Document.class, id);
    doc.setTitle(null);
    con.removeDesignation(doc, Document.class);
    
    // close everything down
    con.close();
    repository.shutDown();
 
 Objects can also be retrieved by their type using the getObjects(Class)
 method, which includes subclasses. More fine grained queries can be
 created using the @Sparql annotation. This annotation should be placed
 on public or protected methods that have a @Bind annotation on their parameters and have
 a return type and parameters types of registered concepts or datatypes.
 The return type may also be a java.util.Set or Result of a concept or
 datatype and may also be Model and any query result, such as GraphQueryResult,
 TupleQueryResult, or boolean. Public and protected methods with this annotation will be
 overridden with an optimized object query execution. The parameters with
 a @Bind annotation will be available in the query in the variable name
 provided. The target resource is available in the query using the
 variable "$this".

 Dynamic queries can be constructed using the prepareObjectQuery method
 or one of the other prepareQuery methods. The prepareObjectQuery method
 returns an ObjectQuery that allows objects and their type to be assigned to
 variables within the query before execution.

Figure 7. Executing Queries

    // retrieve all Documents
    Result<Document> result = con.getObjects(Document.class);
    while (result.hasNext()) {
      out.println(result.next().getTitle());
    }
    
    import org.openrdf.annotations.Sparql;
    import org.openrdf.annotations.Bind;
    
    // retrieve a Document by title using a named query
    @Sparql("PREFIX gs:<http://example.com/rdf/2012/gs#>\n"+
      "SELECT ?doc WHERE {?doc gs:title $title}")
    public Document findDocumentByTitle(@Bind("title") String title) {
      return null;
    }
    
    
    // retrieve a Document by title using a named query
    ValueFactory vf = con.getRepository().getValueFactory();
    URI myQueryID = vf.createURI("http://example.com/rdf/2012/my-query");
    
    NamedQuery named = con.getRepository().createNamedQuery(myQueryID,
      "PREFIX gs:<http://example.com/rdf/2012/gs#>\n"+
      "SELECT ?doc WHERE {?doc gs:title ?title}");
    
    ObjectQuery query = con.prepareObjectQuery(named.getQueryString());
    query.setObject("title", "Getting Started");
    Document doc = query.evaluate(Document.class).singleResult();
    
    
    // retrieve a Document by title using a dynamic query
    ObjectQuery query = con.prepareObjectQuery(
      "PREFIX gs:<http://example.com/rdf/2012/gs#>\n"+
      "SELECT ?doc WHERE {?doc gs:title ?title}");
    query.setObject("title", "Getting Started");
    Document doc = query.evaluate(Document.class).singleResult();

Concepts
--------

 Concepts are a hierarchical model of resource classes, that include a
 description of supported operations on a type, including syntax and semantics.
 A concept defines the properties and methods available to objects retrieved from
 the store.

 Concepts are Java classes or interfaces that are mapped to an IRI
 (Internationalized Resource Identifier). Concepts can be mapped via an @Iri
 annotation, assigned explicitly in a META-INF/org.openrdf.concepts, or
 at runtime in an ObjectRepositoryConfig passed to an ObjectRepositoryFactory.
 If META-INF/org.openrdf.concepts is empty, the path is searched for Concepts
 with an @Iri annotation. If the file includes a list of class names, only those
 Concepts will be mapped to the value of their @Iri annotation value. If the file
 is a Java properties file that maps Concept class names to one or more IRI, the
 Concepts are mapped to their @Iri value (if present) and its IRIs in the files.

 RDF objects retrieved from the store implement all the concepts that map to one
 of the URI/IRI rdf:type values of the RDF resource. Any Java field, getter
 method, or setter method, on a concept, that includes an @Iri annotation will
 be mapped to an RDF property using the given predicate.

Mixin Behaviours
----------------

 Behaviours are implementations that are mixed into objects, retrieved from the
 store. By using behaviours, code can be organized by what it does, not just by
 what it operations on. All methods and interfaces implemented in a Mixin
 Behaviour are inherited by the relevant objects retrieved from the store. Mixin
 Behaviours may extend other Mixin Behaviours to inherit and possibly override
 the super class method implementations.

 Behaviours are Java classes that are mapped to a Concept. Behaviours can be
 mapped by implementing a Concept interface, assigned to a Concept IRI in a
 META-INF/org.openrdf.behaviours file, or at runtime in an ObjectRepositoryConfig.
 If META-INF/org.openrdf.behaviours is empty, the path is searched for Behaviours
 that implement a Concept with an @Iri annotation. If the file includes a list
 of class names, only those Behaviours will be mapped to the value of their
 implementing Concept @Iri annotation value. If the file is a Java properties
 file that maps Behaviours class names to one or more IRI, the
 Behaviours are mapped to its IRIs in the files.

 Behaviours need not be concrete classes, they can be abstract. Abstract methods
 can be called from within a Behaviour to call methods on the proxy object
 retrieved from the store. Method
 implementations, of large Concept interfaces, can be organized into multiple
 abstract Behaviour classes that implement only particular methods. Concept's
 getter and setter methods, with @Iri annotations, need not be implemented at all.

 Behaviours have the same life cycle as concrete Concepts. They are created
 within an ObjectConnection for a unique object instance. The object instance
 is a proxy for the behaviour assigned to a particular resource in the store.
 The object instance should not be used once the ObjectConnection is closed,
 and multiple object instances maybe created to proxy behaviour for the same
 resource of the store within the same ObjectConnection.

Implementing Inverse Properties
-------------------------------

 To simulate inverse properties in Java use named SPARQL queries for the Java getter and setter.

Figure 8. Inverse Property

    @Iri(FOAF + "Person")
    public interface Person {
      @Iri(FOAF + "depiction")
      Image getDepiction();
      
      @Iri(FOAF + "depiction")
      void setDepiction(Image depiction);
    }
    
    @Iri(FOAF + "Image")
    public interface Image {
      @Sparql(PREFIX + "SELECT ?person { ?person foaf:depiction $this }")
      Person getDepicts();
      
      @Sparql(PREFIX + "DELETE WHERE { ?p foaf:depiction $this } ;\n"+
        "INSERT { $person foaf:depiction $this } WHERE {} ")
      void setDepicts(@Bind("person") Person person);
    }

Collections
-----------

 In RDF the most natural collection is an unordered set, or non-functional
 property. Sets are triples that share the same subject and predicate.
 these non-functional properties should have a java.util.Set property type in
 Java.

 Ordered collections in RDF include rdf:List, rdfs:Container, rdf:Seq, rdf:Alt,
 and rdf:Bag. AliBaba provides a java.util.List interface for each of these
 resource types. However, all ordered collections in the RDF store must include
 an rdf:type on the root node. Often RDF formats that include syntax sugar for
 rdf:List do not include a rdf:type and may not be readable in AliBaba. To add
 the missing add the triple rdf:type rdf:List using the add statement method of
 the ObjectConnection. Other Java collections that implement java.util.List are
 mapped to rdfs:Container when merged into the store.

 Most RDF stores (like SQL databases) are not optimized for generic ordered
 collections. Developers will find unordered collections have significantly
 reduce I/O and better performance. If the elements of an ordered collection
 will only exist in (at most) one ordered collection, it is recommended instead
 to use a typed unordered collection (non-functional property) and included a
 functional index member property on the elements. The elements can then be
 sorted in memory when necessary.

 Figure 9 show an example of using an unordered collection with an explicit
 result order. The method getOrderedChildren() will order the nodes in the RDF
 store (often in memory), while the method getSortedChildren() will sort them in
 Java. In both cases calling java.util.List#add(Object) has no effect on the RDF
 store.

Figure 9. Unordered Collection with element index

    import org.openrdf.annotations.Iri;
    
    @Iri(NS + "Node")
    public interface Node {
	    @Iri(NS + "child")
	    Set<Node> getChildren();
    
	    @Iri(NS + "child")
	    void setChildren(Set<Node> children);
    
	    @Iri(NS + "position")
	    Integer getPosition();
    
	    @Iri(NS + "position")
	    void setPosition(Integer position);
    
	    @Sparql(PREFIX
			    + "SELECT ?child { $this ex:child ?child . ?child ex:position ?position }\n"
			    + "ORDER BY ?position")
	    List<Node> getOrderedChildren();
    
	    List<Node> getSortedChildren();
    }
    
    public abstract class NodeSupport implements Node {
	    public List<Node> getSortedChildren() {
		    Set<Node> live = getChildren();
		    List<Node> memory = new ArrayList<Node>(live);
		    Collections.sort(memory, new Comparator<Node>() {
			    public int compare(Node o1, Node o2) {
				    Integer p1 = o1.getPosition();
				    Integer p2 = o2.getPosition();
				    if (p1 == p2)
					    return 0;
				    if (p1 == null)
					    return -1;
				    if (p2 == null)
					    return 1;
				    return p1.compareTo(p2);
			    }
		    });
		    return memory;
	    }
    }

Aspect Behaviours
-----------------

 AliBaba allows any method call (including getters and setters) to be used as
 join-points. Figure 11 show the Concept interface
 Person (with an @Iri annotation) which defines a join-point (i.e. declared method)
 execution(void setDepiction(Image)) and the abstract Behaviour class PersonSupport
 (implements a Concept interface) defines an aspect for that join-point by using
 the same method name, parameter types, and return type.
 
 Multiple aspects may each have their own implementations for the same
 join-point and, if so, they would be executed serially until all are executed
 or a non-null (nor primitive 0 nor boolean false) response is given. For more
 control over the execution order of aspects, AliBaba
 provides the annotation @Precedes, which can be placed on a behaviour class
 with a list of other behaviour classes, who's aspects should not be executed
 before the aspects of this annotated behaviour class.

 Aspects can also intercept
 method executions by using the annotation @ParameterTypes when declaring a
 method. The annotation should list the parameter types of the join-point, while
 the method parameter type is one of ObjectMessage, BooleanMessage, ByteMessage,
 CharacterMessage, DoubleMessage, FloatMessage, IntegerMessage, LongMessage,
 ShortMessage, and VoidMessage corresponding to the object or primitive return type of the
 method/join-point. When the method is executed the parameters of the method
 call (thus far) and the return type are available through one of the previously
 listed message interfaces. Figure 11 shows an example of an aspect, which
 conditionally changes the response of a method call to ensure it is never null.

 Aspect Behaviours have the same life cycle as Mixin Behaviours and are mapped
 to a Concept in the same way. Like Mixin Behaviours, Aspect Behaviour can be
 abstract, and any interface they implement will also be implemented by the
 relevant objects retrieved from the store. The use of @PameterTypes and/or
 @Precedes annotations distinguish an Aspect Behaviour class from a Mixin
 Behaviour class.

Figure 11. Intercept method call

    @Iri(FOAF + "Person")
    public interface Person {
      @Iri(FOAF + "depiction")
      Image getDepiction();
      
      @Iri(FOAF + "depiction")
      void setDepiction(Image depiction);
    }
    
    public abstract class PersonSupport implements Person, RDFObject {
      @ParameterTypes({})
      public Image getDepiction(ObjectMessage msg) throws RepositoryException {
         Image depiction = (Image) msg.proceed();
         if (depiction == null) {
         	return (Image) getObjectConnection().getObject(DEFAULT_IMAGE_URI);
         }
         return depiction;
      }
      
      void setDepiction(Image depiction) {
        // When @ParameterTypes is not used, proceed() is automatically called
        System.out.println("setDepiction called with: " + depiction);
      }
    }

Advice
------

 Advice is code that is executed around a method execution. Unlike Aspect
 Behaviours, Advice has a static life cycle. It is reused for multiple proxy
 object instances across multiple ObjectConnections. Advice allows the
 implementation of concerns that crosscut many different methods.

 Advice is mapped to method executions based on the retained annotation types
 declared on the method. For example, the Advice in Figure 12 check that the
 caller's code source has permission to call the method.

 List the full class name of an AdviceProvider in the provider services file to
 assign an AdviceFactory to an retained annotation type. The AdviceFactory
 assigns Advice to declared methods, with that annotation type, in objects
 retrieved from the store.

Figure 12. Advice Security

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ProtectedBy {
      String value();
    }
    
    @Iri(FOAF + "Person")
    public interface Person {
      @ProtectedBy("somethingImportantPermission")
      void doSomethingImportant();
    }
    
    public class ProtectedAdvice implements Advice {
      private SecurityManager sm;
      private String directive;
      public ProtectedAdvice(SecurityManager sm, String directive) {
        this.sm = sm;
        this.directive = directive;
      }
      
      public Object intercept(ObjectMessage msg) throws Exception {
        sm.checkSecurityAccess(directive);
        return msg.proceed();
      }
    }
    
    public static class ProtectedAdviceFactory implements AdviceFactory, AdviceProvider {
      private SecurityManager sm = System.getSecurityManager();
      public AdviceFactory getAdviserFactory(Class<?> annotationType) {
        if (sm != null && ProtectedBy.class.equals(annotationType))
          return this;
        return null;
      }
      public Advice createAdvice(Method method) {
        ProtectedBy ann = method.getAnnotation(ProtectedBy.class);
        return new ProtectedAdvice(sm, ann.value());
      }
    }
    
    # META-INF/services/org.openrdf.repository.object.advice.AdviceProvider
    ProtectedAdviceFactory

Information Resources (BLOBs)
-----------------------------

 The method getBlobObject can be used to retrieve a FileObject interface of an
 information resource by URI. The FileObject interface includes methods to open
 an InputStream and an OutputStream and will be stored by URI to the configured
 directory. Changes to the blobs will be isolated from other connections until
 the changes are committed (if not in autoCommit mode).

 The delegate BlobStore can be set using the setBlobStore method of the
 ObjectRepository before use. BlobStores are created using the BlobStoreFactory
 openBlobStore(File) method.

Generating Concepts
-------------------
 
 Compatible class files can be created from RDFS/OWL files, for use with
 the ObjectRepository in Java, by using the provided owl-compiler.sh (or
 .bat) file, with main class
 org.openrdf.repository.object.compiler.Compiler. Use the '-h' option to
 review the available command line options.
 
 When precompiled concept interfaces files are not needed in advance, the
 ObjectRepository can compile them itself. When the AliBaba JARs are
 added to the console, additional repository templates are included to
 facilitate creating the ObjectRepository. These include object-memory
 and object-native (among others). When creating the repository with the
 console, it will prompt for an OWL Ontology file that should contain the
 classes and properties needed and/or reference them using owl:imports
 statements within the file.
 
Figure 13. Creating ObjectRepository from the Console

    Commands end with '.' at the end of a line
    Type 'help.' for help
    > connect data.
    Disconnecting from default data directory
    Connected to data
    > create object-native.
    Please specify values for the following variables:
    Repository ID [native]: foaf
    Repository title [Native store]: FOAF Store
    Rollback if multiple states observed (enforce snapshot)? (false|true) [false]: 
    Rollback if outdated state observed (enforce serializable)? (false|true) [false]: 
    Changeset namespace [urn:trx:localhost:]: 
    Archive all removed data (false|true) [false]: 
    If not, archive transactions with removed triples less than [100]: 
    Minimum recent transactions [100]: 
    Maximum recent transactions [1000]: 
    Triple indexes [spoc,posc]: 
    Max Query Time [0]: 
    Default Query Language [SPARQL]: 
    Ontology [http://www.w3.org/2002/07/owl]: http://xmlns.com/foaf/spec/index.rdf
    Read Schema from Repository [false]: 
    Repository created

    > quit.
    Disconnecting from data
    Bye
 
 Scripts can be streamlined by allowing the ObjectRepository to compile
 the ontology. Shown in Figure 14 is jrunscript (in
 JavaScript) that outputs a new FOAF file, demonstrating how RDF/Objects
 can be used without compiling Java files.
 
Figure 14. JRunScript and ObjectRepository

    $ jrunscript -J-Djava.ext.dirs=lib:dist
    js> var rm = org.openrdf.repository.manager.RepositoryProvider.getRepositoryManager("data")
    js> var repo = rm.getRepository("foaf")
    js> var con = repo.getConnection()
    js> con.setAutoCommit(false)
    js> var Person = "http://xmlns.com/foaf/0.1/Person"
    js> var base = "http://example.com/person/"
    js> var james = con.addDesignation(con.getObject(base+"james"), Person)
    js>
    js> james.foafFirstName.add("James")
    js> james.foafSurname.add("Leigh")
    js> james.foafInterest.add("RDF")
    js> var arjohn = con.addDesignation(con.getObject(base+"arjohn"), Person)
    js> arjohn.foafFirstName.add("Arjohn")
    js> james.foafKnows.add(arjohn)
    js>
    js> con.setNamespace("foaf", "http://xmlns.com/foaf/0.1/")
    js> con['export'](new org.openrdf.rio.rdfxml.RDFXMLWriter(java.lang.System.out), [])
    <?xml version="1.0" encoding="UTF-8"?>
    <rdf:RDF
	    xmlns:foaf="http://xmlns.com/foaf/0.1/"
	    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">

    <rdf:Description rdf:about="http://meta.leighnet.ca/data/rdf/2009/foaf/james">
	    <rdf:type rdf:resource="http://xmlns.com/foaf/0.1/Person"/>
	    <foaf:firstName>James</foaf:firstName>
	    <foaf:surname>Leigh</foaf:surname>
	    <foaf:interest>RDF</foaf:interest>
	    <foaf:knows rdf:resource="http://meta.leighnet.ca/data/rdf/2009/foaf/arjohn"/>
    </rdf:Description>

    <rdf:Description rdf:about="http://meta.leighnet.ca/data/rdf/2009/foaf/arjohn">
	    <rdf:type rdf:resource="http://xmlns.com/foaf/0.1/Person"/>
	    <foaf:firstName>Arjohn</foaf:firstName>
    </rdf:Description>

    </rdf:RDF>
    js> con.close()

Message Vocabulary

 In addition to the RDFS and OWL vocabulary, the object repository also supports
 its own vocabulary for declaring methods (describing messages). This vocabulary
 can be used to declare interface methods. These abstract methods or message classes
 can be created by extending the class msg:Message and creating restrictions for
 its msg:target and response properties (msg:object msg:objectSet msg:literal
 msg:literalSet).

 Method annotations and parameter annotations can be added to the generated
 interfaces by using OWL annotation properties. Generated Java annotations
 are created with String array values.

 To add the @Sparql annotation use the OWL annotation property msg:sparql on
 the message class.
 
 Show in
 Figure 15 is a sample of what a message might look like in turtle.
 
Figure 15. Sample Usage of Message Vocabulary

    @prefix xsd:<http://www.w3.org/2001/XMLSchema#>.
    @prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#>.
    @prefix owl:<http://www.w3.org/2002/07/owl#>.
    @prefix msg:<http://www.openrdf.org/rdf/2011/messaging#>.
    @prefix :<http://example.com/rdf/2012/example#>.

    # Declare classes and properties for this example
    :Mammal rdfs:subClassOf owl:Thing.
    :Person rdfs:subClassOf :Mammal.
    :Dog rdfs:subClassOf :Mammal.

    :dateOfBirth a owl:FunctionalProperty, owl:DatatypeProperty;
	    rdfs:domain :Mammal;
	    rdfs:range xsd:date.

    # Common message that responds with the current date time
    :GetCurrentTime rdfs:subClassOf msg:Message;
	    rdfs:subClassOf [owl:onProperty msg:target; owl:allValuesFrom :Mammal];
	    rdfs:subClassOf [owl:onProperty msg:literal; owl:allValuesFrom xsd:dateTime];
	    msg:sparql "SELECT (now() AS ?now) {}".

    # Declare method that take a date time and responds with the mammal's age at that time
    # This message uses the parameter "when" as a query parameter
    :GetAgeAt rdfs:subClassOf msg:Message;
	    rdfs:subClassOf [owl:onProperty msg:target; owl:allValuesFrom :Mammal];
	    rdfs:subClassOf [owl:onProperty msg:literal; owl:allValuesFrom xsd:integer];
	    msg:sparql """
		    PREFIX :<http://example.com/rdf/2012/example#>
		    SELECT DISTINCT ((year($when) - year(?birth)) AS ?age) {
			    $this :dateOfBirth ?birth
		    }
	    """.

    :when a owl:FunctionalProperty, owl:DatatypeProperty;
	    rdfs:domain :GetAgeAt;
	    rdfs:range xsd:dateTime.

    # Dog's age is calculated differently than other mammals
    # This message is a specialization of the previous and overrides it for all dogs
    :GetDogAgeAt owl:intersectionOf (:GetAgeAt [owl:onProperty msg:target; owl:allValuesFrom :Dog]);
	    msg:sparql """
		    PREFIX :<http://example.com/rdf/2012/example#>
		    SELECT DISTINCT ((year($when) * 7 - year(?birth) * 7) AS ?age) {
			    $this :dateOfBirth ?birth
		    }
	    """.

    # Some sample data to test with
    :jack a :Dog;
	    :dateOfBirth "2005-02-18"^^xsd:date.

    :mel a :Person;
	    :dateOfBirth "1956-01-03"^^xsd:date.

    :lucia a :Person;
	    :dateOfBirth "2009-10-30"^^xsd:date.

 When an object repository is set to use the Ontology in Figure 15, the
 repository in Figure 16 can be used to evaluate the messages and calculate the
 age as shown in Figure 17. Since an empty prefix was used in the ontology, no prefix is used when
 calling messages (or properties). If the schema changes at runtime the method
 ObjectConnection#recompileSchemaOnClose() should be called to compile the
 changes within the ObjectConnection#close() method.
 
Figure 16. Creating ObjectRepository from the Console

    Commands end with '.' at the end of a line
    Type 'help.' for help
    > connect data.
    Disconnecting from default data directory
    Connected to data
    > create object-native.
    Please specify values for the following variables:
    Repository ID [native]: mammals
    Repository title [Native store]: Mammal Store
    Rollback if multiple states observed (enforce snapshot)? (false|true) [false]: 
    Rollback if outdated state observed (enforce serializable)? (false|true) [false]: 
    Changeset namespace [urn:trx:localhost:]: 
    Archive all removed data (false|true) [false]: 
    If not, archive transactions with removed triples less than [100]: 
    Minimum recent transactions [100]: 
    Maximum recent transactions [1000]: 
    Triple indexes [spoc,posc]: 
    Max Query Time [0]: 
    Default Query Language [SPARQL]: 
    Ontology [http://www.w3.org/2002/07/owl]: 
    Read Schema from Repository [false]: true
    Repository created

    > open mammals.
    Opened repository 'mammals'
    mammals> load mammal.ttl.
    Loading data...
    Data has been added to the repository (3048 ms)
    mammals> quit.
    Closing repository 'mammals'...
    Disconnecting from data
    Bye 
 
Figure 17. Calling Object Messages from JavaScript

    $ jrunscript -J-Djava.ext.dirs=lib:dist
    js> var rm = org.openrdf.repository.manager.RepositoryProvider.getRepositoryManager("data")
    js> var repo = rm.getRepository("mammals")
    js> var con = repo.getConnection()
    js> var jack = con.getObject("http://example.com/rdf/2012/example#jack")
    js> var mel = con.getObject("http://example.com/rdf/2012/example#mel")
    js> var lucia = con.getObject("http://example.com/rdf/2012/example#lucia")
    js> var now = jack.GetCurrentTime()
    js> jack.GetAgeAt(now)
    49
    js> mel.GetAgeAt(now)
    56
    js> lucia.GetAgeAt(now)
    3
    js> con.close()

 The ObjectRepository simplifies interacting with RDF resources in OO
 languages on the JVM. By bridging RDF properties and object properties,
 creating and manipulating RDF resources is as easy as manipulating objects.
 

