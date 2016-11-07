package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;

/**
 * AbstractRDFHandler used to parse a given RDF schema file.
 */
class SchemaParsingHandler extends AbstractRDFHandler {

    private Anno4j anno4j;

    SchemaParsingHandler(Anno4j anno4j) {
        this.anno4j = anno4j;
    }

    @Override
    public void handleStatement(Statement st) {
        try {
            org.openrdf.model.Resource subject = new URIImpl(st.getSubject().toString());
            org.openrdf.model.URI predicate = new URIImpl(st.getPredicate().toString());

            // The object needs special treatment when a language is associated
            org.openrdf.model.Value object;
            String objectString = st.getObject().toString();
            if(objectString.length() > 3 && objectString.charAt(objectString.length() - 3) == '@') {
                String language = objectString.substring(objectString.length() - 2);

                object = ValueFactoryImpl.getInstance().createLiteral(st.getObject().stringValue(), language);
            } else {
                // The object of the statement is not a literal with language tag, so add the given URI
                object = new URIImpl(st.getObject().stringValue());
            }

            org.openrdf.model.Statement statement = new org.openrdf.model.impl.StatementImpl(subject, predicate, object);

            this.anno4j.getRepository().getConnection().add(statement);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
