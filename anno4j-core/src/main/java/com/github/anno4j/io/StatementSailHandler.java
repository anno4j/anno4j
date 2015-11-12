package com.github.anno4j.io;

import com.github.anno4j.io.ObjectParser;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * Extension of an RDFHandlerBase in order to add statements, read by the {@link ObjectParser}, to the supported SailRepository.
 */
public class StatementSailHandler extends RDFHandlerBase {

    private RepositoryConnection connection;

    /**
     * Constructor which also takes the connection to the necessary Repository.
     *
     * @param connection    The connection of the corresponding Repository.
     */
    public StatementSailHandler(RepositoryConnection connection) {
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
