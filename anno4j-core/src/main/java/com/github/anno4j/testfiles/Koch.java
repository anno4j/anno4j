package com.github.anno4j.testfiles;

import java.lang.CharSequence;
import java.lang.Integer;
import java.util.Set;
import org.openrdf.annotations.Iri;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Generated class for http://www.example.de/Koch */
@Iri("http://www.example.de/Koch")
public interface Koch extends Resource {
  @Iri("http://www.example.de/hatPersonenname")
  Set<CharSequence> getHatPersonennames();

  @Iri("http://www.example.de/hatAlter")
  Set<Integer> getHatAlters();

  @Iri("http://www.example.de/hatNationalität")
  Set<CharSequence> getHatNationalitäts();

  @Iri("http://www.example.de/hatGeburtstag")
  Set<CharSequence> getHatGeburtstags();

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatGeburtstags(CharSequence... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatAlters(Integer... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatNationalitäts(CharSequence... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatPersonennames(CharSequence... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatPersonenname")
  void setHatPersonennames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatAlter")
  void setHatAlters(Set<Integer> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatNationalität")
  void setHatNationalitäts(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatGeburtstag")
  void setHatGeburtstags(Set<? extends CharSequence> values);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatNationalität(CharSequence value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatAlter(Integer value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatPersonenname(CharSequence value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatGeburtstag(CharSequence value);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatNationalitäts(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatGeburtstags(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatPersonennames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatAlters(Set<? extends Integer> values);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatAlter(Integer value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatNationalität(CharSequence value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatPersonenname(CharSequence value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatGeburtstag(CharSequence value);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatAlters(Set<? extends Integer> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatPersonennames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatNationalitäts(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatGeburtstags(Set<? extends CharSequence> values);
}
