package org.openrdf.repository.object;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public class DocumentFragmentTest extends ObjectRepositoryTestCase {
	private URI pred;

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(DocumentFragmentTest.class);
	}

	@Iri("urn:test:Entity")
	public interface Entity extends RDFObject {
		@Iri("urn:test:xml")
		DocumentFragment getXML();

		void setXML(DocumentFragment xml);
	}

	public void setUp() throws Exception {
		config.addConcept(Entity.class);
		super.setUp();
		pred = con.getValueFactory().createURI("urn:test:xml");
	}

	public void testAddSingleElement() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Document doc = parse("<element/>");
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement());
		entity.setXML(frag);
		RepositoryResult<Statement> results = con.getStatements(entity.getResource(), pred, null);
		String xml = results.next().getObject().stringValue();
		results.close();
		assertEquals("<element/>", xml);
	}

	public void testReadSingleElement() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Document doc = parse("<element/>");
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement());
		String before = toString(frag);
		entity.setXML(frag);
		entity = (Entity) con.getObject(entity.getResource());
		frag = entity.getXML();
		assertEquals(before, toString(frag));
	}

	public void testAddMultipleElements() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Document doc = parse("<element><first/><second/></element>");
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement().getFirstChild());
		frag.appendChild(doc.getDocumentElement().getLastChild());
		entity.setXML(frag);
		RepositoryResult<Statement> results = con.getStatements(entity.getResource(), pred, null);
		String xml = results.next().getObject().stringValue();
		results.close();
		assertEquals("<first/><second/>", xml);
	}

	public void testReadMultipleElements() throws Exception {
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		Document doc = parse("<element><first/><second/></element>");
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement().getFirstChild());
		frag.appendChild(doc.getDocumentElement().getLastChild());
		String before = toString(frag);
		entity.setXML(frag);
		entity = (Entity) con.getObject(entity.getResource());
		DocumentFragment xml = entity.getXML();
		assertEquals(before, toString(xml));
	}

	public void testAddNamespaceElement() throws Exception {
		String xml = "<a:Box xmlns:a=\"http://example.org/a#\" required=\"true\"><a:widget size=\"10\"> </a:widget><a:grommit id=\"23\"> text </a:grommit></a:Box>";
		Document doc = parse(xml);
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement());
		entity.setXML(frag);
		RepositoryResult<Statement> resuts = con.getStatements(entity.getResource(), pred, null);
		String label = resuts.next().getObject().stringValue();
		resuts.close();
		assertEquals(xml, label);
	}

	public void testReadNamespaceElement() throws Exception {
		String xml = "<a:Box xmlns:a=\"http://example.org/a#\" required=\"true\"><a:widget size=\"10\"> </a:widget><a:grommit id=\"23\"> text </a:grommit></a:Box>";
		Document doc = parse(xml);
		ObjectFactory of = con.getObjectFactory();
		Entity entity = con.addDesignation(of.createObject(), Entity.class);
		DocumentFragment frag = doc.createDocumentFragment();
		frag.appendChild(doc.getDocumentElement());
		String before = toString(frag);
		entity.setXML(frag);
		entity = (Entity) con.getObject(entity.getResource());
		frag = entity.getXML();
		assertEquals(before, toString(frag));
	}

	private Document parse(String xml) throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
		builder.setNamespaceAware(true);
		builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Source source = new StreamSource(new StringReader(xml));
		Document doc = builder.newDocumentBuilder().newDocument();
		DOMResult result = new DOMResult(doc);
		factory.newTransformer().transform(source, result);
		return doc;
	}

	private String toString(Node node) throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		Source source = new DOMSource(node);
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		factory.newTransformer().transform(source, result);
		return writer.toString();
	}

}
