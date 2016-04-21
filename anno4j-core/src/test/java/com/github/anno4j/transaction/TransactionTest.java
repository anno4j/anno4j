package com.github.anno4j.transaction;


import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.GraphContextQueryTest;
import com.github.anno4j.querying.QueryService;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.*;

public class TransactionTest {

    private Anno4j anno4j;
    private URI subgraph = new URIImpl("http://www.example.com/TESTGRAPH");

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void beginCommitTest() throws RepositoryException, InstantiationException, IllegalAccessException {
        Transaction transaction = anno4j.createTransaction();

        assertFalse(transaction.isActive());
        transaction.begin();
        assertTrue(transaction.isActive());

        Annotation annotation = transaction.createObject(Annotation.class);

        // annotation shouldn't be created yet
        assertEquals(0, anno4j.findAll(Annotation.class).size());

        transaction.commit();
        transaction.close();

        assertEquals(1, anno4j.findAll(Annotation.class).size());
    }

    @Test
    public void queryDuringTransactionTest() throws Exception {
        // Create test annotation

        Transaction transaction = anno4j.createTransaction();
        transaction.begin();

        transaction.createObject(Annotation.class);

        assertEquals(1, transaction.findAll(Annotation.class).size());

        transaction.commit();

        assertEquals(1, transaction.findAll(Annotation.class).size());
    }

    @Test
    public void roolbackTest() throws Exception {
        Transaction transaction = anno4j.createTransaction();
        transaction.begin();

        transaction.createObject(Annotation.class);

        assertEquals(1, transaction.findAll(Annotation.class).size());

        transaction.rollback();

        assertEquals(0, transaction.findAll(Annotation.class).size());
    }

    @Test
    public void insertSubgraphTest() throws Exception {
        Transaction transaction = anno4j.createTransaction();
        transaction.begin();
        transaction.setAllContexts(subgraph);

        transaction.createObject(Annotation.class);
        assertEquals(1, transaction.findAll(Annotation.class).size());
        transaction.commit();

        // check with new connection
        assertEquals(1, anno4j.findAll(Annotation.class).size());
    }

    @Test
    public void querySubgraphTest() throws Exception {
        Transaction transaction = anno4j.createTransaction();
        transaction.begin();
        transaction.createObject(Annotation.class);
        transaction.commit();

        // check with new connection in subgraph
        assertEquals(0, anno4j.findAll(Annotation.class, subgraph).size());
        // check with new connection in global graph
        assertEquals(1, anno4j.findAll(Annotation.class).size());
    }

}
