package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Created by Manu on 07/11/16.
 */
public interface RDFSSchemaResource extends ResourceObject {

    @Iri(RDFS.LABEL)
    void setLabels(Set<String> labels);

    @Iri(RDFS.LABEL)
    Set<String> getLabels();

    void addLabel(String label);

    @Iri(RDFS.COMMENT)
    void setComment(String comment);

    @Iri(RDFS.COMMENT)
    String getComment();
}
