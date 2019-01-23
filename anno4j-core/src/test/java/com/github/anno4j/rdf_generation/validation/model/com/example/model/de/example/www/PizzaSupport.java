package com.github.anno4j.rdf_generation.validation.model.com.example.model.de.example.www;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.repository.object.advisers.helpers.PropertySet;

/**
 * Support class for {@link Pizza} */
@Partial
public abstract class PizzaSupport extends SchemaSanitizingObjectSupport implements Pizza {
  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHatSortennames(CharSequence... values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _newValues = new HashSet<CharSequence>();
    _newValues.addAll(Arrays.asList(values));
    setHatSortennames(_newValues);
    sanitizeSchema("http://www.example.de/hatSortenname");
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
  public void addHatSortenname(CharSequence value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatSortennames());
    _acc.add(value);
    setHatSortennames(_acc);
    sanitizeSchema("http://www.example.de/hatSortenname");
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
  public void addAllHatSortennames(Set<? extends CharSequence> values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatSortennames());
    _acc.addAll(values);
    setHatSortennames(_acc);
    sanitizeSchema("http://www.example.de/hatSortenname");
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHatSortenname(CharSequence value) {
    Set<CharSequence> _oldValues = getHatSortennames();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatSortenname", value);
      sanitizeSchema("http://www.example.de/hatSortenname");
      // Refresh values:
      if(getHatSortennames() instanceof PropertySet) {
        ((PropertySet) getHatSortennames()).refresh();
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
  public boolean removeAllHatSortennames(Set<? extends CharSequence> values) {
    boolean changed = false;
    Set<CharSequence> _oldValues = getHatSortennames();
    Set<CharSequence> _containedValues = new HashSet<CharSequence>();
    for(CharSequence current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(CharSequence _current : _containedValues) {
        removeValue("http://www.example.de/hatSortenname", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatSortenname");
    // Refresh values:
    if(getHatSortennames() instanceof PropertySet) {
      ((PropertySet) getHatSortennames()).refresh();
    }
    return changed;
  }
}
