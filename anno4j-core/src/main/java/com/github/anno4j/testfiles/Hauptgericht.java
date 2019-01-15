package com.github.anno4j.testfiles;

import java.lang.CharSequence;
import java.util.Set;
import org.openrdf.annotations.Iri;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Generated class for http://www.example.de/Hauptgericht */
@Iri("http://www.example.de/Hauptgericht")
public interface Hauptgericht extends Resource {
  @Iri("http://www.example.de/hatBestandteil")
  Set<Zutat> getHatBestandteils();

  @Iri("http://www.example.de/hatKoch")
  Set<Koch> getHatKochs();

  @Iri("http://www.example.de/hatName")
  Set<CharSequence> getHatNames();

  @Iri("http://www.example.de/hatHauptbestandteil")
  Set<Zutat> getHatHauptbestandteils();

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatBestandteil")
  void setHatBestandteils(Set<Zutat> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatName")
  void setHatNames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatHauptbestandteils(Zutat... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatHauptbestandteil")
  void setHatHauptbestandteils(Set<Zutat> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatNames(CharSequence... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatKoch")
  void setHatKochs(Set<Koch> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatBestandteils(Zutat... values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatKochs(Koch... values);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatBestandteil(Zutat value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatName(CharSequence value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatKoch(Koch value);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatHauptbestandteil(Zutat value);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatHauptbestandteils(Set<? extends Zutat> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatBestandteils(Set<? extends Zutat> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatKochs(Set<? extends Koch> values);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatNames(Set<? extends CharSequence> values);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatHauptbestandteil(Zutat value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatKoch(Koch value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatName(CharSequence value);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatBestandteil(Zutat value);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatKochs(Set<? extends Koch> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatNames(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatHauptbestandteils(Set<? extends Zutat> values);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatBestandteils(Set<? extends Zutat> values);
}
