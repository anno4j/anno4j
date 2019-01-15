package com.example.model.de.example;

import com.example.model.com.github.Resource;
import com.example.model.com.xmlns.Person;
import com.github.anno4j.annotations.AllValuesFrom;
import com.github.anno4j.annotations.MinCardinalities;
import com.github.anno4j.annotations.MinCardinality;
import com.github.anno4j.model.impl.ResourceObject;
import java.util.Set;
import org.openrdf.annotations.Iri;

/**
 * Generated class for http://example.de/#validly_annotated_person */
@Iri("http://example.de/#validly_annotated_person")
public interface ValidlyAnnotatedPerson extends Resource {
  @Iri("http://example.de/#has_activity")
  @MinCardinalities({@MinCardinality(2), @MinCardinality(value = 1, onClass = Job.class)})
  Set<ResourceObject> getHasActivities();

  @Iri("http://example.de/#has_boss")
  @AllValuesFrom({ValidlyAnnotatedPerson.class, Person.class})
  Set<ResourceObject> getHasBoss();
}
