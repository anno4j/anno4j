package com.github.anno4j.schema.model.rdfs.collections;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

/**
 * Test for {@link RDFList}.
 */
public class RDFListTest {

    private Anno4j anno4j;

    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        connection = anno4j.getObjectRepository().getConnection();
    }

    @Test
    public void testHasRest() throws Exception {
        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class),
                anno4j.createObject(Annotation.class));

        assertTrue(list.hasRest());
        assertFalse(list.getRest().hasRest());

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertFalse(nilList.hasRest());
    }

    @Test
    public void testToJavaList() throws Exception {
        List<ResourceObject> javaList = new ArrayList<>();
        javaList.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")));
        javaList.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2")));
        javaList.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3")));

        RDFList list = RDFLists.asRDFList(connection, javaList.get(0), javaList.get(1), javaList.get(2));
        assertEquals(javaList, list.toJavaList());

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertEquals(new ArrayList<ResourceObject>(), nilList.toJavaList());

        RDFList clearedList = anno4j.createObject(RDFList.class);
        clearedList.setRest(nilList);
        clearedList.setFirst(null);
        assertEquals(new ArrayList<ResourceObject>(), clearedList.toJavaList());
    }

    @Test
    public void testGetTail() throws Exception {
        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3")));

        RDFList tail = list.getTail();
        assertFalse(tail.hasRest());
        assertEquals("urn:test:anno3", ((ResourceObject) tail.getFirst()).getResourceAsString());

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertNull(nilList.getTail());

        RDFList singleElement = RDFLists.asRDFList(connection, anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4")));
        assertEquals(singleElement, singleElement.getTail());
    }

    @Test
    public void testSize() throws Exception {
        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3")));

        assertEquals(3, list.size());
        assertEquals(2, list.getRest().size());
        assertEquals(1, list.getRest().getRest().size());

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertEquals(0, nilList.size());

        RDFList clearedList = anno4j.createObject(RDFList.class);
        clearedList.setRest(nilList);
        clearedList.setFirst(null);
        assertEquals(0, clearedList.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2")),
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3")));
        assertFalse(list.isEmpty());

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertTrue(nilList.isEmpty());

        RDFList clearedList = anno4j.createObject(RDFList.class);
        clearedList.setRest(nilList);
        clearedList.setFirst(null);
        assertTrue(clearedList.isEmpty());
    }

    public void testContains() throws Exception {
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")),
                anno2,
                anno3);

        assertTrue(list.contains(anno2));
        assertTrue(list.contains(anno3));
        assertFalse(list.contains(anno4));
        assertFalse(list.contains(new Integer(42)));
    }

    @Test
    public void testToArrayTypeless() throws Exception {
        Annotation anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        Annotation anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));

        RDFList list = RDFLists.asRDFList(connection,
                anno1,
                anno2);

        Object[] array = list.toArray();
        assertEquals(2, array.length);
        assertEquals(anno1, array[0]);
        assertEquals(anno2, array[1]);
    }

    @Test
    public void testToArray() throws Exception {
        Annotation anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        Annotation anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));

        RDFList list = RDFLists.asRDFList(connection,
                anno1,
                anno2);

        Annotation[] array = list.toArray(new Annotation[2]);
        assertEquals(2, array.length);
        assertEquals(anno1, array[0]);
        assertEquals(anno2, array[1]);
    }

    @Test
    public void testAdd() throws Exception {
        Annotation anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        Annotation anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        Annotation anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));

        RDFList list = RDFLists.asRDFList(connection,
                anno1,
                anno2);

        list.add(anno3);
        assertEquals(3, list.size());
        assertEquals(anno1, list.get(0));
        assertEquals(anno2, list.get(1));
        assertEquals(anno3, list.get(2));

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        boolean exceptionThrown = false;
        try {
            nilList.add(anno3);
        } catch (IllegalStateException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        RDFList clearedList = anno4j.createObject(RDFList.class);
        clearedList.setRest(nilList);
        clearedList.setFirst(null);
        clearedList.add(anno3);

        assertEquals(anno3, clearedList.getFirst());
        assertEquals(1, clearedList.size());
        assertFalse(clearedList.hasRest());
    }

    @Test
    public void testRemove() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno2, anno3);
        assertTrue(list.remove(anno2));
        assertEquals(3, list.size());
        assertEquals(anno1, list.get(0));
        assertEquals(anno2, list.get(1));
        assertEquals(anno3, list.get(2));

        assertTrue(list.remove(anno1));
        assertEquals(2, list.size());
        assertEquals(anno2, list.get(0));
        assertEquals(anno3, list.get(1));

        assertFalse(list.remove(anno1));
        assertEquals(2, list.size());

        assertTrue(list.remove(anno3));
        assertEquals(1, list.size());
        assertEquals(anno2, list.get(0));

        assertTrue(list.remove(anno2));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testContainsAll() throws Exception {
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        Collection<ResourceObject> testSet1 = Sets.newHashSet(anno2, anno3);
        Collection<ResourceObject> testSet2 = Sets.newHashSet(anno2, anno3, anno4);

        RDFList list = RDFLists.asRDFList(connection,
                anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")),
                anno2,
                anno3);

        assertTrue(list.containsAll(testSet1));
        assertFalse(list.containsAll(testSet2));
    }

    @Test
    public void testAddAll() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2);
        list.addAll(Sets.newHashSet(anno3, anno4));
        assertEquals(4, list.size());
        assertEquals(anno3, list.get(2));
        assertEquals(anno4, list.get(3));

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        boolean exceptionThrown = false;
        try {
            nilList.addAll(Sets.newHashSet(anno3, anno4));
        } catch (IllegalStateException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testAddAllAtIndex() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));
        ResourceObject anno5 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno5"));


        RDFList list = RDFLists.asRDFList(connection, anno1, anno2);
        assertTrue(list.addAll(1, Lists.newArrayList(anno3, anno5)));
        assertEquals(4, list.size());
        assertEquals(anno1, list.get(0));
        assertEquals(anno3, list.get(1));
        assertEquals(anno5, list.get(2));
        assertEquals(anno2, list.get(3));

        assertTrue(list.addAll(0, Lists.newArrayList(anno4, anno3)));
        assertEquals(6, list.size());
        assertEquals(anno4, list.get(0));
        assertEquals(anno3, list.get(1));
        assertEquals(anno1, list.get(2));
        assertEquals(anno3, list.get(3));
        assertEquals(anno5, list.get(4));
        assertEquals(anno2, list.get(5));

        boolean exceptionThrown = false;
        try {
            list.addAll(6, Lists.newArrayList(anno5));
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        RDFList nilList = anno4j.createObject(RDFList.class, (Resource) new URIImpl(RDF.NIL));
        assertFalse(nilList.addAll(0, Lists.newArrayList(anno3, anno4)));
    }

    @Test
    public void testRemoveAll() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));
        ResourceObject anno5 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno5"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno4);

        assertFalse(list.removeAll(Lists.newArrayList(anno5)));

        assertTrue(list.removeAll(Lists.newArrayList(anno4, anno5)));
        assertEquals(3, list.size());
        assertEquals(anno1, list.get(0));
        assertEquals(anno2, list.get(1));
        assertEquals(anno3, list.get(2));

        assertTrue(list.removeAll(Lists.newArrayList(anno1)));
        assertEquals(2, list.size());
        assertEquals(anno2, list.get(0));
        assertEquals(anno3, list.get(1));

        assertTrue(list.removeAll(Lists.newArrayList(anno2, anno3)));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRetainAll() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno4);

        assertFalse(list.retainAll(Lists.newArrayList(anno1, anno2, anno3, anno4)));

        assertTrue(list.retainAll(Lists.newArrayList(anno1, anno2)));
        assertEquals(2, list.size());
        assertEquals(anno1, list.get(0));
        assertEquals(anno2, list.get(1));

        assertTrue(list.retainAll(Lists.newArrayList(anno2)));
        assertEquals(1, list.size());
        assertEquals(anno2, list.get(0));
    }

    @Test
    public void testClear() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2);
        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    public void testGet() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno4);
        assertEquals(anno1, list.get(0));
        assertEquals(anno2, list.get(1));
        assertEquals(anno4, list.get(3));

        boolean exceptionThrown = false;
        try {
            list.get(-1);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            list.get(4);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSet() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3);
        assertEquals(anno2, list.set(1, anno4));
        assertEquals(anno1, list.set(0, anno4));
        assertEquals(anno3, list.set(2, anno4));

        assertEquals(3, list.size());
        assertEquals(Lists.newArrayList(anno4, anno4, anno4), list.toJavaList());

        boolean exceptionThrown = false;
        try {
            list.set(-1, anno4);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            list.set(3, anno4);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testAddAtIndex() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3);

        list.add(1, anno4);
        assertEquals(Lists.newArrayList(anno1, anno4, anno2, anno3), list.toJavaList());

        list.add(0, anno4);
        assertEquals(Lists.newArrayList(anno4, anno1, anno4, anno2, anno3), list.toJavaList());

        list.add(4, anno4);
        assertEquals(Lists.newArrayList(anno4, anno1, anno4, anno2, anno4, anno3), list.toJavaList());

        boolean exceptionThrown = false;
        try {
            list.add(-1, anno4);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            list.add(6, anno4);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testRemoveAtIndex() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));

        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno4);

        assertEquals(anno2, list.remove(1));
        assertEquals(3, list.size());
        assertEquals(Lists.newArrayList(anno1, anno3, anno4), list.toJavaList());

        assertEquals(anno1, list.remove(0));
        assertEquals(2, list.size());
        assertEquals(Lists.newArrayList(anno3, anno4), list.toJavaList());

        assertEquals(anno4, list.remove(1));
        assertEquals(1, list.size());
        assertEquals(Lists.newArrayList(anno3), list.toJavaList());

        boolean exceptionThrown = false;
        try {
            list.remove(-1);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(Lists.newArrayList(anno3), list.toJavaList());

        exceptionThrown = false;
        try {
            list.remove(2);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        assertEquals(Lists.newArrayList(anno3), list.toJavaList());

        assertEquals(anno3, list.remove(0));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testIndexOf() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));
        ResourceObject anno5 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno5"));


        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno2, anno4);

        assertEquals(0, list.indexOf(anno1));
        assertEquals(1, list.indexOf(anno2));
        assertEquals(4, list.indexOf(anno4));
        assertEquals(-1, list.indexOf(anno5));
    }

    @Test
    public void testLastIndexOf() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));
        ResourceObject anno5 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno5"));


        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno2, anno4);

        assertEquals(0, list.lastIndexOf(anno1));
        assertEquals(3, list.lastIndexOf(anno2));
        assertEquals(4, list.lastIndexOf(anno4));
        assertEquals(-1, list.lastIndexOf(anno5));
    }

    @Test
    public void testListIteratorAtIndex() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));


        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno4);

        ListIterator<Object> i1 = list.listIterator(0);
        assertEquals(anno1, i1.next());

        ListIterator<Object> i2 = list.listIterator(1);
        assertEquals(anno2, i2.next());

        ListIterator<Object> i3 = list.listIterator(3);
        assertEquals(anno4, i3.next());

        boolean exceptionThrown = false;
        try {
            list.listIterator(-1);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            list.listIterator(5);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSubList() throws Exception {
        ResourceObject anno1 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1"));
        ResourceObject anno2 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2"));
        ResourceObject anno3 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3"));
        ResourceObject anno4 = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno4"));


        RDFList list = RDFLists.asRDFList(connection, anno1, anno2, anno3, anno2, anno4);

        assertEquals(Lists.newArrayList(anno1, anno2), list.subList(0, 2));
        assertEquals(Lists.newArrayList(anno2, anno3, anno2), list.subList(1, 4));
        assertEquals(Lists.newArrayList(anno4), list.subList(4, 5));

        boolean exceptionThrown = false;
        try {
            list.subList(-1, 2);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            list.subList(1, 6);
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}