package com.github.anno4j;

import com.github.anno4j.persistence.IDGenerator;
import com.github.anno4j.persistence.impl.IDGeneratorLocalURN;

/**
 * Created by schlegel on 06/05/15.
 */
public class Anno4j {


    private static Anno4j instance = null;

    private IDGenerator idGenerator = new IDGeneratorLocalURN();

    private Anno4j() {
        // Exists only to defeat instantiation.
    }

    public IDGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public static Anno4j getInstance() {
        if(instance == null) {
            synchronized (Anno4j.class) {
                if(instance == null) {
                    instance = new Anno4j();
                }
            }
        }
        return instance;
    }

}
