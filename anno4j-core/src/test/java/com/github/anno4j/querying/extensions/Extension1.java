package com.github.anno4j.querying.extensions;

import com.github.anno4j.querying.extension.QueryExtension;
import com.github.anno4j.querying.QueryService;

public class Extension1 extends QueryExtension {
    public QueryService doSomething() {
        return getQueryService();
    }
}
