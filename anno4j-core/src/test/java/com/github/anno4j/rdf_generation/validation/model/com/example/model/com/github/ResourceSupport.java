package com.example.model.com.github;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.repository.object.advisers.helpers.PropertySet;

/**
 * Support class for {@link Resource} */
@Partial
public abstract class ResourceSupport extends SchemaSanitizingObjectSupport implements Resource {
  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setQualifiedCardinalities(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setQualifiedCardinalities(_newValues);
    sanitizeSchema("http://example.de/qualified_cardinality");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingObjSubprops(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingObjSubprops(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSuperproperty1s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSuperproperty1s(_newValues);
    sanitizeSchema("http://example.de/superproperty1");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSuperproperty2s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSuperproperty2s(_newValues);
    sanitizeSchema("http://example.de/superproperty2");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setAllValuesFroms(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setAllValuesFroms(_newValues);
    sanitizeSchema("http://example.de/all_values_from");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingInverse2s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingInverse2s(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setInverseof1s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setInverseof1s(_newValues);
    sanitizeSchema("http://example.de/inverseof_1");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingSubprops(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingSubprops(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHasActivities(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setHasActivities(_newValues);
    sanitizeSchema("http://example.de/#has_activity");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHasSubordinates(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setHasSubordinates(_newValues);
    sanitizeSchema("http://example.de/#has_subordinate");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingObjSuperprops(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingObjSuperprops(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHasBoss(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setHasBoss(_newValues);
    sanitizeSchema("http://example.de/#has_boss");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingSuperprops(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingSuperprops(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setCardinalities(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setCardinalities(_newValues);
    sanitizeSchema("http://example.de/cardinality");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSomeValuesFroms(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSomeValuesFroms(_newValues);
    sanitizeSchema("http://example.de/some_values_from");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSanitizingInverse1s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSanitizingInverse1s(_newValues);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setInverseof2s(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setInverseof2s(_newValues);
    sanitizeSchema("http://example.de/inverseof_2");
  }

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setSubProperties(ResourceObject... values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _newValues = new HashSet<ResourceObject>();
    _newValues.addAll(Arrays.asList(values));
    setSubProperties(_newValues);
    sanitizeSchema("http://example.de/sub_property");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingSubprop(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingSubprops());
    _acc.add(value);
    setSanitizingSubprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingInverse2(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingInverse2s());
    _acc.add(value);
    setSanitizingInverse2s(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addCardinality(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getCardinalities());
    _acc.add(value);
    setCardinalities(_acc);
    sanitizeSchema("http://example.de/cardinality");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSubProperty(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSubProperties());
    _acc.add(value);
    setSubProperties(_acc);
    sanitizeSchema("http://example.de/sub_property");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addInverseof2(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getInverseof2s());
    _acc.add(value);
    setInverseof2s(_acc);
    sanitizeSchema("http://example.de/inverseof_2");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addHasBoss(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasBoss());
    _acc.add(value);
    setHasBoss(_acc);
    sanitizeSchema("http://example.de/#has_boss");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingInverse1(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingInverse1s());
    _acc.add(value);
    setSanitizingInverse1s(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllValuesFrom(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getAllValuesFroms());
    _acc.add(value);
    setAllValuesFroms(_acc);
    sanitizeSchema("http://example.de/all_values_from");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSuperproperty2(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSuperproperty2s());
    _acc.add(value);
    setSuperproperty2s(_acc);
    sanitizeSchema("http://example.de/superproperty2");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSomeValuesFrom(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSomeValuesFroms());
    _acc.add(value);
    setSomeValuesFroms(_acc);
    sanitizeSchema("http://example.de/some_values_from");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSuperproperty1(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSuperproperty1s());
    _acc.add(value);
    setSuperproperty1s(_acc);
    sanitizeSchema("http://example.de/superproperty1");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingSuperprop(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingSuperprops());
    _acc.add(value);
    setSanitizingSuperprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addQualifiedCardinality(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getQualifiedCardinalities());
    _acc.add(value);
    setQualifiedCardinalities(_acc);
    sanitizeSchema("http://example.de/qualified_cardinality");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingObjSubprop(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingObjSubprops());
    _acc.add(value);
    setSanitizingObjSubprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addSanitizingObjSuperprop(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingObjSuperprops());
    _acc.add(value);
    setSanitizingObjSuperprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addHasActivity(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasActivities());
    _acc.add(value);
    setHasActivities(_acc);
    sanitizeSchema("http://example.de/#has_activity");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addInverseof1(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getInverseof1s());
    _acc.add(value);
    setInverseof1s(_acc);
    sanitizeSchema("http://example.de/inverseof_1");
  }

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addHasSubordinate(ResourceObject value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasSubordinates());
    _acc.add(value);
    setHasSubordinates(_acc);
    sanitizeSchema("http://example.de/#has_subordinate");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSuperproperty1s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSuperproperty1s());
    _acc.addAll(values);
    setSuperproperty1s(_acc);
    sanitizeSchema("http://example.de/superproperty1");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSomeValuesFroms(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSomeValuesFroms());
    _acc.addAll(values);
    setSomeValuesFroms(_acc);
    sanitizeSchema("http://example.de/some_values_from");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllQualifiedCardinalities(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getQualifiedCardinalities());
    _acc.addAll(values);
    setQualifiedCardinalities(_acc);
    sanitizeSchema("http://example.de/qualified_cardinality");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingObjSubprops(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingObjSubprops());
    _acc.addAll(values);
    setSanitizingObjSubprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingInverse2s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingInverse2s());
    _acc.addAll(values);
    setSanitizingInverse2s(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllAllValuesFroms(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getAllValuesFroms());
    _acc.addAll(values);
    setAllValuesFroms(_acc);
    sanitizeSchema("http://example.de/all_values_from");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllInverseof1s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getInverseof1s());
    _acc.addAll(values);
    setInverseof1s(_acc);
    sanitizeSchema("http://example.de/inverseof_1");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingSubprops(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingSubprops());
    _acc.addAll(values);
    setSanitizingSubprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllHasSubordinates(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasSubordinates());
    _acc.addAll(values);
    setHasSubordinates(_acc);
    sanitizeSchema("http://example.de/#has_subordinate");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllHasActivities(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasActivities());
    _acc.addAll(values);
    setHasActivities(_acc);
    sanitizeSchema("http://example.de/#has_activity");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllCardinalities(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getCardinalities());
    _acc.addAll(values);
    setCardinalities(_acc);
    sanitizeSchema("http://example.de/cardinality");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingInverse1s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingInverse1s());
    _acc.addAll(values);
    setSanitizingInverse1s(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllHasBoss(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getHasBoss());
    _acc.addAll(values);
    setHasBoss(_acc);
    sanitizeSchema("http://example.de/#has_boss");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSubProperties(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSubProperties());
    _acc.addAll(values);
    setSubProperties(_acc);
    sanitizeSchema("http://example.de/sub_property");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingObjSuperprops(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingObjSuperprops());
    _acc.addAll(values);
    setSanitizingObjSuperprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllInverseof2s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getInverseof2s());
    _acc.addAll(values);
    setInverseof2s(_acc);
    sanitizeSchema("http://example.de/inverseof_2");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSuperproperty2s(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSuperproperty2s());
    _acc.addAll(values);
    setSuperproperty2s(_acc);
    sanitizeSchema("http://example.de/superproperty2");
  }

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void addAllSanitizingSuperprops(Set<? extends ResourceObject> values) {
    for(ResourceObject current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<ResourceObject> _acc = new HashSet<ResourceObject>();
    _acc.addAll(getSanitizingSuperprops());
    _acc.addAll(values);
    setSanitizingSuperprops(_acc);
    sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHasBoss(ResourceObject value) {
    Set<ResourceObject> _oldValues = getHasBoss();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/#has_boss", value);
      sanitizeSchema("http://example.de/#has_boss");
      // Refresh values:
      if(getHasBoss() instanceof PropertySet) {
        ((PropertySet) getHasBoss()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSuperproperty2(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSuperproperty2s();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/superproperty2", value);
      sanitizeSchema("http://example.de/superproperty2");
      // Refresh values:
      if(getSuperproperty2s() instanceof PropertySet) {
        ((PropertySet) getSuperproperty2s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeInverseof2(ResourceObject value) {
    Set<ResourceObject> _oldValues = getInverseof2s();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/inverseof_2", value);
      sanitizeSchema("http://example.de/inverseof_2");
      // Refresh values:
      if(getInverseof2s() instanceof PropertySet) {
        ((PropertySet) getInverseof2s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSomeValuesFrom(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSomeValuesFroms();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/some_values_from", value);
      sanitizeSchema("http://example.de/some_values_from");
      // Refresh values:
      if(getSomeValuesFroms() instanceof PropertySet) {
        ((PropertySet) getSomeValuesFroms()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeCardinality(ResourceObject value) {
    Set<ResourceObject> _oldValues = getCardinalities();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/cardinality", value);
      sanitizeSchema("http://example.de/cardinality");
      // Refresh values:
      if(getCardinalities() instanceof PropertySet) {
        ((PropertySet) getCardinalities()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeQualifiedCardinality(ResourceObject value) {
    Set<ResourceObject> _oldValues = getQualifiedCardinalities();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/qualified_cardinality", value);
      sanitizeSchema("http://example.de/qualified_cardinality");
      // Refresh values:
      if(getQualifiedCardinalities() instanceof PropertySet) {
        ((PropertySet) getQualifiedCardinalities()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSubProperty(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSubProperties();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/sub_property", value);
      sanitizeSchema("http://example.de/sub_property");
      // Refresh values:
      if(getSubProperties() instanceof PropertySet) {
        ((PropertySet) getSubProperties()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeInverseof1(ResourceObject value) {
    Set<ResourceObject> _oldValues = getInverseof1s();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/inverseof_1", value);
      sanitizeSchema("http://example.de/inverseof_1");
      // Refresh values:
      if(getInverseof1s() instanceof PropertySet) {
        ((PropertySet) getInverseof1s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingInverse1(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingInverse1s();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_inverse1", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
      // Refresh values:
      if(getSanitizingInverse1s() instanceof PropertySet) {
        ((PropertySet) getSanitizingInverse1s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeAllValuesFrom(ResourceObject value) {
    Set<ResourceObject> _oldValues = getAllValuesFroms();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/all_values_from", value);
      sanitizeSchema("http://example.de/all_values_from");
      // Refresh values:
      if(getAllValuesFroms() instanceof PropertySet) {
        ((PropertySet) getAllValuesFroms()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSuperproperty1(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSuperproperty1s();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/superproperty1", value);
      sanitizeSchema("http://example.de/superproperty1");
      // Refresh values:
      if(getSuperproperty1s() instanceof PropertySet) {
        ((PropertySet) getSuperproperty1s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingObjSubprop(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingObjSubprops();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_obj_subprop", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
      // Refresh values:
      if(getSanitizingObjSubprops() instanceof PropertySet) {
        ((PropertySet) getSanitizingObjSubprops()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingSubprop(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingSubprops();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_subprop", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
      // Refresh values:
      if(getSanitizingSubprops() instanceof PropertySet) {
        ((PropertySet) getSanitizingSubprops()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingSuperprop(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingSuperprops();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_superprop", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
      // Refresh values:
      if(getSanitizingSuperprops() instanceof PropertySet) {
        ((PropertySet) getSanitizingSuperprops()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHasSubordinate(ResourceObject value) {
    Set<ResourceObject> _oldValues = getHasSubordinates();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/#has_subordinate", value);
      sanitizeSchema("http://example.de/#has_subordinate");
      // Refresh values:
      if(getHasSubordinates() instanceof PropertySet) {
        ((PropertySet) getHasSubordinates()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingInverse2(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingInverse2s();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_inverse2", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
      // Refresh values:
      if(getSanitizingInverse2s() instanceof PropertySet) {
        ((PropertySet) getSanitizingInverse2s()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeSanitizingObjSuperprop(ResourceObject value) {
    Set<ResourceObject> _oldValues = getSanitizingObjSuperprops();
    if(_oldValues.contains(value)) {
      removeValue("urn:anno4j_test:sanitizing_obj_superprop", value);
      sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
      // Refresh values:
      if(getSanitizingObjSuperprops() instanceof PropertySet) {
        ((PropertySet) getSanitizingObjSuperprops()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHasActivity(ResourceObject value) {
    Set<ResourceObject> _oldValues = getHasActivities();
    if(_oldValues.contains(value)) {
      removeValue("http://example.de/#has_activity", value);
      sanitizeSchema("http://example.de/#has_activity");
      // Refresh values:
      if(getHasActivities() instanceof PropertySet) {
        ((PropertySet) getHasActivities()).refresh();
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSuperproperty2s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSuperproperty2s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/superproperty2", _current);
      }
    }
    sanitizeSchema("http://example.de/superproperty2");
    // Refresh values:
    if(getSuperproperty2s() instanceof PropertySet) {
      ((PropertySet) getSuperproperty2s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingObjSuperprops(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingObjSuperprops();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_obj_superprop", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
    // Refresh values:
    if(getSanitizingObjSuperprops() instanceof PropertySet) {
      ((PropertySet) getSanitizingObjSuperprops()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllCardinalities(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getCardinalities();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/cardinality", _current);
      }
    }
    sanitizeSchema("http://example.de/cardinality");
    // Refresh values:
    if(getCardinalities() instanceof PropertySet) {
      ((PropertySet) getCardinalities()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSubProperties(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSubProperties();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/sub_property", _current);
      }
    }
    sanitizeSchema("http://example.de/sub_property");
    // Refresh values:
    if(getSubProperties() instanceof PropertySet) {
      ((PropertySet) getSubProperties()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllInverseof1s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getInverseof1s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/inverseof_1", _current);
      }
    }
    sanitizeSchema("http://example.de/inverseof_1");
    // Refresh values:
    if(getInverseof1s() instanceof PropertySet) {
      ((PropertySet) getInverseof1s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingObjSubprops(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingObjSubprops();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_obj_subprop", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
    // Refresh values:
    if(getSanitizingObjSubprops() instanceof PropertySet) {
      ((PropertySet) getSanitizingObjSubprops()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingInverse1s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingInverse1s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_inverse1", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
    // Refresh values:
    if(getSanitizingInverse1s() instanceof PropertySet) {
      ((PropertySet) getSanitizingInverse1s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllInverseof2s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getInverseof2s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/inverseof_2", _current);
      }
    }
    sanitizeSchema("http://example.de/inverseof_2");
    // Refresh values:
    if(getInverseof2s() instanceof PropertySet) {
      ((PropertySet) getInverseof2s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllQualifiedCardinalities(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getQualifiedCardinalities();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/qualified_cardinality", _current);
      }
    }
    sanitizeSchema("http://example.de/qualified_cardinality");
    // Refresh values:
    if(getQualifiedCardinalities() instanceof PropertySet) {
      ((PropertySet) getQualifiedCardinalities()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHasSubordinates(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getHasSubordinates();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/#has_subordinate", _current);
      }
    }
    sanitizeSchema("http://example.de/#has_subordinate");
    // Refresh values:
    if(getHasSubordinates() instanceof PropertySet) {
      ((PropertySet) getHasSubordinates()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingInverse2s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingInverse2s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_inverse2", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
    // Refresh values:
    if(getSanitizingInverse2s() instanceof PropertySet) {
      ((PropertySet) getSanitizingInverse2s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingSubprops(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingSubprops();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_subprop", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
    // Refresh values:
    if(getSanitizingSubprops() instanceof PropertySet) {
      ((PropertySet) getSanitizingSubprops()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSomeValuesFroms(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSomeValuesFroms();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/some_values_from", _current);
      }
    }
    sanitizeSchema("http://example.de/some_values_from");
    // Refresh values:
    if(getSomeValuesFroms() instanceof PropertySet) {
      ((PropertySet) getSomeValuesFroms()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllAllValuesFroms(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getAllValuesFroms();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/all_values_from", _current);
      }
    }
    sanitizeSchema("http://example.de/all_values_from");
    // Refresh values:
    if(getAllValuesFroms() instanceof PropertySet) {
      ((PropertySet) getAllValuesFroms()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHasBoss(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getHasBoss();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/#has_boss", _current);
      }
    }
    sanitizeSchema("http://example.de/#has_boss");
    // Refresh values:
    if(getHasBoss() instanceof PropertySet) {
      ((PropertySet) getHasBoss()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSuperproperty1s(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSuperproperty1s();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/superproperty1", _current);
      }
    }
    sanitizeSchema("http://example.de/superproperty1");
    // Refresh values:
    if(getSuperproperty1s() instanceof PropertySet) {
      ((PropertySet) getSuperproperty1s()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllSanitizingSuperprops(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getSanitizingSuperprops();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("urn:anno4j_test:sanitizing_superprop", _current);
      }
    }
    sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
    // Refresh values:
    if(getSanitizingSuperprops() instanceof PropertySet) {
      ((PropertySet) getSanitizingSuperprops()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHasActivities(Set<? extends ResourceObject> values) {
    boolean changed = false;
    Set<ResourceObject> _oldValues = getHasActivities();
    Set<ResourceObject> _containedValues = new HashSet<ResourceObject>();
    for(ResourceObject current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(ResourceObject _current : _containedValues) {
        removeValue("http://example.de/#has_activity", _current);
      }
    }
    sanitizeSchema("http://example.de/#has_activity");
    // Refresh values:
    if(getHasActivities() instanceof PropertySet) {
      ((PropertySet) getHasActivities()).refresh();
    }
    return changed;
  }
}
