package com.github.anno4j.rdfs_parser.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import java.util.Collection;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

/**
 * Test for {@link StronglyConnectedComponents}.
 */
public class StronglyConnectedComponentsTest {

    @Test
    public void findSCCs() throws Exception {
        Anno4j anno4j = new Anno4j();
        Collection<Collection<RDFSClazz>> sccs = new HashSet<>();

        // The graph is picked from Wikipedia
        // (https://en.wikipedia.org/wiki/Strongly_connected_component#/media/File:Graph_Condensation.svg):

        RDFSClazz a = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/a"));
        RDFSClazz b = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/b"));
        RDFSClazz c = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/c"));
        RDFSClazz d = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/d"));
        RDFSClazz e = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/e"));
        a.addSubClazz(c);
        b.addSubClazz(a);
        c.addSubClazz(b);
        d.addSubClazz(c);
        c.addSubClazz(e);
        e.addSubClazz(d);
        sccs.add(Sets.newHashSet(a, b, c, d, e));

        RDFSClazz f = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/f"));
        b.addSubClazz(f);
        e.addSubClazz(f);
        sccs.add(Sets.newHashSet(f));

        RDFSClazz g = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/g"));
        RDFSClazz h = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/h"));
        RDFSClazz i = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/i"));
        f.addSubClazz(g);
        f.addSubClazz(i);
        g.addSubClazz(h);
        h.addSubClazz(i);
        i.addSubClazz(g);
        sccs.add(Sets.newHashSet(g, h, i));

        RDFSClazz j = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/j"));
        i.addSubClazz(j);
        h.addSubClazz(j);
        sccs.add(Sets.newHashSet(j));

        RDFSClazz k = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/k"));
        RDFSClazz l = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/l"));
        f.addSubClazz(k);
        k.addSubClazz(j);
        k.addSubClazz(l);
        l.addSubClazz(k);
        sccs.add(Sets.newHashSet(k, l));

        RDFSClazz m = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/m"));
        RDFSClazz n = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/n"));
        RDFSClazz o = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/o"));
        RDFSClazz p = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/p"));
        d.addSubClazz(m);
        e.addSubClazz(n);
        p.addSubClazz(k);
        m.addSubClazz(n);
        n.addSubClazz(p);
        p.addSubClazz(o);
        o.addSubClazz(m);
        n.addSubClazz(o);
        sccs.add(Sets.newHashSet(m, n, o, p));

        Collection<RDFSClazz> seeds = Sets.newHashSet(a);
        assertEquals(sccs, StronglyConnectedComponents.findSCCs(seeds));
    }

    @Test
    public void findSCCsInIsolatedForest() throws Exception {
        Anno4j anno4j = new Anno4j();
        Collection<Collection<RDFSClazz>> sccs = new HashSet<>();

        RDFSClazz a = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/a"));
        RDFSClazz b = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/b"));
        RDFSClazz c = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/c"));
        RDFSClazz d = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/d"));
        RDFSClazz e = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/e"));
        a.addSubClazz(c);
        b.addSubClazz(a);
        c.addSubClazz(b);
        d.addSubClazz(c);
        c.addSubClazz(e);
        e.addSubClazz(d);
        sccs.add(Sets.newHashSet(a, b, c, d, e));

        RDFSClazz f = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/k"));
        RDFSClazz g = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://ex.de/l"));
        f.addSubClazz(g);
        g.addSubClazz(f);
        sccs.add(Sets.newHashSet(f, g));

        Collection<RDFSClazz> seeds = Sets.newHashSet(a, f);
        assertEquals(sccs, StronglyConnectedComponents.findSCCs(seeds));
    }
}