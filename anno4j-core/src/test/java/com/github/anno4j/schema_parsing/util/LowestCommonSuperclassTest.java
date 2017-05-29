package com.github.anno4j.schema_parsing.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.google.common.collect.Sets;
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

    private static BuildableRDFSClazz r;

    private static BuildableRDFSClazz a;

    private static BuildableRDFSClazz b;

    private static BuildableRDFSClazz c;

    private static BuildableRDFSClazz d;

    private static BuildableRDFSClazz e;

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

        r = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl(RDFS.CLAZZ));
        a = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl("http://example.de/a"));
        b = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl("http://example.de/b"));
        c = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl("http://example.de/c"));
        d = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl("http://example.de/d"));
        e = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl("http://example.de/e"));

        a.setSuperclazzes(Sets.<RDFSClazz>newHashSet(r));
        b.setSuperclazzes(Sets.<RDFSClazz>newHashSet(r));

        c.setSuperclazzes(Sets.<RDFSClazz>newHashSet(a));
        d.setSuperclazzes(Sets.<RDFSClazz>newHashSet(a, b));
        e.setSuperclazzes(Sets.<RDFSClazz>newHashSet(b, r));
    }

    @Test
    public void testLowestCommonSuperclass() throws Exception {
        Set<BuildableRDFSClazz> cd = new HashSet<>();
        cd.add(c);
        cd.add(d);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(cd));

        Set<BuildableRDFSClazz> ce = new HashSet<>();
        ce.add(c);
        ce.add(e);
        assertEquals(r, LowestCommonSuperclass.getLowestCommonSuperclass(ce));

        Set<BuildableRDFSClazz> ac = new HashSet<>();
        ac.add(a);
        ac.add(c);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(ac));

        Set<BuildableRDFSClazz> aa = new HashSet<>();
        aa.add(a);
        aa.add(a);
        assertEquals(a, LowestCommonSuperclass.getLowestCommonSuperclass(aa));

        Set<BuildableRDFSClazz> de = new HashSet<>();
        de.add(d);
        de.add(e);
        assertEquals(b, LowestCommonSuperclass.getLowestCommonSuperclass(de));
    }
}