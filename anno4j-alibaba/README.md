> Forked from [Openrdf Alibaba Repository](https://bitbucket.org/openrdf/alibaba/overview)

Please visit https://github.com/anno4j/anno4j for detailed information about the Anno4j project.

AliBaba
=======

 AliBaba is an RDF application library for developing complex RDF storage applications. AliBaba is the next generation of the Elmo codebase. It is a collection of modules that provide simplified RDF store abstractions to accelerate development and facilitate application maintenance.
 

 The program models used in today's software are growing in complexity. Most object-oriented models were not designed for the amount of growth and increased scope of today interconnected software agents. This is increasing the cost of new feature development and the cost of maintaining the software model. The object oriented paradigm was designed to model complex systems with complex behaviours, but many of its most powerful concepts (such as specialisation) are too often overlooked when designing distributed systems. By combining the flexibility and adaptivity of RDF with an powerful Object Oriented programming model, [AliBaba's object repository](https://bitbucket.org/openrdf/alibaba/src/master/object-repository/) is able to provide programmers with increased expressivity and a simplified subject-oriented programming environment. This can accelerate the time to market and reduce maintenance cost down the road.

[AliBaba's object server](https://bitbucket.org/openrdf/alibaba/src/master/object-server/) makes these objects available as resources on the Web. Using simple annotations object methods can be mapped to request handlers. This gives increased flexibility and URL manageability by allowing request handlers to be moved and shared among Web resources and endpoints.

 Most RDF stores are optimized for read and bulk load operations, optimizing small concurrent write operations and data consistency is often overlooked. The [Optimistic SAIL of AliBaba](https://bitbucket.org/openrdf/alibaba/src/master/optimistic-sail/) is designed to improve the performance of small write operations by supporting concurrent write transactions and protects your data from data inconsistency issues when snapshot and serializable transactions are enabled.

 Today most people expect to be able to search their data using keywords. However, modern full text indexing has lots of overhead with large indexes and offline indexing. [AliBaba's keyword SAIL](https://bitbucket.org/openrdf/alibaba/src/master/keyword-sail/) allows quick and easy indexing of keywords for RDF resources.

 RDF is designed for metadata and is often accompanied by binary or text documents. Integrating binary and RDF store with consistent states is easier with [AliBaba's two phase commit BLOB store](https://bitbucket.org/openrdf/alibaba/src/master/blob-store).

 AliBaba includes many features not found in Sesame core to facility building complex, modern RDF applications.
