package org.openrdf.repository.object;


import junit.framework.Test;
import org.junit.Ignore;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.event.base.NotifyingRepositoryWrapper;
import org.openrdf.repository.object.base.RepositoryTestCase;
import org.openrdf.repository.object.concepts.Seq;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.object.traits.VoidMessage;
import org.openrdf.result.Result;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Ignore // TODO check this test again
public class UserGuideTest extends RepositoryTestCase {
	private static final String NS = "http://www.example.com/rdf/2007/";

	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(UserGuideTest.class);
	}

	public interface EmailUser extends User {

		public boolean readEmail(EmailMessage message);
	}

	public static class EmailValidator {
		private void validate(String email) throws Exception {
			if (!email.endsWith("@example.com")) {
				throw new IllegalArgumentException("Only internal emails");
			}
		}

		@ParameterTypes(String.class)
		public void setFromEmailAddress(VoidMessage msg) throws Exception {
			validate((String) msg.getParameters()[0]);
			msg.proceed();
		}

		@ParameterTypes(String.class)
		public void setToEmailAddress(VoidMessage msg) throws Exception {
			validate((String) msg.getParameters()[0]);
			msg.proceed();
		}
	}

	@Iri("http://www.example.com/rdf/2007/Employee")
	public interface Employee {

		public double calculateExpectedBonus(double d);

		@Iri("http://www.example.com/rdf/2007/address")
		public String getAddress();

		@Iri("http://www.example.com/rdf/2007/emailAddress")
		public String getEmailAddress();

		@Iri("http://www.example.com/rdf/2007/name")
		public String getName();

		@Iri("http://www.example.com/rdf/2007/phoneNumber")
		public String getPhoneNumber();

		@Iri("http://www.example.com/rdf/2007/salary")
		public double getSalary();

		public void setAddress(String address);

		public void setEmailAddress(String email);

		public void setName(String string);

		public void setPhoneNumber(String phone);

		public void setSalary(double salary);
	}

	@Iri("http://www.example.com/rdf/2007/Engineer")
	public interface Engineer extends Employee {

		@Iri("http://www.example.com/rdf/2007/bonusTargetMet")
		public boolean isBonusTargetMet();

		public void setBonusTargetMet(boolean met);
	}

	public static abstract class EngineerBonusBehaviour implements Engineer {
		public double calculateExpectedBonus(double percent) {
			boolean target = isBonusTargetMet();
			if (target) {
				return percent * getSalary();
			}
			return 0;
		}
	}

	public static class ITSupportAgent {
		public boolean readEmail(EmailMessage message) {
			if (message.getToEmailAddress().equals("help@support.exmple.com")) {
				// process email here
				return true;
			}
			return false;
		}
	}

	@Iri("http://www.example.com/rdf/2007/EmailMessage")
	public interface EmailMessage {

		@Iri("http://www.example.com/rdf/2007/fromEmailAddress")
		public String getFromEmailAddress();

		@Iri("http://www.example.com/rdf/2007/toEmailAddress")
		public String getToEmailAddress();

		public void setFromEmailAddress(String string);

		public void setToEmailAddress(String string);
	}

	@Iri("http://www.example.com/rdf/2007/Node")
	public interface Node1 {
		@Iri("http://www.example.com/rdf/2007/children")
		public java.util.List<Node1> getChildren();

		public void setChildren(java.util.List<Node1> children);
	}

	@Iri("http://www.example.com/rdf/2007/Node")
	public interface Node2 {
		@Iri("http://www.example.com/rdf/2007/children")
		public java.util.List<Node2> getChildren();

		public void setChildren(java.util.List<Node2> children);
	}

	@Iri("http://www.example.com/rdf/2007/Node")
	public interface Node3 {
		@Iri("http://www.example.com/rdf/2007/child")
		public Set<Node3> getChildren();

		public void setChildren(Set<Node3> children);
	}

    @Iri("http://www.example.com/rdf/2007/Node4")
	public interface Node4 {
		@Iri("http://www.example.com/rdf/2007/child")
		public Set<Node4> getChildren();

		public void setChildren(Set<Node4> children);
	}

	@Iri("http://www.example.com/rdf/2007/Node")
	public interface Node2SetConcept {
		@Iri("http://www.example.com/rdf/2007/child")
		public abstract Set<Node2> getChildSet();

		public abstract void setChildSet(Set<Node2> children);
	}

	public static abstract class NodeWithoutOrderedChildrenSupport implements
			Node2SetConcept {
		public java.util.List<Node2> getChildren() {
			return new ArrayList<Node2>(getChildSet());
		}

		public void setChildren(java.util.List<Node2> children) {
			setChildSet(new HashSet<Node2>(children));
		}
	}

	public static abstract class PersonalBehaviour implements EmailUser {

		public boolean readEmail(EmailMessage message) {
			String un = getUserName();
			if (message.getToEmailAddress().equals(un + "@example.com")) {
				// process email here
				return true;
			}
			return false;
		}
	}

	public static final class PropertyChangeListenerImpl implements
			PropertyChangeListener {
		private boolean updated;

		public boolean isUpdated() {
			return updated;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			updated = true;
		}
	}

	@Iri("http://www.example.com/rdf/2007/Salesman")
	public interface Salesman extends Employee {

		@Iri("http://www.example.com/rdf/2007/targetUnits")
		public int getTargetUnits();

		@Iri("http://www.example.com/rdf/2007/unitsSold")
		public int getUnitsSold();

		public void setTargetUnits(int target);

		public void setUnitsSold(int units);
	}

	public static abstract class SalesmanBonusBehaviour implements Salesman {
		public double calculateExpectedBonus(double percent) {
			int units = getUnitsSold();
			int target = getTargetUnits();
			if (units > target) {
				return percent * getSalary() * units / target;
			}
			return 0;
		}
	}

	public interface SupportAgent {
		// Concept identifier
	}

	@Iri("http://www.example.com/rdf/2007/User")
	public interface User {

		@Iri("http://www.example.com/rdf/2007/userName")
		public String getUserName();

		public void setUserName(String name);
	}

	private ObjectRepository factory;

	private ObjectConnection manager;

	public void testBehaviour1() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Node1.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		Node1 node = manager.addDesignation(manager.getObjectFactory().createObject(), Node1.class);

		// All setter calls use a bean created by the manager.
		java.util.List<Node1> children = manager.addDesignation(manager.getObjectFactory().createObject(), Seq.class);

		Node1 childNode = manager.addDesignation(manager.getObjectFactory().createObject(), Node1.class);
		children.add(childNode);

		node.setChildren(children);

		assertEquals(1, node.getChildren().size());
	}

