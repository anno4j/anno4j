package org.openrdf.repository.object.codegen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import junit.framework.Test;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectService;
import org.openrdf.repository.object.ObjectServiceImpl;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.compiler.OWLCompiler;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.vocabulary.MSG;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.ContextStatementCollector;

public class ClasspathMixinTest extends ObjectRepositoryTestCase {
	ObjectService service;

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(ClasspathMixinTest.class);
	}

	public static abstract class BehaviourClass {
		public boolean isBehaviourClass() {
			return true;
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		service = new ObjectServiceImpl();
		con.getRepository().setObjectService(new ObjectService() {
			public ObjectFactory createObjectFactory() {
				return service.createObjectFactory();
			}
		});
	}

	public void testValid() throws Exception {
		ValueFactory vf = con.getValueFactory();
		con.setNamespace("test", "urn:test:");
		URI thing = vf.createURI("urn:test:thing");
		URI Thing = vf.createURI("urn:test:Thing");
		con.add(Thing, RDF.TYPE, OWL.CLASS);
		Class<BehaviourClass> bc = BehaviourClass.class;
		con.add(Thing, MSG.MIXIN, vf.createLiteral(bc.getName()));
		con.add(Thing, MSG.CLASSPATH, vf.createURI(getJarLocation(bc)));
		con.add(thing, RDF.TYPE, Thing);
		con.close();
		recompileSchema();
		con = con.getRepository().getConnection();
		Object obj = con.getObject("urn:test:thing");
		Method isBehaviourClass = obj.getClass().getMethod("isBehaviourClass");
		assertEquals(Boolean.TRUE, isBehaviourClass.invoke(obj));
	}

	private String getJarLocation(Class<BehaviourClass> bc) {
		String name = '/' + bc.getName().replace('.', '/') + ".class";
		String url = bc.getResource(name).toExternalForm();
		if (url.startsWith("jar:") && url.endsWith("!" + name))
			return url.substring("jar:".length(), url.lastIndexOf('!'));
		if (url.endsWith(name))
			return url.substring(0, url.length() - name.length());
		return null;
	}

	private void recompileSchema() throws IOException, RepositoryException,
			RDFHandlerException, ObjectStoreConfigException {
		con = con.getRepository().getConnection();
		try {
			Model schema = new TreeModel();
			ContextStatementCollector collector = new ContextStatementCollector(
					schema, con.getValueFactory());
			con.export(collector);
			OWLCompiler compiler = new OWLCompiler();
			compiler.setModel(schema);
			compiler.setNamespaces(collector.getNamespaces());
			File concepts = File.createTempFile("recompile", ".jar");
			ClassLoader cl = compiler.createJar(concepts);
			service = new ObjectServiceImpl(cl);
		} finally {
			con.close();
		}
	}
}
