package org.openrdf.repository.object.advisers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.openrdf.query.MalformedQueryException;

public class SparqlQueryResolver {
	private static SparqlQueryResolver instance = new SparqlQueryResolver();

	public static synchronized SparqlQueryResolver getInstance() {
		return instance;
	}

	public static synchronized void setInstance(SparqlQueryResolver resolver) {
		instance = resolver;
	}

	public SparqlQuery resolve(String systemId) throws IOException,
			MalformedQueryException {
		URLConnection con = new URL(systemId).openConnection();
		con.addRequestProperty("Accept", "application/sparql-query");
		con.addRequestProperty("Accept-Encoding", "gzip");
		String encoding = con.getHeaderField("Content-Encoding");
		InputStream in = con.getInputStream();
		String base = con.getURL().toExternalForm();
		if (encoding != null && encoding.contains("gzip")) {
			in = new GZIPInputStream(in);
		}
		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		try {
			return new SparqlQuery(reader, base);
		} finally {
			reader.close();
		}
	}
}
