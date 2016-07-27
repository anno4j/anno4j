package com.github.anno4j.model;

import java.util.HashSet;

/**
 * Support class for the ExternalWebResource interface.
 */
public abstract class ExternalWebResourceSupport extends CreationProvenanceSupport implements ExternalWebResource {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLanguage(String language) {
        HashSet<String> languages = new HashSet<>();

        if(this.getLanguages() != null) {
            languages.addAll(this.getLanguages());
        }

        languages.add(language);
        this.setLanguages(languages);
    }
}
