package com.github.anno4j.schema.model.rdfs.collections;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

import java.util.List;


/**
 * Implements a list that can be considered finished when persisted to the repository.
 * Objects can be treated as conventional {@link List}s and can be exported as those
 * using {@link #toJavaList()}.
 * For how to create RDF lists please also see the convenience functions in {@link RDFLists}.
 */
@Iri(RDF.LIST)
public interface RDFList extends ResourceObject, Iterable<Object>, List<Object> {

    /**
     * @return The value attached to this list node.
     */
    @Iri(RDF.FIRST)
    Object getFirst();

    /**
     * @param first The value attached to this list node.
     */
    @Iri(RDF.FIRST)
    void setFirst(Object first);

    /**
     * @return The rest of the list, i.e. the next node.
     */
    @Iri(RDF.REST)
    RDFList getRest();

    /**
     * @param rest The next node in the list.
     */
    @Iri(RDF.REST)
    void setRest(RDFList rest);

    /**
     * @return Returns true iff {@link #getFirst()} is not the last element of the list and
     * thus {@link #getRest()} does not return {@code rdf:nil}.
     */
    boolean hasRest();

    /**
     * Converts the list to a Java list. The first element of the returned list is {@link #getFirst()}.
     * @return This list as a Java list.
     */
    List<Object> toJavaList();

    /**
     * Returns the last sublist, which has {@code rdf:nil} as rest.
     * @return The last sublist.
     */
    RDFList getTail();
}
