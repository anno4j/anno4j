package com.github.anno4j.persistence;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

/**
 * Created by schlegel on 06/05/15.
 */
public class PersistenceService {

    public void persistAnnotation(Annotation annotation) throws RepositoryException {
        ObjectRepository objectRepository = Anno4j.getInstance().getObjectRepository();
        ObjectConnection connection = objectRepository.getConnection();
        connection.addObject(annotation);
        connection.close();
    }
}
