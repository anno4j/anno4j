package com.example.model.de.example.www;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Override;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.repository.object.advisers.helpers.PropertySet;

/**
 * Support class for {@link Koch} */
@Partial
public abstract class KochSupport extends SchemaSanitizingObjectSupport implements Koch {
  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHatGeburtstags(CharSequence... values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _newValues = new HashSet<CharSequence>();
    _newValues.addAll(Arrays.asList(values));
    setHatGeburtstags(_newValues);
    sanitizeSchema("http://www.example.de/hatGeburtstag");
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
  public void setHatPersonennames(CharSequence... values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _newValues = new HashSet<CharSequence>();
    _newValues.addAll(Arrays.asList(values));
    setHatPersonennames(_newValues);
    sanitizeSchema("http://www.example.de/hatPersonenname");
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
  public void setHatAlters(Integer... values) {
    for(Integer current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Integer> _newValues = new HashSet<Integer>();
    _newValues.addAll(Arrays.asList(values));
    setHatAlters(_newValues);
    sanitizeSchema("http://www.example.de/hatAlter");
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
  public void setHatNationalitäts(CharSequence... values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _newValues = new HashSet<CharSequence>();
    _newValues.addAll(Arrays.asList(values));
    setHatNationalitäts(_newValues);
    sanitizeSchema("http://www.example.de/hatNationalität");
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
  public void addHatAlter(Integer value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<Integer> _acc = new HashSet<Integer>();
    _acc.addAll(getHatAlters());
    _acc.add(value);
    setHatAlters(_acc);
    sanitizeSchema("http://www.example.de/hatAlter");
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
  public void addHatGeburtstag(CharSequence value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatGeburtstags());
    _acc.add(value);
    setHatGeburtstags(_acc);
    sanitizeSchema("http://www.example.de/hatGeburtstag");
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
  public void addHatPersonenname(CharSequence value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatPersonennames());
    _acc.add(value);
    setHatPersonennames(_acc);
    sanitizeSchema("http://www.example.de/hatPersonenname");
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
  public void addHatNationalität(CharSequence value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatNationalitäts());
    _acc.add(value);
    setHatNationalitäts(_acc);
    sanitizeSchema("http://www.example.de/hatNationalität");
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
  public void addAllHatGeburtstags(Set<? extends CharSequence> values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatGeburtstags());
    _acc.addAll(values);
    setHatGeburtstags(_acc);
    sanitizeSchema("http://www.example.de/hatGeburtstag");
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
  public void addAllHatAlters(Set<? extends Integer> values) {
    for(Integer current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Integer> _acc = new HashSet<Integer>();
    _acc.addAll(getHatAlters());
    _acc.addAll(values);
    setHatAlters(_acc);
    sanitizeSchema("http://www.example.de/hatAlter");
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
  public void addAllHatNationalitäts(Set<? extends CharSequence> values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatNationalitäts());
    _acc.addAll(values);
    setHatNationalitäts(_acc);
    sanitizeSchema("http://www.example.de/hatNationalität");
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
  public void addAllHatPersonennames(Set<? extends CharSequence> values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatPersonennames());
    _acc.addAll(values);
    setHatPersonennames(_acc);
    sanitizeSchema("http://www.example.de/hatPersonenname");
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHatNationalität(CharSequence value) {
    Set<CharSequence> _oldValues = getHatNationalitäts();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatNationalität", value);
      sanitizeSchema("http://www.example.de/hatNationalität");
      // Refresh values:
      if(getHatNationalitäts() instanceof PropertySet) {
        ((PropertySet) getHatNationalitäts()).refresh();
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
  public boolean removeHatGeburtstag(CharSequence value) {
    Set<CharSequence> _oldValues = getHatGeburtstags();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatGeburtstag", value);
      sanitizeSchema("http://www.example.de/hatGeburtstag");
      // Refresh values:
      if(getHatGeburtstags() instanceof PropertySet) {
        ((PropertySet) getHatGeburtstags()).refresh();
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
  public boolean removeHatPersonenname(CharSequence value) {
    Set<CharSequence> _oldValues = getHatPersonennames();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatPersonenname", value);
      sanitizeSchema("http://www.example.de/hatPersonenname");
      // Refresh values:
      if(getHatPersonennames() instanceof PropertySet) {
        ((PropertySet) getHatPersonennames()).refresh();
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
  public boolean removeHatAlter(Integer value) {
    Set<Integer> _oldValues = getHatAlters();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatAlter", value);
      sanitizeSchema("http://www.example.de/hatAlter");
      // Refresh values:
      if(getHatAlters() instanceof PropertySet) {
        ((PropertySet) getHatAlters()).refresh();
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
  public boolean removeAllHatAlters(Set<? extends Integer> values) {
    boolean changed = false;
    Set<Integer> _oldValues = getHatAlters();
    Set<Integer> _containedValues = new HashSet<Integer>();
    for(Integer current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(Integer _current : _containedValues) {
        removeValue("http://www.example.de/hatAlter", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatAlter");
    // Refresh values:
    if(getHatAlters() instanceof PropertySet) {
      ((PropertySet) getHatAlters()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatNationalitäts(Set<? extends CharSequence> values) {
    boolean changed = false;
    Set<CharSequence> _oldValues = getHatNationalitäts();
    Set<CharSequence> _containedValues = new HashSet<CharSequence>();
    for(CharSequence current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(CharSequence _current : _containedValues) {
        removeValue("http://www.example.de/hatNationalität", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatNationalität");
    // Refresh values:
    if(getHatNationalitäts() instanceof PropertySet) {
      ((PropertySet) getHatNationalitäts()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatGeburtstags(Set<? extends CharSequence> values) {
    boolean changed = false;
    Set<CharSequence> _oldValues = getHatGeburtstags();
    Set<CharSequence> _containedValues = new HashSet<CharSequence>();
    for(CharSequence current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(CharSequence _current : _containedValues) {
        removeValue("http://www.example.de/hatGeburtstag", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatGeburtstag");
    // Refresh values:
    if(getHatGeburtstags() instanceof PropertySet) {
      ((PropertySet) getHatGeburtstags()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatPersonennames(Set<? extends CharSequence> values) {
    boolean changed = false;
    Set<CharSequence> _oldValues = getHatPersonennames();
    Set<CharSequence> _containedValues = new HashSet<CharSequence>();
    for(CharSequence current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(CharSequence _current : _containedValues) {
        removeValue("http://www.example.de/hatPersonenname", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatPersonenname");
    // Refresh values:
    if(getHatPersonennames() instanceof PropertySet) {
      ((PropertySet) getHatPersonennames()).refresh();
    }
    return changed;
  }
}
