package org.openrdf.repository.object.codegen;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectService;
import org.openrdf.repository.object.ObjectServiceImpl;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.compiler.OWLCompiler;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.ContextStatementCollector;

public class RecompileTest extends ObjectRepositoryTestCase {
	ObjectService service;

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(RecompileTest.class);
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
		con.setNamespace("", "urn:dynamic:");
		URI property = vf.createURI("urn:dynamic:property");
		con.add(property, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		con.add(property, RDFS.RANGE, XMLSchema.STRING);
		con.close();
		recompileSchema();
		con = con.getRepository().getConnection();
		Object obj = con.getObject("urn:test:resource");
		obj.getClass().getMethod("getProperty");
	}

	public void testInvalid() throws Exception {
		ValueFactory vf = con.getValueFactory();
		con.setNamespace("", "urn:dynamic:");
		URI property = vf.createURI("urn:dynamic:property");
		con.add(property, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		con.add(property, RDFS.RANGE, XMLSchema.BASE64BINARY);
		con.close();
		recompileSchema();
		con = con.getRepository().getConnection();
		con.remove(property, RDFS.RANGE, null);
		con.add(property, RDFS.RANGE, XMLSchema.STRING);
		con.close();
		recompileSchema();
		con = con.getRepository().getConnection();
		Object obj = con.getObject("urn:test:resource");
		obj.getClass().getMethod("getProperty");
	}

	public void testUnionOf() throws Exception {
		ValueFactory vf = con.getValueFactory();
		con.setNamespace("", "urn:dynamic:");
		URI property = vf.createURI("urn:dynamic:property");
		con.add(property, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		con.add(property, RDFS.RANGE, XMLSchema.STRING);
		Resource node = vf.createBNode();
		con.add(property, RDFS.DOMAIN, node);
		Resource list = vf.createBNode();
		con.add(node, OWL.UNIONOF, list);
		con.add(list, RDF.FIRST, vf.createURI("urn:mimetype:text/html"));
		Resource rest = vf.createBNode();
		con.add(list, RDF.REST, rest);
		con.add(rest, RDF.FIRST, vf.createURI("urn:mimetype:image/gif"));
		con.add(rest, RDF.REST, RDF.NIL);
		con.close();
		recompileSchema();
		con = con.getRepository().getConnection();
		con.add(vf.createURI("urn:test:resource"), RDF.TYPE, vf.createURI("urn:mimetype:text/html"));
		Object obj = con.getObject("urn:test:resource");
		obj.getClass().getMethod("getProperty");
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
