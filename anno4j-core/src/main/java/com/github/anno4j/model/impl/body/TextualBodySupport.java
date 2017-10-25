package com.github.anno4j.model.impl.body;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.ExternalWebResourceSupport;
import com.github.anno4j.model.Motivation;

import java.util.HashSet;

/**
 * Support class for the TextualBody interface.
 */
@Partial
public abstract class TextualBodySupport extends ExternalWebResourceSupport implements TextualBody {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPurpose(Motivation purpose) {
        HashSet<Motivation> purposes = new HashSet<>();

        if(this.getPurposes() != null) {
            purposes.addAll(this.getPurposes());
        }

        purposes.add(purpose);
        this.setPurposes(purposes);
    }
}
