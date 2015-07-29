package org.openrdf.repository.object.behaviours;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.openrdf.annotations.Iri;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.event.NotifyingRepository;
import org.openrdf.repository.event.RepositoryConnectionListener;
import org.openrdf.repository.event.base.NotifyingRepositoryWrapper;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class MergeEmptyListsTest extends TestCase {

	public static boolean PRINT_REPOSITORY_ACTIONS = true;
	private ObjectConnection manager;
	private SomePerson person1;
	private SomePerson person2;
	private SomePerson someChild;
	private RessourceManager rm;

	protected void setUp() throws Exception {
		rm = new RessourceManager();
		manager = rm.getManager();
		person1 = manager.addDesignation(manager.getObject("http://www.something.org/person1"), SomePerson.class);
		person2 = manager.addDesignation(manager.getObject("http://www.something.org/person2"), SomePerson.class);

		someChild = manager.addDesignation(manager.getObject("http://www.some.org/someChild"), SomePerson.class);

	}

	@Override
	protected void tearDown() throws Exception {
		rm.destroy();
		super.tearDown();
	}

	public void testList() {

		// Create 2 lists
		List<SomePerson> childrenOfPerson1AsList = new ArrayList<SomePerson>();
		List<SomePerson> childrenOfPerson2AsList = new ArrayList<SomePerson>();

		// Notice, I add the two seperate lists
		person1.setChildrenList(childrenOfPerson1AsList);
		person2.setChildrenList(childrenOfPerson2AsList);

		// Add child _ONLY_ to person2
		person2.getChildrenList().add(someChild);

		/*
		 * No lets check who has children
		 */
		List<SomePerson> childrenList1 = person1.getChildrenList();
		System.out.println("Children of person 1 (should not have a child):");
		for (SomePerson child : childrenList1) {
			System.out.println(" -> Child: " + child);
		}
		assertEquals(Arrays.asList(), person1.getChildrenList());

		List<SomePerson> childrenList2 = person2.getChildrenList();
		System.out.println("Children of person 2:");
		for (SomePerson child : childrenList2) {
			System.out.println(" -> Child: " + child);
		}
		assertEquals(Arrays.asList(someChild), person2.getChildrenList());

		System.out.println("------------------------------------------");
	}

	/*
	 * Ok, thats strange, now lets test that with sets
	 */
	public void testSet() {

		// Create 2 lists
		Set<SomePerson> childrenOfPerson1AsSet = new HashSet<SomePerson>();
		Set<SomePerson> childrenOfPerson2AsSet = new HashSet<SomePerson>();

		// Notice, I add the two seperate lists
		person1.setChildrenSet(childrenOfPerson1AsSet);
		person2.setChildrenSet(childrenOfPerson2AsSet);

		// Add child _ONLY_ to person2
		person2.getChildrenSet().add(someChild);

		/*
		 * No lets check who has children
		 */
		Set<SomePerson> childrenSet1 = person1.getChildrenSet();
		System.out.println("Children of person 1 (should not have a child):");
		for (SomePerson child : childrenSet1) {
			System.out.println(" -> Child: " + child);
		}
		assertEquals(new HashSet(Arrays.asList()), person1.getChildrenSet());

		Set<SomePerson> childrenSet2 = person2.getChildrenSet();
		System.out.println("Children of person 2:");
		for (SomePerson child : childrenSet2) {
			System.out.println(" -> Child: " + child);
		}
		assertEquals(new HashSet(Arrays.asList(someChild)), person2
				.getChildrenSet());

	}

	class RessourceManager {

		protected Repository repository = null;
		protected ObjectRepository factory = null;
		protected ObjectConnection manager = null;
		protected ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		protected RepositoryConnectionListener listener = null;

		protected RessourceManager() {
			init();
		}

		protected void init() {

			try {

				module.addConcept(SomePerson.class);

				// module.addBehaviour(PropertyChangeNotifierSupport.class,
				// Person.PERSON);

				// Register behaviours
				// module.addRole(PersonBehavior.class);

				// Prepare repository
				repository = new SailRepository(new MemoryStore());
				repository = new NotifyingRepositoryWrapper(repository);

				if (repository instanceof NotifyingRepository) {
					// NotifyingRepository notifyingRepository = null;
					//	                    
					// listener = new
					// RepositoryConnectionListenerImpl(visualRoot);
					//	                    
					// notifyingRepository = (NotifyingRepository)repository;
					// notifyingRepository.addRepositoryConnectionListener(listener);
				}

				// repository = new NotifyingRepositoryConnection(repository);

				// Prepare factory and manager
				repository.initialize();
				factory = new ObjectRepositoryFactory().createRepository(module, repository);
				factory.setQueryLanguage(QueryLanguage.SERQL);
				manager = factory.getConnection();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void destroy() throws Exception {
			manager.close();
			factory.shutDown();
		}

		public Repository getRepository() {
			return repository;
		}

		public ObjectRepository getFactory() {
			return factory;
		}

		public ObjectConnection getManager() {
			return manager;
		}

		public ObjectRepositoryConfig getModule() {
			return module;
		}
	}

	@Iri(SomePerson.SOMENODE)
	public interface SomePerson {

		public final static String NAMESPACE_BSG = "http://bsg.org/bsg/1.0/";
		public final static String SOMENODE = "http://bsg.org/bsg/1.0/somenode";
		public final static String NAME = "http://bsg.org/bsg/1.0/name";
		public final static String CHILDREN_LIST = "http://bsg.org/bsg/1.0/children_list";
		public final static String CHILDREN_SET = "http://bsg.org/bsg/1.0/children_set";

		@Iri(SomePerson.NAME)
		public String getName();

		public void setName(String name);

		@Iri(SomePerson.CHILDREN_LIST)
		public List<SomePerson> getChildrenList();

		public void setChildrenList(List<SomePerson> children);

		@Iri(SomePerson.CHILDREN_SET)
		public Set<SomePerson> getChildrenSet();

		public void setChildrenSet(Set<SomePerson> children);

	}
}
