package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
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
