package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Created by Manu on 02/11/16.
 */
public class Anno4jStatementHandler extends AbstractRDFHandler {

    private Anno4j anno4j;

    public Anno4jStatementHandler(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

    public void handleStatement(Statement st) {
        try {
            this.anno4j.getRepository().getConnection().add(st);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
