package com.github.anno4j.rdf_generation.validation;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLValidator {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		DocumentBuilder builder = factory.newDocumentBuilder();

		builder.setErrorHandler(new SimpleErrorHandler());
		// the "parse" method also validates XML, will throw an exception if
		// misformatted
		try {
			Document document = builder.parse(new InputSource("C:\\Users\\Brinninger Sandra\\Documents\\result.txt"));
			System.out.println("document is correctly formatted!");
		} catch (SAXException e) {
			System.out.println("document is misformatted!");
		} catch (IOException e) {}
	}

}
