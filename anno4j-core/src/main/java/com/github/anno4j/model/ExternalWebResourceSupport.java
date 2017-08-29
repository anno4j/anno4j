package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;

/**
 * Support class for the ExternalWebResource interface.
 */
@Partial
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
