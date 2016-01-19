package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.repository.RepositoryException;

public abstract class MotivationFactory {

    public static Motivation getBookmarking(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_BOOKMARKING);
        return result;
    }

    public static Motivation getClassifying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_CLASSIFYING);
        return result;
    }

    public static Motivation getCommenting(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_COMMENTING);
        return result;
    }
    public static Motivation getDescribing(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_DESCRIBING);
        return result;
    }

    public static Motivation getEditing(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_EDITING);
        return result;
    }

    public static Motivation getHighlighting(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_HIGHLIGHTING);
        return result;
    }

    public static Motivation getIdentifying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_IDENTIFYING);
        return result;
    }

    public static Motivation getLinking(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_LINKING);
        return result;
    }

    public static Motivation getModerating(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_MODERATING);
        return result;
    }

    public static Motivation getQuestioning(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_QUESTIONING);
        return result;
    }

    public static Motivation getReplying(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_REPLYING);
        return result;
    }

    public static Motivation getTagging(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Motivation result = anno4j.createObject(Motivation.class);
        result.setResourceAsString(OADM.MOTIVATION_TAGGING);
        return result;
    }


}
