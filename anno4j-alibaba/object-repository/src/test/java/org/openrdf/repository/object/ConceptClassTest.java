package org.openrdf.repository.object;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Matching;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.annotations.Sparql;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.traits.Mergeable;
import org.openrdf.repository.object.traits.ObjectMessage;

public class ConceptClassTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(ConceptClassTest.class);
	}

	@Iri("urn:test:Throwable")
	public interface IThrowable {
		@Iri("urn:test:cause")
		IThrowable getStoredCause();

		void setStoredCause(IThrowable cause);

		@Iri("urn:test:message")
		String getMessage();

		void setMessage(String message);

		@Iri("urn:test:stackTrace")
		List<StackTraceItem> getStackTraceItems();

		void setStackTraceItems(List<StackTraceItem> list);
	}

	public abstract static class ThrowableMerger implements IThrowable,
			Mergeable, RDFObject {
		public void merge(Object source) throws RepositoryException {
			if (source instanceof Throwable) {
				Throwable t = (Throwable) source;
				setMessage(t.getMessage());
				setStackTraceItems((List) Arrays.asList(t.getStackTrace()));
				Throwable cause = t.getCause();
				if (cause != null) {
					ObjectConnection r = getObjectConnection();
					ObjectFactory of = r.getObjectFactory();
					setStoredCause(of.createObject((Resource) r.addObject(cause), IThrowable.class));
				}
			}
		}
	}

	@Iri("urn:test:StackTrace")
	public interface StackTraceItem {
		@Iri("urn:test:className")
		String getClassName();

		void setClassName(String value);

		@Iri("urn:test:fileName")
		String getFileName();

		void setFileName(String value);

		@Iri("urn:test:lineNumber")
		int getLineNumber();

		void setLineNumber(int value);

		@Iri("urn:test:methodName")
		String getMethodName();

		void setMethodName(String value);

		@Iri("urn:test:nativeMethod")
		boolean isNativeMethod();

		void setNativeMethod(boolean value);
	}

	public abstract static class StackTraceItemMereger implements
			StackTraceItem, Mergeable {
		public void merge(Object source) {
			if (source instanceof StackTraceElement) {
				StackTraceElement e = (StackTraceElement) source;
				setClassName(e.getClassName());
				setFileName(e.getFileName());
				setLineNumber(e.getLineNumber());
				setMethodName(e.getMethodName());
				setNativeMethod(e.isNativeMethod());
			}
		}
	}

	@Iri("urn:test:CodeException")
	public static class CodeException extends Exception {
		private static final long serialVersionUID = 6831592297981512051L;
		private int code;

		public CodeException() {
			super();
		}

		public CodeException(String message, Throwable cause) {
			super(message, cause);
		}

		public CodeException(String message) {
			super(message);
		}

		public CodeException(int code) {
			this.code = code;
		}

		@Iri("urn:test:code")
		public int getCode() {
			return code;
		}
	}

	@Iri("urn:test:Person")
	public static class Person {
		private String surname;
		private Set<String> givenNames = new HashSet<String>();
		private Person spouse;

		@Iri("urn:test:surname")
		public String getSurname() {
			return surname;
		}

		@Iri("urn:test:surname")
		public void setSurname(String surname) {
			this.surname = surname;
		}

		@Iri("urn:test:givenNames")
		public Set<String> getGivenNames() {
			return givenNames;
		}

		@Iri("urn:test:givenNames")
		public void setGivenNames(Set<String> givenNames) {
			this.givenNames = givenNames;
		}

		@Iri("urn:test:spouse")
		public Person getSpouse() {
			return spouse;
		}

		@Iri("urn:test:spouse")
		public void setSpouse(Person spouse) {
			this.spouse = spouse;
		}

		public boolean isMarried() {
			return getSpouse() != null;
		}

		public String toString() {
			return getGivenNames() + " " + getSurname();
		}
	}

	@Iri("urn:test:Compnay")
	public static class Company {
		private String name;
		private Set<Person> employees = new HashSet<Person>();

		@Iri("urn:test:name")
		public String getName() {
			return name;
		}

		@Iri("urn:test:name")
		public void setName(String name) {
			this.name = name;
		}

		boolean isNamePresent() {
			return this.name != null;
		}

		@Iri("urn:test:employees")
		public Set<Person> getEmployees() {
			return employees;
		}

		@Iri("urn:test:employees")
		public void setEmployees(Set<Person> employees) {
			this.employees = employees;
		}

		@Sparql("SELECT ?employee ?employee_class ?employee_givenNames {\n"
				+ "$this <urn:test:employees> ?employee\n"
				+ "OPTIONAL {?employee a ?employee_class}\n"
				+ "OPTIONAL {?employee <urn:test:givenNames> ?employee_givenNames}\n"
				+ "}")
		public Set<Person> getEmployeesWithGivenNames() {
			return getEmployees();
		}

		public boolean isEmployed(Person employee) {
			return getEmployees().contains(employee);
		}
	
		public Person findByGivenName(String given) {
			Person found = null;
			for (Person person : getEmployees()) {
				if (person.getGivenNames().contains(given)) {
					found = person;
				}
			}
			return found;
		}
	}

	@Matching("file:*")
	public interface LocalFile {
		String getName();
	}

	public static abstract class LocalFileImpl implements LocalFile, RDFObject {
		@ParameterTypes({})
		public String getName(ObjectMessage msg) throws Exception {
			String ret = (String) msg.proceed();
			if (ret == null) {
				String uri = getResource().stringValue();
				return uri.substring(uri.lastIndexOf('/') + 1);
			}
			return ret;
		}
	}

	public void testException() throws Exception {
		CodeException e1 = new CodeException(47);
		Exception e = new Exception("my message", e1);
		RDFObject bean = (RDFObject) ((Exception) con.getObject(con.addObject(e)));
		Method method = bean.getClass().getMethod("getMessage");
		assertEquals("my message", method.invoke(bean));
		method = bean.getClass().getMethod("getStackTraceItems");
		List list = (List) method.invoke(bean);
		Object st = list.get(0);
		assertTrue(st instanceof StackTraceItem);
		method = st.getClass().getMethod("getClassName");
		assertEquals(getClass().getName(), method.invoke(st));
	}

	public void test_company() throws Exception {
		Company c = new Company();
		Person p = new Person();
		Person w = new Person();
		c.setName("My Company");
		p.getGivenNames().add("me");
		w.getGivenNames().add("my");
		w.setSurname("wife");
		p.setSpouse(w);
		c.getEmployees().add(p);
		c = (Company) con.getObject(con.addObject(c));
		p = c.findByGivenName("me");
		w = p.getSpouse();
		assertTrue(p.isMarried());
		assertEquals(Collections.singleton("me"), p.getGivenNames());
		assertEquals("wife", w.getSurname());
		assertTrue(c.isEmployed(p));
		assertFalse(c.isNamePresent());
		c.setName(c.getName());
		assertTrue(c.isNamePresent());
		assertEquals("my wife", w.toString());
	}

	public void testCompanyName() throws Exception {
		Company c = con.addDesignation(con.getObject("file:///tmp/company.txt"), Company.class);
		assertEquals("company.txt", c.getName());
		c.setName("My Company");
		assertEquals("My Company", c.getName());
	}

	public void testEagerEmployeeSurname() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:company> a <urn:test:Compnay>; <urn:test:employees> <urn:test:employee>}").execute();
		con.prepareUpdate("INSERT DATA { <urn:test:employee> a <urn:test:Person>; <urn:test:surname> 'Smith'}").execute();
		Company c = con.getObject(Company.class, "urn:test:company");
		Person e = c.getEmployees().iterator().next();
		con.prepareUpdate("DELETE {?person <urn:test:surname> 'Smith'} INSERT {?person <urn:test:surname> 'Leigh'} WHERE {?person <urn:test:surname> 'Smith'}").execute();
		assertEquals("Smith", e.getSurname());
	}

	public void testEagerEmployeeGivenName() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:company> a <urn:test:Compnay>; <urn:test:employees> <urn:test:employee>}").execute();
		con.prepareUpdate("INSERT DATA { <urn:test:employee> a <urn:test:Person>; <urn:test:givenNames> 'Megan'; <urn:test:surname> 'Smith'}").execute();
		Company c = con.getObject(Company.class, "urn:test:company");
		Person e = c.getEmployeesWithGivenNames().iterator().next();
		con.prepareUpdate("DELETE {?person <urn:test:givenNames> 'Megan'} INSERT {?person <urn:test:givenNames> 'Clair'} WHERE {?person <urn:test:givenNames> 'Megan'}").execute();
		assertEquals(Collections.singleton("Megan"), e.getGivenNames());
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(Throwable.class, new URIImpl("urn:test:Throwable"));
		config.addConcept(StackTraceElement.class, new URIImpl("urn:test:StackTrace"));
		config.addConcept(Exception.class, new URIImpl("urn:test:Exception"));
		config.addConcept(CodeException.class);
		config.addConcept(IThrowable.class);
		config.addBehaviour(ThrowableMerger.class);
		config.addConcept(StackTraceItem.class);
		config.addBehaviour(StackTraceItemMereger.class);
		config.addConcept(Person.class);
		config.addConcept(Company.class);
		config.addConcept(LocalFile.class);
		config.addBehaviour(LocalFileImpl.class);
		super.setUp();
	}

}
