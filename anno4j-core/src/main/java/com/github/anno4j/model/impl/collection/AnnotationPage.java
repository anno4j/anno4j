package com.github.anno4j.model.impl.collection;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.AS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to http://www.w3.org/ns/activitystreams#OrderedCollectionPage.
 *
 * An Annotation Page is part of an Annotation Collection, and has an ordered list of some or all of the Annotations
 * that are within the Collection. Each Collection may have multiple pages, and these are traversed by following the
 * next and prev links between the pages.
 */
@Iri(AS.ORDERED_COLLECTION_PAGE)
public interface AnnotationPage extends ResourceObject {

    /**
     * Sets the value of the http://www.w3.org/ns/activitystreams#partOf relationship.
     *
     * The relationship between the Page and the Annotation Collection that it is part of.
     *
     * @param collection  The value to set for the http://www.w3.org/ns/activitystreams#partOf relationship.
     */
    @Iri(AS.PART_OF)
    void setPartOf(AnnotationCollection collection);

    /**
     * Gets the value currently set for the http://www.w3.org/ns/activitystreams#partOf relationship.
     *
     * The relationship between the Page and the Annotation Collection that it is part of.
     *
     * @return  The value currently defined for the http://www.w3.org/ns/activitystreams#partOf relationship.
     */
    @Iri(AS.PART_OF)
    AnnotationCollection getPartOf();

    /**
     * Sets the Set of values for the http://www.w3.org/ns/activitystreams#items relationship.
     *
     * The list of Annotations that are the members of the Page.
     *
     * @param items   The Set of values to set for the http://www.w3.org/ns/activitystreams#items relationship.
     */
    @Iri(AS.ITEMS)
    void setItems(Set<Annotation> items);

    /**
     * Gets the Set of values currently defined for the http://www.w3.org/ns/activitystreams#items relationship.
     *
     * The list of Annotations that are the members of the Page.
     *
     * @return  The Set of values currently defined for the http://www.w3.org/ns/activitystreams#items relationship.
     */
    @Iri(AS.ITEMS)
    Set<Annotation> getItems();

    /**
     * Adds a single value to the Set of values currently defined for the http://www.w3.org/ns/activitystreams#items
     * relationship.
     *
     * @param annotation    The Annotation to add to the Set of Annotations currently defined for the
     *                      http://www.w3.org/ns/activitystreams#items relationship.
     */
    void addItem(Annotation annotation);

    /**
     * Sets the value for the http://www.w3.org/ns/activitystreams#next relationship.
     *
     * A reference to the next Page in the sequence of pages that make up the Collection.
     *
     * @param page  The AnnotationPage to set the http://www.w3.org/ns/activitystreams#next relationship to.
     */
    void setNext(AnnotationPage page);

    void setNextSymmetric(AnnotationPage page);

    /**
     * Gets the value currently defined for the http://www.w3.org/ns/activitystreams#next relationship.
     *
     * A reference to the next Page in the sequence of pages that make up the Collection.
     *
     * @return  The AnnotationPage that is currently set for the http://www.w3.org/ns/activitystreams#next
     *          relationship.
     */
    AnnotationPage getNext();

    /**
     * Sets the value for the http://www.w3.org/ns/activitystreams#prev relationship.
     *
     * A reference to the previous Page in the sequence of pages that make up the Collection.
     *
     * @param page  The AnnotationPage to set the http://www.w3.org/ns/activitystreams#prev relationship to.
     */
    void setPrev(AnnotationPage page);

    void setPrevSymmetric(AnnotationPage page);

    /**
     * Gets the value currently defined for the http://www.w3.org/ns/activitystreams#prev relationship.
     *
     * A reference to the previous Page in the sequence of pages that make up the Collection.
     *
     * @return  The AnnotationPage that is currently set for the http://www.w3.org/ns/activitystreams#prev
     *          relationship.
     */
    AnnotationPage getPrev();

    /**
     * Sets the value for the http://www.w3.org/ns/activitystreams#startIndex property.
     *
     * The relative position of the first Annotation in the items list, relative to the Annotation Collection.
     * The first entry in the first page is considered to be entry 0.
     *
     * @param index The value to set for the http://www.w3.org/ns/activitystreams#startIndex property.
     */
    @Iri(AS.START_INDEX)
    void setStartIndex(int index);

    /**
     * Gets the value currently defined for the http://www.w3.org/ns/activitystreams#startIndex property.
     *
     * The relative position of the first Annotation in the items list, relative to the Annotation Collection.
     * The first entry in the first page is considered to be entry 0.
     *
     * @return  The value currently defined for the http://www.w3.org/ns/activitystreams#startIndex property.
     */
    @Iri(AS.START_INDEX)
    int getStartIndex();
}
