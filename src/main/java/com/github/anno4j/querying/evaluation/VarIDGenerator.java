package com.github.anno4j.querying.evaluation;

import java.util.concurrent.atomic.AtomicLong;

public class VarIDGenerator {

    private static AtomicLong counter = new AtomicLong(0);

    public static String createID() {
        return "var" + Math.abs(counter.incrementAndGet());
    }
}
