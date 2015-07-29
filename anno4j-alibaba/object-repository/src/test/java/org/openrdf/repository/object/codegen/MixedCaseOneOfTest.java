package org.openrdf.repository.object.codegen;

import java.io.File;
import java.io.FileWriter;

import org.openrdf.repository.object.base.CodeGenTestCase;

public class MixedCaseOneOfTest extends CodeGenTestCase {

	public void test() throws Exception {
		String RDF_XML = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#' xmlns:owl='http://www.w3.org/2002/07/owl#'>\n" +
		"<rdf:Description rdf:about='#UnitSymbol'>\n" +
		"<owl:oneOf rdf:nodeID='Xd2X26cfd7d5Xa3X12e4f66a852Xa3X-346'/>\n" +
		"<rdfs:label>UnitSymbol</rdfs:label>\n" +
		"<rdf:type rdf:resource='http://www.w3.org/2002/07/owl#Class'/>\n" +
		"</rdf:Description>\n" +
		"<rdf:Description rdf:nodeID='Xd2X26cfd7d5Xa3X12e4f66a852Xa3X-346'>\n" +
		"<rdf:rest rdf:nodeID='Xd2X26cfd7d5Xa3X12e4f66a852Xa3X-347'/>\n" +
		"<rdf:first rdf:resource='http://iec.ch/TC57/2010/CIM-schema-cim15#UnitSymbol.H'/>\n" +
		"</rdf:Description>\n" +
		"<rdf:Description rdf:nodeID='Xd2X26cfd7d5Xa3X12e4f66a852Xa3X-347'>\n" +
		"<rdf:rest rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n" +
		"<rdf:first rdf:resource='http://iec.ch/TC57/2010/CIM-schema-cim15#UnitSymbol.h'/>\n" +
		"</rdf:Description>\n" +
		   
		"<rdf:Description rdf:about='http://iec.ch/TC57/2010/CIM-schema-cim15#UnitSymbol.H'>\n" +
		"<rdfs:label>H</rdfs:label>\n" +
		"</rdf:Description>\n" +
		"<rdf:Description rdf:about='http://iec.ch/TC57/2010/CIM-schema-cim15#UnitSymbol.h'>\n" +
		"<rdfs:label>h</rdfs:label>\n" +
		"</rdf:Description>\n" +
		"</rdf:RDF>\n";
		File file = new File(targetDir, "mixedCaseOneOf.rdf");
		FileWriter writer = new FileWriter(file);
		writer.write(RDF_XML);
		writer.close();
		addImports(file.toURI().toURL());
		createJar("mixedCaseOneOf.jar");
	}

}
