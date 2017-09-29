package com.github.anno4j.schema.model.rdfs.collections;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Precedes;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.behaviours.RDFSContainer;

import java.util.*;

/**
 * Support class for {@link RDFList}.
 */
@Partial
@Precedes(RDFSContainer.class)
public abstract class RDFListSupport extends ResourceObjectSupport implements RDFList {

    /**
     * In order to check whether there is a rest the rdf:nil list must be queried.
     * This item must be persisted to the Anno4j connected triplestore before it is
     * queried. Otherwise this would result in an exception.
     */
    private static boolean nilCreated;

    /**
     * Creates a {@link RDFList} with URI {@code rdf:nil}.
     */
    private void createNilList() {
        try {
            getObjectConnection().createObject(RDFList.class, new URIImpl(RDF.NIL));
            nilCreated = true;

        } catch (RepositoryException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the {@code rdf:nil} list. Creates it in the repository if necessary.
     * @return The {@code rdf:nil} list.
     */
    private RDFList getNilList() {
        if (!nilCreated) {
            createNilList();
        }
        try {
            return getObjectConnection().findObject(RDFList.class, new URIImpl(RDF.NIL));
        } catch (RepositoryException | QueryEvaluationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean hasRest() {
        // Create the rdf:nil list on first call. Otherwise this would cause an exception with Anno4j:
        if(!nilCreated) {
            createNilList();
        }

        return getRest() != null && !getRest().getResourceAsString().equals(RDF.NIL);
    }

    @Override
    public java.util.List<Object> toJavaList() {
        if(!getResourceAsString().equals(RDF.NIL) && (getFirst() != null || hasRest())) {
            List<Object> result = new ArrayList<>();
            result.add(getFirst());

            if(hasRest()) {
                result.addAll(getRest().toJavaList());
            }

            return result;
        } else {
            return new LinkedList<>();
        }
    }

    /**
     * Returns the last sublist, which has {@code rdf:nil} as rest.
     *
     * @return The last sublist or null if this list is empty.
     */
    @Override
    public RDFList getTail() {
        if(!isEmpty()) {
            RDFList current = this;
            while (current.hasRest()) {
                current = current.getRest();
            }
            return current;
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Object> iterator() {
        return toJavaList().iterator();
    }

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        if(isEmpty()) {
            return 0;
        } else if(!hasRest()) {
            return 1;
        } else {
            return 1 + getRest().size();
        }
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        // A RDF list is empty iff its the rdf:nil list:
        return getResourceAsString().equals(RDF.NIL)
                || (!hasRest() && getFirst() == null);
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public boolean contains(Object o) {
        return (getFirst() != null && getFirst().equals(o))
                || (hasRest() && getRest().contains(o));
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * <p>
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must
     * allocate a new array even if this list is backed by an array).
     * The caller is thus free to modify the returned array.
     * <p>
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in proper
     * sequence
     * @see Arrays#asList(Object[])
     */
    @Override
    public Object[] toArray() {
        return toJavaList().toArray();
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     * <p>
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to <tt>null</tt>.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     * <p>
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * <p>
     * <p>Suppose <tt>x</tt> is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of <tt>String</tt>:
     * <p>
     * <pre>{@code
     *     String[] y = x.toArray(new String[0]);
     * }</pre>
     * <p>
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of this list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException  if the runtime type of the specified array
     *                              is not a supertype of the runtime type of every element in
     *                              this list
     * @throws NullPointerException if the specified array is null
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return toJavaList().toArray(a);
    }

    /**
     * Appends the specified element to the end of this list (optional
     * operation).
     * <p>
     * <p>Lists that support this operation may place limitations on what
     * elements may be added to this list.  In particular, some
     * collections will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added.  List
     * classes should clearly specify in their documentation any restrictions
     * on what elements may be added.
     *
     * @param o element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and this
     *                                       list does not permit null elements
     * @throws IllegalArgumentException      if some property of this element
     *                                       prevents it from being added to this list
     */
    @Override
    public boolean add(Object o) {
        if(!getResourceAsString().equals(RDF.NIL)) {
            // Set value on cleared nodes:
            if(getFirst() == null && !hasRest()) {
                setFirst(o);
                return true;
            }

            RDFList tail = getTail();

            RDFList newTail;
            try {
                newTail = getObjectConnection().createObject(RDFList.class);
            } catch (RepositoryException e) {
                return false;
            }

            tail.setRest(newTail);
            newTail.setFirst(o);
            newTail.setRest(getNilList());

            return true;
        } else {
            throw new IllegalStateException("rdf:nil is constant and can't be extended!");
        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present (optional operation).  If this list does not contain
     * the element, it is unchanged.  More formally, removes the element with
     * the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list changed
     * as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     * @throws ClassCastException            if the type of the specified element
     *                                       is incompatible with this list
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified element is null and this
     *                                       list does not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *                                       is not supported by this list
     */
    @Override
    public boolean remove(Object o) {
        if(o != null) {
            // The element of this node is a match:
            if(getFirst() != null && getFirst().equals(o)) {
                if(!hasRest()) {
                    setFirst(null);
                    return true;
                }

                // Pull all values one in front (overwrite old first value):
                RDFList last = this;
                RDFList current = getRest();
                while (!current.getRest().getResourceAsString().equals(RDF.NIL)) {
                    last.setFirst(current.getFirst());

                    last = current;
                    current = current.getRest();
                }
                last.setFirst(current.getFirst());
                // Cut-off old tail:
                last.setRest(getNilList());

                return true;

            // Remove the element in the rest of the list:
            } else {
                RDFList last = this;
                RDFList current = getRest();

                // While no match and not at end of list:
                while ((current.getFirst() == null || !current.getFirst().equals(o)) && current.hasRest()) {
                    last = current;
                    current = current.getRest();
                }

                // Are we currently at a match (or at end of list)?
                if(current.getFirst().equals(o)) {
                    last.setRest(current.getRest());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if this list contains all of the elements of the
     * specified collection.
     *
     * @param c collection to be checked for containment in this list
     * @return <tt>true</tt> if this list contains all of the elements of the
     * specified collection
     * @throws ClassCastException   if the types of one or more elements
     *                              in the specified collection are incompatible with this
     *                              list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *                              or more null elements and this list does not permit null
     *                              elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>),
     *                              or if the specified collection is null
     * @see #contains(Object)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        boolean containsAll = true;
        Iterator<?> i = c.iterator();
        while (i.hasNext() && containsAll) {
            containsAll = contains(i.next());
        }
        return containsAll;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator (optional operation).  The behavior of this
     * operation is undefined if the specified collection is modified while
     * the operation is in progress.  (Note that this will occur if the
     * specified collection is this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of the specified
     *                                       collection prevents it from being added to this list
     * @throws NullPointerException          if the specified collection contains one
     *                                       or more null elements and this list does not permit null
     *                                       elements, or if the specified collection is null
     * @throws IllegalArgumentException      if some property of an element of the
     *                                       specified collection prevents it from being added to this list
     * @see #add(Object)
     */
    @Override
    public boolean addAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= add(o);
        }
        return changed;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list at the specified position (optional operation).  Shifts the
     * element currently at that position (if any) and any subsequent
     * elements to the right (increases their indices).  The new elements
     * will appear in this list in the order that they are returned by the
     * specified collection's iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the
     * operation is in progress.  (Note that this will occur if the specified
     * collection is this list, and it's nonempty.)
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c     collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of the specified
     *                                       collection prevents it from being added to this list
     * @throws NullPointerException          if the specified collection contains one
     *                                       or more null elements and this list does not permit null
     *                                       elements, or if the specified collection is null
     * @throws IllegalArgumentException      if some property of an element of the
     *                                       specified collection prevents it from being added to this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    @Override
    public boolean addAll(int index, Collection<?> c) {
        if(c.size() == 1) {
            add(index, c.iterator().next());
            return true;
        }

        if(index == 0) {
            if(!getResourceAsString().equals(RDF.NIL)) {
                ArrayList<?> cl = new ArrayList<>(c);

                RDFList oldRest = getRest();
                RDFList newRest;
                RDFList nodeCopy;
                try {
                    newRest = RDFLists.asRDFList(cl.subList(1, cl.size()), getObjectConnection());
                    nodeCopy = getObjectConnection().createObject(RDFList.class);
                } catch (RepositoryException e) {
                    throw new IllegalStateException(e);
                }
                if(getFirst() != null) {
                    nodeCopy.setFirst(getFirst());
                    nodeCopy.setRest(oldRest);
                    newRest.getTail().setRest(nodeCopy);
                } else {
                    newRest.getTail().setRest(getRest());
                }

                setFirst(cl.get(0));
                setRest(newRest);

                return true;

            } else {
                return false;
            }

        } else if (hasRest()){
            return getRest().addAll(index - 1, c);

        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection (optional operation).
     *
     * @param c collection containing elements to be removed from this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of this list
     *                                       is incompatible with the specified collection
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this list contains a null element and the
     *                                       specified collection does not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object o : c) {
            changed |= remove(o);
        }
        return changed;
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this list all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of this list
     *                                       is incompatible with the specified collection
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this list contains a null element and the
     *                                       specified collection does not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        while (!c.contains(getFirst()) && (getFirst() != null || hasRest())) {
            remove(getFirst());
            changed = true;
        }

        if(hasRest()) {
            changed |= getRest().retainAll(c);
            if(getRest().isEmpty()) {
                setRest(getNilList());
            }
        }
        return changed;
    }

    /**
     * Removes all of the elements from this list (optional operation).
     * The list will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *                                       is not supported by this list
     */
    @Override
    public void clear() {
        if (!getResourceAsString().equals(RDF.NIL)) {
            setFirst(null);
            setRest(getNilList());
        }
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public Object get(int index) {
        if(index == 0) {
            return getFirst();
        }
        RDFList current = this;
        while (!current.getResourceAsString().equals(RDF.NIL)) {
            if(index == 0) {
                return current.getFirst();
            }

            current = current.getRest();
            index--;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>set</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and
     *                                       this list does not permit null elements
     * @throws IllegalArgumentException      if some property of the specified
     *                                       element prevents it from being added to this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public Object set(int index, Object element) {
        RDFList current = this;
        while (!current.getResourceAsString().equals(RDF.NIL)) {
            if(index == 0) {
                Object oldValue = current.getFirst();
                current.setFirst(element);
                return oldValue;
            }

            current = current.getRest();
            index--;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and
     *                                       this list does not permit null elements
     * @throws IllegalArgumentException      if some property of the specified
     *                                       element prevents it from being added to this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    @Override
    public void add(int index, Object element) {
        if(index == 0) {
            // Copy this node:
            RDFList newRest;
            try {
                newRest = getObjectConnection().createObject(RDFList.class);
            } catch (RepositoryException e) {
                throw new IllegalStateException(e);
            }
            newRest.setFirst(getFirst());
            newRest.setRest(getRest());

            // Set element to this node and the copy as rest:
            setFirst(element);
            setRest(newRest);

        } else if(hasRest()) {
            getRest().add(index - 1, element);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *                                       is not supported by this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    @Override
    public Object remove(int index) {
        if (index == 0) {
            Object oldValue = getFirst();
            if(hasRest()) {
                RDFList last = this;
                RDFList current = getRest();
                last.setFirst(current.getFirst());

                while (current.hasRest()) {
                    last = current;
                    current = current.getRest();
                    last.setFirst(current.getFirst());
                }
                last.setRest(getNilList());
            } else {
                setFirst(null);
            }
            return oldValue;

        } else if(hasRest()){
            if(index == 1 && getRest().hasRest()) {
                RDFList newRest = getRest().getRest();
                Object removed = getRest().getFirst();
                setRest(newRest);
                return removed;

            } else if (index == 1) {
                Object removed = getRest().getFirst();
                setRest(getNilList());
                return removed;

            } else {
                return getRest().remove(index - 1);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public int indexOf(Object o) {
        if(o == null) {
            throw new NullPointerException();
        }
        if(getFirst() != null && getFirst().equals(o)) {
            return 0;
        } else if(!hasRest()){
            return -1;
        } else {
            int index = getRest().indexOf(o);
            if(index >= 0) {
                return 1 + index;
            } else {
                return -1;
            }
        }
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public int lastIndexOf(Object o) {
        return toJavaList().lastIndexOf(o);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * @return a list iterator over the elements in this list (in proper
     * sequence)
     */
    @Override
    public ListIterator<Object> listIterator() {
        return toJavaList().listIterator();
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @param index index of the first element to be returned from the
     *              list iterator (by a call to {@link ListIterator#next next})
     * @return a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index > size()})
     */
    @Override
    public ListIterator<Object> listIterator(int index) {
        return toJavaList().listIterator(index);
    }

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.<p>
     * <p>
     * This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>{@code
     *      list.subList(from, to).clear();
     * }</pre>
     * Similar idioms may be constructed for <tt>indexOf</tt> and
     * <tt>lastIndexOf</tt>, and all of the algorithms in the
     * <tt>Collections</tt> class can be applied to a subList.<p>
     * <p>
     * The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex   high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *                                   (<tt>fromIndex &lt; 0 || toIndex &gt; size ||
     *                                   fromIndex &gt; toIndex</tt>)
     */
    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return toJavaList().subList(fromIndex, toIndex);
    }
}
