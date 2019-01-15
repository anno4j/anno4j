package com.example.model.de.example.www;

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
 * Support class for {@link Hauptgericht} */
@Partial
public abstract class HauptgerichtSupport extends SchemaSanitizingObjectSupport implements Hauptgericht {
  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Override
  public void setHatBestandteils(Zutat... values) {
    for(Zutat current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Zutat> _newValues = new HashSet<Zutat>();
    _newValues.addAll(Arrays.asList(values));
    setHatBestandteils(_newValues);
    sanitizeSchema("http://www.example.de/hatBestandteil");
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
  public void setHatKochs(Koch... values) {
    for(Koch current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Koch> _newValues = new HashSet<Koch>();
    _newValues.addAll(Arrays.asList(values));
    setHatKochs(_newValues);
    sanitizeSchema("http://www.example.de/hatKoch");
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
  public void setHatHauptbestandteils(Zutat... values) {
    for(Zutat current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Zutat> _newValues = new HashSet<Zutat>();
    _newValues.addAll(Arrays.asList(values));
    setHatHauptbestandteils(_newValues);
    sanitizeSchema("http://www.example.de/hatHauptbestandteil");
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
  public void setHatNames(CharSequence... values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _newValues = new HashSet<CharSequence>();
    _newValues.addAll(Arrays.asList(values));
    setHatNames(_newValues);
    sanitizeSchema("http://www.example.de/hatName");
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
  public void addHatKoch(Koch value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<Koch> _acc = new HashSet<Koch>();
    _acc.addAll(getHatKochs());
    _acc.add(value);
    setHatKochs(_acc);
    sanitizeSchema("http://www.example.de/hatKoch");
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
  public void addHatHauptbestandteil(Zutat value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<Zutat> _acc = new HashSet<Zutat>();
    _acc.addAll(getHatHauptbestandteils());
    _acc.add(value);
    setHatHauptbestandteils(_acc);
    sanitizeSchema("http://www.example.de/hatHauptbestandteil");
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
  public void addHatName(CharSequence value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatNames());
    _acc.add(value);
    setHatNames(_acc);
    sanitizeSchema("http://www.example.de/hatName");
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
  public void addHatBestandteil(Zutat value) {
    if(value == null) {
      throw new IllegalArgumentException("Value must not be null");
    }
    Set<Zutat> _acc = new HashSet<Zutat>();
    _acc.addAll(getHatBestandteils());
    _acc.add(value);
    setHatBestandteils(_acc);
    sanitizeSchema("http://www.example.de/hatBestandteil");
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
  public void addAllHatBestandteils(Set<? extends Zutat> values) {
    for(Zutat current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Zutat> _acc = new HashSet<Zutat>();
    _acc.addAll(getHatBestandteils());
    _acc.addAll(values);
    setHatBestandteils(_acc);
    sanitizeSchema("http://www.example.de/hatBestandteil");
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
  public void addAllHatNames(Set<? extends CharSequence> values) {
    for(CharSequence current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<CharSequence> _acc = new HashSet<CharSequence>();
    _acc.addAll(getHatNames());
    _acc.addAll(values);
    setHatNames(_acc);
    sanitizeSchema("http://www.example.de/hatName");
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
  public void addAllHatKochs(Set<? extends Koch> values) {
    for(Koch current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Koch> _acc = new HashSet<Koch>();
    _acc.addAll(getHatKochs());
    _acc.addAll(values);
    setHatKochs(_acc);
    sanitizeSchema("http://www.example.de/hatKoch");
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
  public void addAllHatHauptbestandteils(Set<? extends Zutat> values) {
    for(Zutat current : values) {
      if(current == null) {
        throw new IllegalArgumentException("Value must not be null");
      }
    }
    Set<Zutat> _acc = new HashSet<Zutat>();
    _acc.addAll(getHatHauptbestandteils());
    _acc.addAll(values);
    setHatHauptbestandteils(_acc);
    sanitizeSchema("http://www.example.de/hatHauptbestandteil");
  }

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  @Override
  public boolean removeHatBestandteil(Zutat value) {
    Set<Zutat> _oldValues = getHatBestandteils();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatBestandteil", value);
      sanitizeSchema("http://www.example.de/hatBestandteil");
      // Refresh values:
      if(getHatBestandteils() instanceof PropertySet) {
        ((PropertySet) getHatBestandteils()).refresh();
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
  public boolean removeHatHauptbestandteil(Zutat value) {
    Set<Zutat> _oldValues = getHatHauptbestandteils();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatHauptbestandteil", value);
      sanitizeSchema("http://www.example.de/hatHauptbestandteil");
      // Refresh values:
      if(getHatHauptbestandteils() instanceof PropertySet) {
        ((PropertySet) getHatHauptbestandteils()).refresh();
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
  public boolean removeHatName(CharSequence value) {
    Set<CharSequence> _oldValues = getHatNames();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatName", value);
      sanitizeSchema("http://www.example.de/hatName");
      // Refresh values:
      if(getHatNames() instanceof PropertySet) {
        ((PropertySet) getHatNames()).refresh();
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
  public boolean removeHatKoch(Koch value) {
    Set<Koch> _oldValues = getHatKochs();
    if(_oldValues.contains(value)) {
      removeValue("http://www.example.de/hatKoch", value);
      sanitizeSchema("http://www.example.de/hatKoch");
      // Refresh values:
      if(getHatKochs() instanceof PropertySet) {
        ((PropertySet) getHatKochs()).refresh();
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
  public boolean removeAllHatBestandteils(Set<? extends Zutat> values) {
    boolean changed = false;
    Set<Zutat> _oldValues = getHatBestandteils();
    Set<Zutat> _containedValues = new HashSet<Zutat>();
    for(Zutat current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(Zutat _current : _containedValues) {
        removeValue("http://www.example.de/hatBestandteil", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatBestandteil");
    // Refresh values:
    if(getHatBestandteils() instanceof PropertySet) {
      ((PropertySet) getHatBestandteils()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatKochs(Set<? extends Koch> values) {
    boolean changed = false;
    Set<Koch> _oldValues = getHatKochs();
    Set<Koch> _containedValues = new HashSet<Koch>();
    for(Koch current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(Koch _current : _containedValues) {
        removeValue("http://www.example.de/hatKoch", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatKoch");
    // Refresh values:
    if(getHatKochs() instanceof PropertySet) {
      ((PropertySet) getHatKochs()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatHauptbestandteils(Set<? extends Zutat> values) {
    boolean changed = false;
    Set<Zutat> _oldValues = getHatHauptbestandteils();
    Set<Zutat> _containedValues = new HashSet<Zutat>();
    for(Zutat current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(Zutat _current : _containedValues) {
        removeValue("http://www.example.de/hatHauptbestandteil", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatHauptbestandteil");
    // Refresh values:
    if(getHatHauptbestandteils() instanceof PropertySet) {
      ((PropertySet) getHatHauptbestandteils()).refresh();
    }
    return changed;
  }

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  @Override
  public boolean removeAllHatNames(Set<? extends CharSequence> values) {
    boolean changed = false;
    Set<CharSequence> _oldValues = getHatNames();
    Set<CharSequence> _containedValues = new HashSet<CharSequence>();
    for(CharSequence current : values) {
      if(_oldValues.contains(current)) {
        changed |= true;
        _containedValues.add(current);
      }
    }
    if(!_containedValues.isEmpty()) {
      for(CharSequence _current : _containedValues) {
        removeValue("http://www.example.de/hatName", _current);
      }
    }
    sanitizeSchema("http://www.example.de/hatName");
    // Refresh values:
    if(getHatNames() instanceof PropertySet) {
      ((PropertySet) getHatNames()).refresh();
    }
    return changed;
  }
}
