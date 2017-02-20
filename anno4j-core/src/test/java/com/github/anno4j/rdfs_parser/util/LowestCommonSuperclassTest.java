package com.github.anno4j.rdfs_parser.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for the {@link LowestCommonSuperclass} class.
 */
public class LowestCommonSuperclassTest {

    private static Anno4j anno4j;

    private static ExtendedRDFSClazz r;

    private static ExtendedRDFSClazz a;

    private static ExtendedRDFSClazz b;

    private static ExtendedRDFSClazz c;

    private static ExtendedRDFSClazz d;

    private static ExtendedRDFSClazz e;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();

        /*
        Build ontology:

                r (rdfs:Class)
                   |_________
             ______|_____    \
             a           b   |
             |_____  ___/ \_ |
             |     \/       \|
             c     d        e
         */

        r = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(RDFS.CLAZZ));
        a = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/a"));
        b = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/b"));
        c = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/c"));
        d = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/d"));
        e = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl("http://example.de/e"));

        a.addSuperclazz(r);
        b.addSuperclazz(r);

        c.addSuperclazz(a);
        d.addSuperclazz(a);
        d.addSuperclazz(b);
        e.addSuperclazz(b);
        e.addSuperclazz(r);
    }

    @Test
    public void testLowestCommonSuperclass() throws Exception {
        Set<ExtendedRDFSClazz> cd = new HashSet<>();
        cd.add(c);
        cd.add(d);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(cd));

        Set<ExtendedRDFSClazz> ce = new HashSet<>();
        ce.add(c);
        ce.add(e);
        assertEquals(r, LowestCommonSuperclass.getLowestCommonSuperclass(ce));

        Set<ExtendedRDFSClazz> ac = new HashSet<>();
        ac.add(a);
        ac.add(c);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(ac));

        Set<ExtendedRDFSClazz> aa = new HashSet<>();
        aa.add(a);
        aa.add(a);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(aa));

        Set<ExtendedRDFSClazz> de = new HashSet<>();
        de.add(d);
        de.add(e);
        assertEquals(b, LowestCommonSuperclass.getLowestCommonSuperclass(de));
    }
}