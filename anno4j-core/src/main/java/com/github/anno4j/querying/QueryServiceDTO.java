package com.github.anno4j.querying;

import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import org.apache.marmotta.ldpath.parser.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for the QueryService. Bundles all data needed for further processing.
 */
public class QueryServiceDTO {

    private ArrayList<Criteria> criteria = new ArrayList<>();

    private Configuration configuration;

    private LDPathEvaluatorConfiguration evaluatorConfiguration;

    private Map<String, String> prefixes = new HashMap<>();;

    public ArrayList<Criteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(ArrayList<Criteria> criteria) {
        this.criteria = criteria;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public LDPathEvaluatorConfiguration getEvaluatorConfiguration() {
        return evaluatorConfiguration;
    }

    public void setEvaluatorConfiguration(LDPathEvaluatorConfiguration evaluatorConfiguration) {
        this.evaluatorConfiguration = evaluatorConfiguration;
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }
}
