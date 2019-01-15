package com.example.model.de.example;

import com.example.model.com.github.Resource;
import com.github.anno4j.annotations.AllValuesFrom;
import com.github.anno4j.annotations.MaxCardinality;
import com.github.anno4j.annotations.MinCardinality;
import com.github.anno4j.annotations.SomeValuesFrom;
import com.github.anno4j.model.impl.ResourceObject;
import java.util.Set;
import org.openrdf.annotations.Iri;

/**
 * Generated class for http://example.de/validated_transaction_test_resource */
@Iri("http://example.de/validated_transaction_test_resource")
public interface ValidatedTransactionTestResource extends Resource {
  @Iri("http://example.de/cardinality")
  @MinCardinality(2)
  @MaxCardinality(3)
  Set<ResourceObject> getCardinalities();

  @Iri("http://example.de/qualified_cardinality")
  @MaxCardinality(
      value = 2,
      onClass = ValidatedTransactionTestResourceChild.class
  )
  @MinCardinality(
      value = 1,
      onClass = ValidatedTransactionTestResourceChild.class
  )
  Set<ResourceObject> getQualifiedCardinalities();

  @Iri("http://example.de/all_values_from")
  @AllValuesFrom({ValidatedTransactionTestResourceChild.class})
  Set<ResourceObject> getAllValuesFroms();

  @Iri("http://example.de/some_values_from")
  @SomeValuesFrom({ValidatedTransactionTestResourceChild.class})
  Set<ResourceObject> getSomeValuesFroms();
}
