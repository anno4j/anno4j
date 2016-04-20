package com.github.anno4j.transaction;


import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import static org.junit.Assert.*;

public class TransactionTest {

    private Anno4j anno4j;

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
}
