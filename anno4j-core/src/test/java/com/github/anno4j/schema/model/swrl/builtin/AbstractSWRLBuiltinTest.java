package com.github.anno4j.schema.model.swrl.builtin;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.swrl.Variable;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Arrays;

public abstract class AbstractSWRLBuiltinTest {

    protected Anno4j anno4j;

    protected SWRLBuiltInService service = SWRLBuiltInService.getBuiltInService();

    protected <T extends SWRLBuiltin> T instantiate(String uri, Object... args) throws InstantiationException {
        return (T) service.getBuiltIn(uri, Arrays.asList(args));
    }

    public Variable createVariable() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException {
        if(anno4j == null) { // Only create Memstore if required
            anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        }
        return anno4j.createObject(Variable.class);
    }
}
