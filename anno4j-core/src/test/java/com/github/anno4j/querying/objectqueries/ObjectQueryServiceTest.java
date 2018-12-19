package com.github.anno4j.querying.objectqueries;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.objectqueries.model.Cat;
import com.github.anno4j.querying.objectqueries.model.Owner;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.List;

import static org.junit.Assert.*;

public class ObjectQueryServiceTest {

    @Test
    public void testSimpleQueryWithEasyModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, QueryEvaluationException, MalformedQueryException {
        Anno4j anno4j = new Anno4j(false);

        String catName = "Garfield";
        String ownerName = "Bob";

        Cat cat = anno4j.createObject(Cat.class);
        cat.setName(catName);

        Owner owner = anno4j.createObject(Owner.class);
        owner.setName(ownerName);
        cat.setOwner(owner);

        ObjectQueryService oqs = anno4j.createObjectQueryService();

        Cat queryCat = oqs.createObject(Cat.class);
        queryCat.setName(catName);

        Owner queryOwner = oqs.createObject(Owner.class);
        queryOwner.setName(ownerName);
        queryCat.setOwner(queryOwner);

        oqs.setPivot(queryCat);
        List<ResourceObject> result = oqs.execute();

        assertEquals(catName, ((Cat) result.get(0)).getName());
        assertEquals(ownerName, ((Cat) result.get(0)).getOwner().getName());

        ObjectQueryService oqs2 = anno4j.createObjectQueryService();

        Cat queryCat2 = oqs2.createObject(Cat.class);
        queryCat2.setName(catName);

        oqs2.setPivot(queryCat2);
        List<ResourceObject> result2 = oqs2.execute();

        assertEquals(catName, ((Cat) result2.get(0)).getName());

        ObjectQueryService oqs3 = anno4j.createObjectQueryService();

        Owner queryOwner3 = oqs3.createObject(Owner.class);
        queryOwner3.setName(ownerName);

        oqs3.setPivot(queryOwner3);
        List<ResourceObject> result3 = oqs3.execute();

        assertEquals(ownerName, ((Owner) result3.get(0)).getName());
    }
}