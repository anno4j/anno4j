package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.tests.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PathTest.class,
        LanguageTest.class,
        ConstraintLessTest.class,
        DataTypeTest.class,
        IsATest.class
})
public class QueryServiceTestSuite {

    @Before
    public static void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
    }
}
