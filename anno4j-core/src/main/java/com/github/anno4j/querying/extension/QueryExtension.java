package com.github.anno4j.querying.extension;

import com.github.anno4j.querying.QueryService;

public abstract class QueryExtension {

    QueryService queryService;

    public QueryExtension() {
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public void setQueryService(QueryService service) {
        this.queryService = service;
    }
}
