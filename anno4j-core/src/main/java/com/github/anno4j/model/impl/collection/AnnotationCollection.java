package com.github.anno4j.model.impl.collection;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.AS;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to http://www.w3.org/ns/activitystreams#OrderedCollection.
 *
 * It is often useful to be able to collect Annotations together into a list, called an Annotation Collection.
 * This list, which is always ordered, serves as a means to refer to the Annotations that are contained within it,
 * and to maintain any information about the Collection itself.
 */
@Iri(AS.ORDERED_COLLECTION)
public interface AnnotationCollection extends ResourceObject {

    /**
     * Sets the Set of values for the http://www.w3.org/2000/01/rdf-schema#label relationship.
     *
     * A human readable label intended as the name of the Collection.
     *
     * @param labels    The Set of values to set for the http://www.w3.org/2000/01/rdf-schema#label relationship.
     */
    @Iri(RDFS.LABEL)
    void setLabels(Set<String> labels);

    /**
     * Gets the Set of values currently defined for the http://www.w3.org/2000/01/rdf-schema#label relationship.
     *
     * A human readable label intended as the name of the Collection.
     *
     * @return  The Set of values currently defined for the http://www.w3.org/2000/01/rdf-schema#label relationship.
     */
    @Iri(RDFS.LABEL)
    Set<String> getLabels();

    /**
     * Adds a single value to the currently defined Set for the http://www.w3.org/2000/01/rdf-schema#label relationship.
     *
     * @param label The single label to add to the currently defined values of the
     *              http://www.w3.org/2000/01/rdf-schema#label relationship.
     */
    void addLabel(String label);

    /**
     * Sets the value for the http://www.w3.org/ns/activitystreams#totalItems relationship.
     *
     * The total number of Annotations in the Collection.
     *
     * @param total The value to set for the http://www.w3.org/ns/activitystreams#totalItems relationship.
     */
    @Iri(AS.TOTAL_ITEMS)
    void setTotal(int total);

    /**
     * Gets the currently defined value for the http://www.w3.org/ns/activitystreams#totalItems relationship.
     *
     * The total number of Annotations in the Collection.
     *
     * @return  The value currently defined for the http://www.w3.org/ns/activitystreams#totalItems relationship.
     */
    @Iri(AS.TOTAL_ITEMS)
    int getTotal();

    /**
     * Sets the value of the http://www.w3.org/ns/activitystreams#first relationship.
     *
     * The first page of Annotations that are included within the Collection.
     *
     * @param page  The AnnotationPage to define the http://www.w3.org/ns/activitystreams#first relationship point to.
     */
    @Iri(AS.FIRST)
    void setFirstPage(AnnotationPage page);

    /**
     * Gets the AnnotationPage currently defined at the http://www.w3.org/ns/activitystreams#first relationship.
     *
     * The first page of Annotations that are included within the Collection.
     *
     * @return  The AnnotationPage currently defined for the http://www.w3.org/ns/activitystreams#first relationship.
     */
    @Iri(AS.FIRST)
    AnnotationPage getFirstPage();

    /**
     * Sets the value for the http://www.w3.org/ns/activitystreams#last relationship.
     *
     * The last page of Annotations that are included within the Collection.
     *
     * @param page  The AnnotationPage to set for the http://www.w3.org/ns/activitystreams#last relationship.
     */
    @Iri(AS.LAST)
    void setLastPage(AnnotationPage page);

    /**
     * Gets the AnnotationPage currently defined for the http://www.w3.org/ns/activitystreams#last relationship.
     *
     * The last page of Annotations that are included within the Collection.
     *
     * @return  The AnnotationPage currently defined for the http://www.w3.org/ns/activitystreams#last relationship.
     */
    @Iri(AS.LAST)
    AnnotationPage getLastPage();
}
