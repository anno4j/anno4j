package com.example.model.com.github;

import com.github.anno4j.annotations.SubPropertyOf;
import com.github.anno4j.model.impl.ResourceObject;
import java.util.Set;
import org.openrdf.annotations.Iri;

/**
 * Generated class for https://github.com/anno4j/ns#Resource */
@Iri("https://github.com/anno4j/ns#Resource")
public interface Resource extends ResourceObject {
  @Iri("http://example.de/superproperty1")
  Set<ResourceObject> getSuperproperty1s();

  @Iri("http://example.de/all_values_from")
  Set<ResourceObject> getAllValuesFroms();

  @Iri("http://example.de/#has_subordinate")
  Set<ResourceObject> getHasSubordinates();

  @Iri("http://example.de/inverseof_1")
  Set<ResourceObject> getInverseof1s();

  @Iri("urn:anno4j_test:sanitizing_subprop")
  @SubPropertyOf({"urn:anno4j_test:sanitizing_superprop"})
  Set<ResourceObject> getSanitizingSubprops();

  @Iri("http://example.de/qualified_cardinality")
  Set<ResourceObject> getQualifiedCardinalities();

  @Iri("urn:anno4j_test:sanitizing_inverse2")
  Set<ResourceObject> getSanitizingInverse2s();

  @Iri("urn:anno4j_test:sanitizing_obj_subprop")
  @SubPropertyOf({"urn:anno4j_test:sanitizing_obj_superprop"})
  Set<ResourceObject> getSanitizingObjSubprops();

  @Iri("http://example.de/cardinality")
  Set<ResourceObject> getCardinalities();

  @Iri("urn:anno4j_test:sanitizing_inverse1")
  Set<ResourceObject> getSanitizingInverse1s();

  @Iri("urn:anno4j_test:sanitizing_superprop")
  Set<ResourceObject> getSanitizingSuperprops();

  @Iri("http://example.de/#has_boss")
  Set<ResourceObject> getHasBoss();

  @Iri("http://example.de/inverseof_2")
  Set<ResourceObject> getInverseof2s();

  @Iri("http://example.de/sub_property")
  @SubPropertyOf({"http://example.de/superproperty2"})
  Set<ResourceObject> getSubProperties();

  @Iri("http://example.de/some_values_from")
  Set<ResourceObject> getSomeValuesFroms();

  @Iri("http://example.de/#has_activity")
  Set<ResourceObject> getHasActivities();

  @Iri("urn:anno4j_test:sanitizing_obj_superprop")
  Set<ResourceObject> getSanitizingObjSuperprops();

  @Iri("http://example.de/superproperty2")
  @SubPropertyOf({"http://example.de/superproperty1"})
  Set<ResourceObject> getSuperproperty2s();

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHasSubordinates(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_superprop")
  void setSanitizingSuperprops(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_inverse2")
  void setSanitizingInverse2s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_subprop")
  void setSanitizingSubprops(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingObjSubprops(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/superproperty2")
  void setSuperproperty2s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingInverse1s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/#has_subordinate")
  void setHasSubordinates(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHasBoss(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSubProperties(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingObjSuperprops(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/#has_boss")
  void setHasBoss(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/some_values_from")
  void setSomeValuesFroms(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/qualified_cardinality")
  void setQualifiedCardinalities(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/inverseof_2")
  void setInverseof2s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/#has_activity")
  void setHasActivities(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_obj_superprop")
  void setSanitizingObjSuperprops(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingInverse2s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/inverseof_1")
  void setInverseof1s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_inverse1")
  void setSanitizingInverse1s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("urn:anno4j_test:sanitizing_obj_subprop")
  void setSanitizingObjSubprops(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHasActivities(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/all_values_from")
  void setAllValuesFroms(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingSuperprops(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setCardinalities(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setQualifiedCardinalities(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSanitizingSubprops(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/cardinality")
  void setCardinalities(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/superproperty1")
  void setSuperproperty1s(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSuperproperty1s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setAllValuesFroms(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://example.de/sub_property")
  void setSubProperties(Set<ResourceObject> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setInverseof2s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setInverseof1s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSuperproperty2s(ResourceObject... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setSomeValuesFroms(ResourceObject... values);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingSubprop(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHasBoss(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSuperproperty2(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHasActivity(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSubProperty(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllValuesFrom(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addInverseof1(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingInverse1(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addQualifiedCardinality(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingObjSuperprop(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSuperproperty1(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingInverse2(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addInverseof2(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingSuperprop(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHasSubordinate(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addCardinality(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSanitizingObjSubprop(ResourceObject value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addSomeValuesFrom(ResourceObject value);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingObjSubprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSomeValuesFroms(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingSubprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingSuperprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHasSubordinates(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSubProperties(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHasBoss(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingInverse1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSuperproperty1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllAllValuesFroms(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllInverseof2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHasActivities(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingObjSuperprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllCardinalities(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllInverseof1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSanitizingInverse2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllSuperproperty2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllQualifiedCardinalities(Set<? extends ResourceObject> values);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSuperproperty2(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingInverse1(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSuperproperty1(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeAllValuesFrom(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeInverseof2(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHasBoss(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeQualifiedCardinality(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSubProperty(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingObjSuperprop(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeCardinality(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingSuperprop(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHasSubordinate(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSomeValuesFrom(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingSubprop(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingObjSubprop(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeInverseof1(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHasActivity(ResourceObject value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeSanitizingInverse2(ResourceObject value);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSubProperties(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHasActivities(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingSubprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSuperproperty1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllAllValuesFroms(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHasSubordinates(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllCardinalities(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingInverse1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSomeValuesFroms(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllQualifiedCardinalities(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingObjSubprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHasBoss(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingInverse2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingObjSuperprops(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllInverseof1s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSuperproperty2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllInverseof2s(Set<? extends ResourceObject> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllSanitizingSuperprops(Set<? extends ResourceObject> values);
}
