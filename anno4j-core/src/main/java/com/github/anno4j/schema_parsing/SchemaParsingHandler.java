package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * AbstractRDFHandler used to parse a given RDF schema file.
 */
class SchemaParsingHandler extends RDFHandlerBase {

    private Anno4j anno4j;

    SchemaParsingHandler(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

    @Override
    public void handleStatement(Statement st) {
        try {
            this.anno4j.getRepository().getConnection().add(st);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
