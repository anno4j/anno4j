package com.github.anno4j.schema.model.rdfs.collections;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.object.ObjectConnection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link RDFLists}.
 */
public class RDFListsTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
    }

    @Test
    public void asRDFList() throws Exception {
        List<Annotation> list = new ArrayList<>();
        list.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno1")));
        list.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno2")));
        list.add(anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:test:anno3")));

        RDFList rdfList = RDFLists.asRDFList(list, anno4j.getObjectRepository().getConnection());
        assertEquals(3, rdfList.size());
        assertEquals("urn:test:anno1", ((ResourceObject) rdfList.get(0)).getResourceAsString());
        assertEquals("urn:test:anno2", ((ResourceObject) rdfList.get(1)).getResourceAsString());
        assertEquals("urn:test:anno3", ((ResourceObject) rdfList.get(2)).getResourceAsString());

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();
        BooleanQuery query = connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "ASK {" +
                        "   ?l1 a rdf:List ." +
                        "   ?l1 rdf:first <urn:test:anno1> . " +
                        "   ?l1 rdf:rest ?l2 . " +
                        "   ?l2 rdf:first <urn:test:anno2> . " +
                        "   ?l2 rdf:rest ?l3 . " +
                        "   ?l3 rdf:first <urn:test:anno3> . " +
                        "   ?l3 rdf:rest rdf:nil . " +
                        "}"
        );
        assertTrue(query.evaluate());
    }
}