package org.openrdf.repository.object.base;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class RepositoryTestCase extends TestCase {

	protected static final String DEFAULT = "memory";

	protected static final String DELIM = "#";

	private interface RepositoryFactory {
		Repository createRepository() throws Exception;
	}

	public static LinkedHashMap<String, RepositoryFactory> factories = new LinkedHashMap<String, RepositoryFactory>();
	static {
		//*
		factories.put(DEFAULT, new RepositoryFactory() {
			public Repository createRepository() {
				return new SailRepository(new MemoryStore());
			}
		});
		/*
		factories.put("http", new RepositoryFactory() {
			public Repository createRepository() {
				return new HTTPRepository("http://localhost:8080/openrdf-sesame/repositories/memory");
			}
		});
		//*/
		/*
		factories.put("mysql", new RepositoryFactory() {
			public Repository createRepository() {
				RdbmsStore sail = new MySqlStore("sesame_test");
				return new SailRepository(sail);
			}
		});
		factories.put("mysql-readAhead", new RepositoryFactory() {
			public Repository createRepository() {
				RdbmsStore sail = new MySqlStore("sesame_test");
				return new ReadAheadRepository(new SailRepository(sail));
			}
		});
		/*
		factories.put("pgsql-readAhead", new RepositoryFactory() {
			public Repository createRepository() {
				RdbmsStore sail = new PgSqlStore("sesame_test");
				return new ReadAheadRepository(new SailRepository(sail));
			}
		});
		/*
		factories.put("pgsql", new RepositoryFactory() {
			public Repository createRepository() {
				RdbmsStore sail = new PgSqlStore("sesame_test");
				return new SailRepository(sail);
			}
		});
		//*/
		/*
		factories.put("native", new RepositoryFactory() {
			public Repository createRepository() throws IOException {
				File dir = File.createTempFile("openrdf", "");
				dir.delete();
				dir.mkdir();
				return new SailRepository(new NativeStore(dir));
			}
		});
		//*/
		/*
		factories.put("http", new RepositoryFactory() {
			public Repository createRepository() {
				return new HTTPRepository("http://localhost:8080/openrdf-sesame", "sesame-test");
			}
		});
		/*
		factories.put("forward", new RepositoryFactory() {
			public Repository createRepository() throws IOException {
				return new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
			}
		});
		factories.put("hierarchy", new RepositoryFactory() {
			public Repository createRepository() throws IOException {
				return new SailRepository(new DirectTypeHierarchyInferencer(new MemoryStore()));
			}
		});
		//*/
	}

	public static Test suite() throws Exception {
		return new TestSuite();
	}

	public static Test suite(Class<? extends TestCase> subclass)
			throws Exception {
		String sname = subclass.getName();
		TestSuite suite = new TestSuite(sname);
		for (Method method : subclass.getMethods()) {
			String name = method.getName();
			if (name.startsWith("test")) {
				for (String code : factories.keySet()) {
					TestCase test = subclass.newInstance();
					test.setName(name + DELIM + code);
					suite.addTest(test);
				}
			}
		}
		return suite;
	}

	private String factory;

	protected Repository repository;

	public RepositoryTestCase() {
		factory = DEFAULT;
	}

	public RepositoryTestCase(String name) {
		setName(name);
	}

	@Override
	public String getName() {
		String name = super.getName();
		if (DEFAULT.equals(factory))
			return name;
		return name + DELIM + factory;
	}

	@Override
	public void setName(String name) {
		if (name.contains(DELIM)) {
			super.setName(name.substring(0, name.indexOf(DELIM)));
			this.factory = name.substring(name.lastIndexOf(DELIM) + 1);
		} else {
			super.setName(name);
			factory = DEFAULT;
		}
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	@Override
	protected void setUp() throws Exception {
		repository = getRepository();
	}

	protected Repository getRepository() throws Exception, RepositoryException {
		Repository repository = createRepository();
		repository.initialize();
		RepositoryConnection con = repository.getConnection();
		try {
			con.setAutoCommit(false);
			con.clear();
			con.clearNamespaces();
			con.setNamespace("test", "urn:test:");
			con.setAutoCommit(true);
		} finally {
			con.close();
		}
		return repository;
	}

	protected Repository createRepository() throws Exception {
		return factories.get(factory).createRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			repository.shutDown();
		} catch (Exception e) {
		}
	}

}
