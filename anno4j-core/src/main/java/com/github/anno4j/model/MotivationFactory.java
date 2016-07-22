package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

public class MotivationFactory {

    public static Motivation getAssessing(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_ASSESSING));
    }

    public static Motivation getBookmarking(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_BOOKMARKING));
    }

    public static Motivation getClassifying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_CLASSIFYING));
    }

    public static Motivation getCommenting(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_COMMENTING));
    }

    public static Motivation getDescribing(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_DESCRIBING));
    }

    public static Motivation getEditing(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_EDITING));
    }

    public static Motivation getHighlighting(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_HIGHLIGHTING));
    }

    public static Motivation getIdentifying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_IDENTIFYING));
    }

    public static Motivation getLinking(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_LINKING));
    }

    public static Motivation getModerating(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_MODERATING));
    }

    public static Motivation getQuestioning(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_QUESTIONING));
    }

    public static Motivation getReplying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_REPLYING));
    }

    public static Motivation getTagging(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        return anno4j.createObject(Motivation.class, new URIImpl(OADM.MOTIVATION_TAGGING));
    }
}