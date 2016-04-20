package com.github.anno4j.recommendation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.impl.TestBody1;
import com.github.anno4j.recommendation.impl.TestBody2;
import com.github.anno4j.recommendation.impl.SimpleSimilarityAlgorithm;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.*;

/**
 * Created by Manu on 11/04/16.
 */
public class RecommendationServiceTest extends RecommendationTestSetup {

    @Override
    protected void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {

    }
}