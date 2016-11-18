package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;

import java.util.Set;

/**
 * Created by Manu on 15/11/16.
 */
@Iri(OWL.CLAZZ)
public interface OWLClazz extends OWLSchemaResource {

    @Iri(RDFS.SUB_CLASS_OF)
    void setSubClazzes(Set<OWLClazz> subClazzes);

    @Iri(RDFS.SUB_CLASS_OF)
    Set<OWLClazz> getSubClazzes();

    void addSubClazz(OWLClazz subClazz);

    @Iri(RDFS.SUB_CLASS_OF)
    void setRestrictions(Set<OWLRestriction> restrictions);

    @Sparql("SELECT ?restriction WHERE { $this <"+ RDFS.SUB_CLASS_OF + "> ?restriction . ?restriction <"+ RDF.TYPE +"> <"+ OWL.RESTRICTION +"> }")
    @Iri(RDFS.SUB_CLASS_OF)
    Set<OWLRestriction> getRestrictions();

    void addRestriction(OWLRestriction restriction);
}
