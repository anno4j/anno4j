package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import org.openrdf.annotations.Iri;

/**
 * A class defines a group of individuals that belong together because they share some properties.
 * Refers to http://www.w3.org/2002/07/owl#Class
 *<br>
 * <strong>Important:</strong> This definition of <code>owl:Class</code> refers to OWL Lite/DL
 * where it is a subclass of <code>rdfs:Class</code>. In OWL Full it is defined as <code>owl:equivalentClass</code>
 * of <code>rdfs:Class</code>.
 */
@Iri(OWL.CLAZZ)
public interface OWLClazz extends RDFSClazz {
}
