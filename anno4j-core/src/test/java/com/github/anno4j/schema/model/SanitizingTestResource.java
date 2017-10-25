package com.github.anno4j.schema.model;

import com.github.anno4j.annotations.InverseOf;
import com.github.anno4j.annotations.SubPropertyOf;
import com.github.anno4j.annotations.Symmetric;
import com.github.anno4j.annotations.Transitive;
import com.github.anno4j.schema.SchemaSanitizingObject;
import com.github.anno4j.schema.SchemaSanitizingObjectSupportTest;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Created by fischmat on 20.06.17.
 */
@Iri("urn:anno4j_test:sanitizing_resource")
public interface SanitizingTestResource extends SchemaSanitizingObject {

    @Iri("urn:anno4j_test:sanitizing_superprop")
    Set<Integer> getSuperproperty();

    @Iri("urn:anno4j_test:sanitizing_superprop")
    void setSuperproperty(Set<Integer> values);

    @Iri("urn:anno4j_test:sanitizing_subprop")
    @SubPropertyOf({"urn:anno4j_test:sanitizing_superprop"})
    Set<Integer> getSubproperty();

    @Iri("urn:anno4j_test:sanitizing_subprop")
    void setSubproperty(Set<Integer> values);

    @Iri("urn:anno4j_test:sanitizing_obj_superprop")
    Set<SanitizingTestResource> getObjectSuperproperty();

    @Iri("urn:anno4j_test:sanitizing_obj_superprop")
    void setObjectSuperproperty(Set<SanitizingTestResource> values);

    @Iri("urn:anno4j_test:sanitizing_obj_subprop")
    @SubPropertyOf({"urn:anno4j_test:sanitizing_obj_superprop"})
    Set<SanitizingTestResource> getObjectSubproperty();

    @Iri("urn:anno4j_test:sanitizing_obj_subprop")
    void setObjectSubproperty(Set<SanitizingTestResource> values);

    @Iri("urn:anno4j_test:sanitizing_symmetric")
    @Symmetric
    Set<SanitizingTestResource> getSymmetric();

    @Iri("urn:anno4j_test:sanitizing_symmetric")
    void setSymmetric(Set<SanitizingTestResource> values);

    @Iri("urn:anno4j_test:sanitizing_transitive")
    @Transitive
    Set<SanitizingTestResource> getTransitive();

    @Iri("urn:anno4j_test:sanitizing_transitive")
    void setTransitive(Set<SanitizingTestResource> values);

    @Iri("urn:anno4j_test:sanitizing_inverse1")
    @InverseOf({"urn:anno4j_test:sanitizing_inverse2"})
    Set<SanitizingTestResource> getInverse1();

    @Iri("urn:anno4j_test:sanitizing_inverse1")
    void setInverse1(Set<SanitizingTestResource> values);

    @Iri("urn:anno4j_test:sanitizing_inverse2")
    @InverseOf({"urn:anno4j_test:sanitizing_inverse1"})
    Set<SanitizingTestResource> getInverse2();

    @Iri("urn:anno4j_test:sanitizing_inverse2")
    void setInverse2(Set<SanitizingTestResource> values);

    /**
     * This method tests value removal and belongs to
     * {@link SchemaSanitizingObjectSupportTest#testRemoveValue()}.
     */
    void testRemoveValue(SanitizingTestResource superObject);
}
