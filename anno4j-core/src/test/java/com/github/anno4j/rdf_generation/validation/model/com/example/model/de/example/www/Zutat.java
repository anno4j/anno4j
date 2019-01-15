package com.example.model.de.example.www;

import com.example.model.com.github.Resource;
import java.lang.CharSequence;
import java.util.Set;
import org.openrdf.annotations.Iri;

/**
 * Generated class for http://www.example.de/Zutat */
@Iri("http://www.example.de/Zutat")
public interface Zutat extends Resource {
  @Iri("http://www.example.de/hatHerkunftsland")
  Set<CharSequence> getHatHerkunftslands();

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  @Iri("http://www.example.de/hatHerkunftsland")
  void setHatHerkunftslands(Set<? extends CharSequence> values);

  /**
   *
   * @param values The elements to set.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void setHatHerkunftslands(CharSequence... values);

  /**
   *
   * @param value The element to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addHatHerkunftsland(CharSequence value);

  /**
   *
   * @param values The elements to be added.
   * @throws IllegalArgumentException If the element(s) are not in the value space.
   * The value space is defined as:<ol>
   * 	<li>The value is not null.</li>
   * 
   * </ol> */
  void addAllHatHerkunftslands(Set<? extends CharSequence> values);

  /**
   *
   * @param value The element to be removed.
   * @return Returns true if the element was present. Returns false otherwise. */
  boolean removeHatHerkunftsland(CharSequence value);

  /**
   *
   * @param values The elements to be removed.
   * @return Returns true if any value was removed. */
  boolean removeAllHatHerkunftslands(Set<? extends CharSequence> values);
}