//	public void testBehaviour2() throws Exception {
//		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
//		module.addConcept(Node2SetConcept.class);
//		module.addBehaviour(NodeWithoutOrderedChildrenSupport.class);
//		module.addConcept(Node2.class);
//		factory = new ObjectRepositoryFactory().createRepository(module, repository);
//		manager = factory.getConnection();
//
//		Node2 node = manager.addDesignation(manager.getObjectFactory().createObject(), Node2.class);
//
//		java.util.List<Node2> children = new ArrayList<Node2>();
//
//		Node2 childNode = manager.addDesignation(manager.getObjectFactory().createObject(), Node2.class);
//		children.add(childNode);
//
//		node.setChildren(children);
//
//		assertEquals(1, node.getChildren().size());
//	}

	@Ignore // TODO check this test again
	public void testInterceptor2() throws Exception {
		// The RDfBean Seq can also be created within the behaviour.

		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Node2.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		Node2 node = manager.addDesignation(manager.getObjectFactory().createObject(), Node2.class);

		java.util.List<Node2> children = new ArrayList<Node2>();

		Node2 childNode = manager.addDesignation(manager.getObjectFactory().createObject(), Node2.class);
		children.add(childNode);

		node.setChildren(children);

		assertEquals(1, node.getChildren().size());
	}

	public void testChainOfResponsibility() throws Exception {
		String agentType = NS + "SupportAgent";
		String userType = NS + "User";
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addBehaviour(ITSupportAgent.class, new URIImpl(agentType));
		module.addConcept(SupportAgent.class, new URIImpl(agentType));
		module.addBehaviour(PersonalBehaviour.class);
		module.addConcept(EmailUser.class, new URIImpl(userType));
		module.addConcept(User.class, new URIImpl(userType));
		module.addConcept(EmailMessage.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		URI id = ValueFactoryImpl.getInstance().createURI(NS, "E340076");
		manager.addDesignation(manager.getObject(id), SupportAgent.class);
		manager.addDesignation(manager.getObject(id), User.class);

		EmailUser user = (EmailUser) manager.getObject(id);
		user.setUserName("john");
		EmailMessage message = manager.addDesignation(manager.getObjectFactory().createObject(), EmailMessage.class);
		message.setToEmailAddress("john@example.com");
		if (!user.readEmail(message)) {
			fail();
		}
	}

	public void testConcept1() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Node4.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		Node4 node = manager.addDesignation(manager.getObjectFactory().createObject(), Node4.class);

		Set<Node4> children = new HashSet<>();

		Node4 childNode = manager.addDesignation(manager.getObjectFactory().createObject(), Node4.class);
		children.add(childNode);

		node.setChildren(children);

		assertEquals(1, node.getChildren().size());
	}

	public void testConcept2() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Engineer.class, new URIImpl(("http://www.example.org/rdf/2007/"
		+ "Engineer")));
		// uri type of Salesman is retrieved from the @rdf annotation
		module.addConcept(Salesman.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		Engineer eng = manager.addDesignation(manager.getObjectFactory().createObject(), Engineer.class);
		assertNotNull(eng);
		Salesman sales = manager.addDesignation(manager.getObjectFactory().createObject(), Salesman.class);
		assertNotNull(sales);
	}

	public void testContextSpecificData() throws Exception {
		URI c = new URIImpl(NS + "Period#common");
		URI p1 = new URIImpl(NS + "Period#1");
		URI p2 = new URIImpl(NS + "Period#2");
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Employee.class);
		module.addConcept(Salesman.class);
		module.addConcept(Engineer.class);
		module.addBehaviour(SalesmanBonusBehaviour.class);
		module.addBehaviour(EngineerBonusBehaviour.class);
		module.setAddContexts(c);
		module.setRemoveContexts(c);
		module.setReadContexts(c);
		ObjectRepositoryConfig m1 = module.clone();
		m1.setAddContexts(p1);
		m1.setRemoveContexts(c, p1);
		m1.setReadContexts(c, p1);
		ObjectRepositoryConfig m2 = module.clone();
		m2.setAddContexts(p2);
		m2.setRemoveContexts(c, p2);
		m2.setReadContexts(c, p2);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		ObjectRepository f1 = new ObjectRepositoryFactory().createRepository(m1, repository);
		ObjectRepository f2 = new ObjectRepositoryFactory().createRepository(m2, repository);
		ObjectConnection common = factory.getConnection();
		ObjectConnection period1 = f1.getConnection();
		ObjectConnection period2 = f2.getConnection();
		String qry = "SELECT ?s WHERE { ?j <http://www.example.com/rdf/2007/salary> ?s}";
		try {

			Employee emp;
			Object obj;
			URI id = ValueFactoryImpl.getInstance().createURI(NS, "E340076");
			emp = common.addDesignation(common.getObject(id), Employee.class);
			emp.setName("John");
			Salesman slm = period1.addDesignation(period1.getObject(id), Salesman.class);
			slm.setTargetUnits(10);
			slm.setUnitsSold(15);
			slm.setSalary(90);
			Engineer eng = period2.addDesignation(period2.getObject(id), Engineer.class);
			eng.setBonusTargetMet(true);
			eng.setSalary(100);

			obj = common.getObject(id);
			assertTrue(obj instanceof Employee);
			assertFalse(obj instanceof Salesman);
			assertFalse(obj instanceof Engineer);
			emp = (Employee) obj;
			assertEquals("John", emp.getName());
			assertEquals(0.0, emp.getSalary(), 0);
			assertTrue(common.prepareObjectQuery(qry).evaluate().asList().isEmpty());

			obj = period1.getObject(id);
			assertTrue(obj instanceof Employee);
			assertTrue(obj instanceof Salesman);
			assertFalse(obj instanceof Engineer);
			emp = (Employee) obj;
			assertEquals("John", emp.getName());
			assertEquals(90.0, emp.getSalary(), 0);
			assertEquals(6.75, emp.calculateExpectedBonus(0.05), 0);
			assertEquals(90.0, period1.prepareObjectQuery(qry).evaluate().singleResult());

			obj = period2.getObject(id);
			assertTrue(obj instanceof Employee);
			assertFalse(obj instanceof Salesman);
			assertTrue(obj instanceof Engineer);
			emp = (Employee) obj;
			assertEquals("John", emp.getName());
			assertEquals(100.0, emp.getSalary(), 0);
			assertEquals(5, emp.calculateExpectedBonus(0.05), 0);
			assertEquals(100.0, period2.prepareObjectQuery(qry).evaluate().singleResult());
		} finally {
			common.close();
			period1.close();
			period2.close();
			f1.shutDown();
			f2.shutDown();
		}
	}

	@Iri("http://www.w3.org/2000/01/rdf-schema#Resource")
	public interface MyResource {

		/** A name given to the resource. */
		@Iri("urn:test:title")
		public abstract String getDcTitle();

		/** A name given to the resource. */
		public abstract void setDcTitle(String value);
		
	}

	public void testElmoManager1() throws Exception {
		assert Salesman.class.isInterface();
		assert Engineer.class.isInterface();

		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Engineer.class);
		module.addConcept(Salesman.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		URI id = ValueFactoryImpl.getInstance().createURI(NS, "E340076");
		manager.addDesignation(manager.getObject(id), Salesman.class);
		Object john = manager.addDesignation(manager.getObject(id), Engineer.class);

		assertTrue(john instanceof Engineer);
		assertTrue(john instanceof Salesman);
	}

	public void testElmoManager2() throws Exception {
		factory = new ObjectRepositoryFactory().createRepository(new ObjectRepositoryConfig(), repository);
		manager = factory.getConnection();

		String ns = NS;
		URI id = ValueFactoryImpl.getInstance().createURI(ns, "E340076");
		Object john = manager.getObject(id);

		assertNotNull(john);
		assertEquals(id, manager.addObject(john));

		// the subject john has the uri of
		// "http://www.example.com/rdf/2007/E340076"
	}

	public void testElmoManager3() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Employee.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		URI id = ValueFactoryImpl.getInstance().createURI(NS, "E340076");
		Employee john = manager.addDesignation(manager.getObject(id), Employee.class);
		Employee jonny = (Employee) manager.getObject(id);

		assert john.equals(jonny);

		john.setName("John");
		assert jonny.getName().equals("John");
	}

	public void testElmoQuery() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Employee.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		factory.setQueryLanguage(QueryLanguage.SERQL);
		manager = factory.getConnection();

		Employee john = manager.addDesignation(manager.getObjectFactory().createObject(), Employee.class);
		john.setName("John");

		String queryStr = "SELECT emp FROM {emp} <http://www.example.com/rdf/2007/name> {name}";
		ObjectQuery query = manager.prepareObjectQuery(queryStr);
		query.setObject("name", "John");
		int count = 0;
		for (Object obj : query.evaluate().asList()) {
			Employee emp = (Employee) obj;
			count++;
			assert emp.getName().equals("John");
		}
		assertEquals(1, count);
	}

	public void testInterceptor1() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addBehaviour(EmailValidator.class, new URIImpl("http://www.example.com/rdf/2007/EmailMessage"));
		module.addConcept(EmailMessage.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		EmailMessage message = manager.addDesignation(manager.getObjectFactory().createObject(), EmailMessage.class);
		message.setFromEmailAddress("john@example.com"); // okay
		try {
			message.setToEmailAddress("jonny@invalid-example.com");
			fail();
		} catch (IllegalArgumentException e) {
			// invalid email
		}
	}

	public void testLocking() throws Exception {
		repository = new NotifyingRepositoryWrapper(repository, true);
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addConcept(Employee.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();
		for (int i = 0; i < 20; i++) {
			Employee emp = manager.addDesignation(manager.getObjectFactory().createObject(), Employee.class);
			emp.setName("Emp" + i);
			emp.setAddress(i + " street");
			emp.setPhoneNumber("555-" + i + i);
			emp.setEmailAddress("emp" + i + "@example.com");
		}
		Result<Employee> beans = manager.getObjects(Employee.class);
		Employee first = beans.next();
		beans.close();
		first.setName(first.getName().replaceAll("Emp", "Employee Number "));
		for (Employee emp : manager.getObjects(Employee.class).asList()) {
			emp.setName(emp.getName().replaceAll("Emp", "Employee Number "));
		}
	}

	public void testStrategy() throws Exception {
		ObjectRepositoryConfig module = new ObjectRepositoryConfig();
		module.addBehaviour(SalesmanBonusBehaviour.class);
		module.addBehaviour(EngineerBonusBehaviour.class);
		module.addConcept(Engineer.class);
		factory = new ObjectRepositoryFactory().createRepository(module, repository);
		manager = factory.getConnection();

		URI id = ValueFactoryImpl.getInstance().createURI(NS, "E340076");
		Engineer eng = manager.addDesignation(manager.getObject(id), Engineer.class);
		eng.setBonusTargetMet(true);
		eng.setSalary(100);

		Employee employee = (Employee) manager.getObject(id);
		double bonus = employee.calculateExpectedBonus(0.05);

		assertEquals("bonus", 5.0, bonus, 0);
	}

	@Override
	protected void tearDown() throws Exception {
		if (manager != null)
			manager.close();
		if (factory != null) {
			factory.shutDown();
		}
		super.tearDown();
	}
}
