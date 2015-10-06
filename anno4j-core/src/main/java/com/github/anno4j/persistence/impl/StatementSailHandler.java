package com.github.anno4j.persistence.impl;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * Extension of an RDFHandlerBase in order to add statements, read by the {@link ObjectParser}, to the supported SailRepository.
 */
public class StatementSailHandler extends RDFHandlerBase {

    private SailRepositoryConnection connection;

    /**
     * Constructor which also takes the connection to the necessary SailRepository.
     *
     * @param connection    The connection of the corresponding SailRepository.
     */
    public StatementSailHandler(SailRepositoryConnection connection) {
        this.connection = connection;
    }

    @Override
    public void handleStatement(Statement statement) {
        try {
            this.connection.add(statement);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
