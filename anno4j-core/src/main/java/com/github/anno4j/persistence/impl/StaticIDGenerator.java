package com.github.anno4j.persistence.impl;

import com.github.anno4j.persistence.IDGenerator;

/**
 * Created by schlegel on 05/10/15.
 */
public class StaticIDGenerator {
// TODO directly generate the IDs in alibaba?
    private static StaticIDGenerator instance;

    private IDGenerator idGenerator = new IDGeneratorAnno4jURN();

    private StaticIDGenerator() {

    }

    public static StaticIDGenerator getInstance() {
        if (instance == null) {
            synchronized (StaticIDGenerator.class) {
                if (instance == null) {
                    instance = new StaticIDGenerator();
                }
            }
        }
        return instance;
    }

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}

