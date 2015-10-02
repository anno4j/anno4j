package com.github.anno4j.querying.extensions;

import com.github.anno4j.querying.extension.QueryExtension;
import com.github.anno4j.querying.QueryService;

public class Extension2 extends QueryExtension {
    public QueryService helloWorld() {
        return getQueryService();
    }
}
