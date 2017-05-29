package org.openrdf.repository.object.advisers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedOperation;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.object.advisers.helpers.SparqlEvaluator;
import org.openrdf.repository.object.managers.PropertyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlQuery {
	private static final Pattern selectWhere = Pattern.compile(
			"\\sSELECT\\s+([\\?\\$]\\w+)(\\s+WHERE)?\\s*\\{",
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern limitOffset = Pattern.compile(
			"\\bLIMIT\\b|\\bOFFSET\\b", Pattern.CASE_INSENSITIVE);

	private final Logger logger = LoggerFactory.getLogger(SparqlQuery.class);
	private String sparql;
	private String base;
	private Class<?> concept;
	private String object;
	private ParsedOperation query;

	public SparqlQuery(Reader in, String base) throws IOException,
			MalformedQueryException {
		try {
			StringWriter sw = new StringWriter();
			int read;
			char[] cbuf = new char[1024];
			while ((read = in.read(cbuf)) >= 0) {
				sw.write(cbuf, 0, read);
			}
			sparql = sw.toString();
			this.base = base;
			try {
				query = new SPARQLParser().parseQuery(sparql, base);
			} catch (MalformedQueryException e) {
				try {
					query = new SPARQLParser().parseUpdate(sparql, base);
				} catch (MalformedQueryException u) {
					throw e;
				}
			}
		} catch (MalformedQueryException e) {
			logger.warn(base + " " + e.getMessage(), e);
		}
	}

	public String getBaseURI() {
		return base;
	}

	public boolean isBooleanQuery() {
		return query instanceof ParsedBooleanQuery;
	}

	public boolean isGraphQuery() {
		return query instanceof ParsedGraphQuery;
	}

	public boolean isTupleQuery() {
		return query instanceof ParsedTupleQuery;
	}

	public synchronized String toObjectString(Class<?> concept) {
		if (concept.equals(this.concept))
			return object;
		if (isTupleQuery()) {
			this.concept = concept;
			ClassLoader cl = concept.getClassLoader();
			if (cl == null) {
				cl = SparqlEvaluator.class.getClassLoader();
			}
			PropertyMapper pm = new PropertyMapper(cl, true);
			Map<String, String> eager = pm.findEagerProperties(concept);
			object = optimizeQueryString(sparql, eager);
		} else {
			this.concept = concept;
			object = sparql;
		}
		return object;
	}

	public String toString() {
		return sparql;
	}

	/**
	 * @param map
	 *            property name to predicate uri or null for datatype
	 */
	private String optimizeQueryString(String sparql,
			Map<String, String> map) {
		Matcher matcher = selectWhere.matcher(sparql);
		if (map != null && matcher.find()
				&& !limitOffset.matcher(sparql).find()) {
			String var = matcher.group(1);
			int idx = sparql.lastIndexOf('}');
			StringBuilder sb = new StringBuilder(256 + sparql.length());
			sb.append(sparql, 0, matcher.start(1));
			sb.append(var).append(" ");
			sb.append(var).append("_class").append(" ");
			for (Map.Entry<String, String> e : map.entrySet()) {
				String name = e.getKey();
				if (name.equals("class"))
					continue;
				sb.append(var).append("_").append(name).append(" ");
			}
			sb.append(sparql, matcher.end(1), idx);
			sb.append(" OPTIONAL { ").append(var);
			sb.append(" a ").append(var).append("_class}");
			for (Map.Entry<String, String> e : map.entrySet()) {
				String pred = e.getValue();
				String name = e.getKey();
				if (name.equals("class"))
					continue;
				sb.append(" OPTIONAL { ").append(var).append(" <");
				sb.append(pred).append("> ");
				sb.append(var).append("_").append(name).append("}");
			}
			sb.append(sparql, idx, sparql.length());
			return sb.toString();
		}
		return sparql;
	}
}