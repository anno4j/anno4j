package com.example.model.de.example.www;

import com.github.anno4j.annotations.SubPropertyOf;
import java.lang.CharSequence;
import java.util.Set;
import org.openrdf.annotations.Iri;

/**
 * Generated class for http://www.example.de/Pizza */
@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht {
  @Iri("http://www.example.de/hatSortenname")
  @SubPropertyOf({"http://www.example.de/hatName"})
  Set<CharSequence> getHatSortennames();

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatSortenname")
  void setHatSortennames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatSortennames(CharSequence... values);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatSortenname(CharSequence value);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatSortennames(Set<? extends CharSequence> values);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatSortenname(CharSequence value);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatSortennames(Set<? extends CharSequence> values);
}
