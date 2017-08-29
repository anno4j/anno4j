/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution. 
 * - Neither the name of the openrdf.org nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.openrdf.repository.object.managers;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.annotations.Iri;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.exceptions.ObjectConversionException;
import org.openrdf.repository.object.managers.converters.BigDecimalMarshall;
import org.openrdf.repository.object.managers.converters.BigIntegerMarshall;
import org.openrdf.repository.object.managers.converters.BooleanMarshall;
import org.openrdf.repository.object.managers.converters.ByteArrayMarshall;
import org.openrdf.repository.object.managers.converters.ByteMarshall;
import org.openrdf.repository.object.managers.converters.CharacterMarshall;
import org.openrdf.repository.object.managers.converters.ClassMarshall;
import org.openrdf.repository.object.managers.converters.DateMarshall;
import org.openrdf.repository.object.managers.converters.DocumentFragmentMarshall;
import org.openrdf.repository.object.managers.converters.DoubleMarshall;
import org.openrdf.repository.object.managers.converters.DurationMarshall;
import org.openrdf.repository.object.managers.converters.FloatMarshall;
import org.openrdf.repository.object.managers.converters.GregorianCalendarMarshall;
import org.openrdf.repository.object.managers.converters.IntegerMarshall;
import org.openrdf.repository.object.managers.converters.LocaleMarshall;
import org.openrdf.repository.object.managers.converters.LongMarshall;
import org.openrdf.repository.object.managers.converters.ObjectConstructorMarshall;
import org.openrdf.repository.object.managers.converters.ObjectSerializationMarshall;
import org.openrdf.repository.object.managers.converters.PatternMarshall;
import org.openrdf.repository.object.managers.converters.QNameMarshall;
import org.openrdf.repository.object.managers.converters.ShortMarshall;
import org.openrdf.repository.object.managers.converters.SqlDateMarshall;
import org.openrdf.repository.object.managers.converters.SqlTimeMarshall;
import org.openrdf.repository.object.managers.converters.SqlTimestampMarshall;
import org.openrdf.repository.object.managers.converters.StringMarshall;
import org.openrdf.repository.object.managers.converters.LangStringMarshall;
import org.openrdf.repository.object.managers.converters.ValueOfMarshall;
import org.openrdf.repository.object.managers.converters.XMLGregorianCalendarMarshall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;

/**
 * Converts between simple Java Objects and Strings.
 * 
 * @author James Leigh
 * 
 */
public class LiteralManager implements Cloneable {
	private static final String JAVA_NS = "java:";
	private static final String LITERALS_PROPERTIES = "META-INF/org.openrdf.literals";
	private static final String DATATYPES_PROPERTIES = "META-INF/org.openrdf.datatypes";
	private final Logger logger = LoggerFactory.getLogger(LiteralManager.class);
	private ClassLoader cl;
	private final ValueFactory uf;
	private final ValueFactory lf;
	private final URI STRING;
	private final URI LANG_STRING;
	private ConcurrentMap<URI, Class<?>> javaClasses;
	private ConcurrentMap<String, Marshall<?>> marshalls;
	private ConcurrentMap<Class<?>, URI> rdfTypes;

	public LiteralManager() {
		this(ValueFactoryImpl.getInstance(), ValueFactoryImpl.getInstance());
	}

	public LiteralManager(ClassLoader cl) {
		this(ValueFactoryImpl.getInstance(), ValueFactoryImpl.getInstance());
		setClassLoader(cl);
	}

	public LiteralManager(ValueFactory uf, ValueFactory lf) {
		this.uf = uf;
		this.lf = lf;
		STRING = uf.createURI(XMLSchema.STRING.toString());
		LANG_STRING = uf.createURI(RDF.NAMESPACE + "langString");
		javaClasses = new ConcurrentHashMap<URI, Class<?>>();
		rdfTypes = new ConcurrentHashMap<Class<?>, URI>();
		marshalls = new ConcurrentHashMap<String, Marshall<?>>();
	}

	public LiteralManager clone() {
		try {
			LiteralManager cloned = (LiteralManager) super.clone();
			cloned.javaClasses = new ConcurrentHashMap<URI, Class<?>>(javaClasses);
			cloned.marshalls = new ConcurrentHashMap<String, Marshall<?>>(marshalls);
			cloned.rdfTypes = new ConcurrentHashMap<Class<?>, URI>(rdfTypes);
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	public void setClassLoader(ClassLoader cl) {
		this.cl = cl;
		try {
			recordMarshall(new BigDecimalMarshall(lf));
			recordMarshall(new BigIntegerMarshall(lf));
			recordMarshall(new BooleanMarshall(lf));
			recordMarshall(new ByteArrayMarshall(lf));
			recordMarshall(new ByteMarshall(lf));
			recordMarshall(new DoubleMarshall(lf));
			recordMarshall(new FloatMarshall(lf));
			recordMarshall(new IntegerMarshall(lf));
			recordMarshall(new LongMarshall(lf));
			recordMarshall(new ShortMarshall(lf));
			recordMarshall(new CharacterMarshall(lf));
			recordMarshall(new DateMarshall(lf));
			recordMarshall(new LocaleMarshall(lf));
			recordMarshall(new PatternMarshall(lf));
			recordMarshall(new QNameMarshall(lf));
			recordMarshall(new GregorianCalendarMarshall(lf));
			recordMarshall(new SqlDateMarshall(lf));
			recordMarshall(new SqlTimeMarshall(lf));
			recordMarshall(new SqlTimestampMarshall(lf));
			recordMarshall(new ClassMarshall(lf, cl));
			DocumentFragmentMarshall dfm = new DocumentFragmentMarshall(lf);
			recordMarshall(dfm.getJavaClassName(), dfm);
			recordMarshall(DocumentFragment.class, dfm);
			DurationMarshall dm = new DurationMarshall(lf);
			recordMarshall(dm.getJavaClassName(), dm);
			recordMarshall(Duration.class, dm);
			XMLGregorianCalendarMarshall xgcm;
			xgcm = new XMLGregorianCalendarMarshall(lf);
			recordMarshall(xgcm.getJavaClassName(), xgcm);
			recordMarshall(XMLGregorianCalendar.class, xgcm);
			recordMarshall(new StringMarshall(lf));
			recordMarshall(new StringMarshall(lf, "org.codehaus.groovy.runtime.GStringImpl"));
			recordMarshall(new StringMarshall(lf, "groovy.lang.GString$1"));
			recordMarshall(new StringMarshall(lf, "groovy.lang.GString$2"));
			recordMarshall(new LangStringMarshall<LangString>(lf, LangString.class));
			recordMarshall(new LangStringMarshall<CharSequence>(lf, CharSequence.class));
			loadDatatypes(LiteralManager.class.getClassLoader(), DATATYPES_PROPERTIES);
			loadDatatypes(cl, DATATYPES_PROPERTIES);
			loadDatatypes(cl, LITERALS_PROPERTIES);
		} catch (Exception e) {
			throw new ObjectConversionException(e);
		}
	}

	public Class<?> findClass(URI datatype) {
		if (javaClasses.containsKey(datatype))
			return javaClasses.get(datatype);
		try {
			if (datatype.getNamespace().equals(JAVA_NS)) {
				synchronized (cl) {
					return Class.forName(datatype.getLocalName(), true, cl);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new ObjectConversionException(e);
		}
		return null;
	}

	public boolean isRecordedeType(URI datatype) {
		return findClass(datatype) != null;
	}

	public URI findDatatype(Class<?> type) {
		if (type.equals(String.class))
			return null;
		if (rdfTypes.containsKey(type))
			return rdfTypes.get(type);
		URI datatype = uf.createURI(JAVA_NS, type.getName());
		recordType(type, datatype);
		return datatype;
	}

	@SuppressWarnings("unchecked")
	public Literal createLiteral(Object object) {
		Marshall marshall = findMarshall(object.getClass());
		return marshall.serialize(object);
	}

	public Literal createLiteral(String value, String language) {
		return lf.createLiteral(value, language);
	}

	public Object createObject(Literal literal) {
		URI datatype = literal.getDatatype();
		if (datatype == null) {
			if (literal.getLanguage() == null) {
				datatype = STRING;
			} else {
				datatype = LANG_STRING;
			}
		}
		Marshall<?> marshall = findMarshall(datatype);
		return marshall.deserialize(literal);
	}

	public void recordMarshall(String javaClassName, Marshall<?> marshall) {
		marshalls.put(javaClassName, marshall);
	}

	public void recordMarshall(Class<?> javaClass, Marshall<?> marshall) {
		recordMarshall(javaClass.getName(), marshall);
	}

	public void addDatatype(Class<?> javaClass, URI datatype) {
		recordType(javaClass, datatype);
	}

	public boolean isDatatype(Class<?> type) {
		return rdfTypes.containsKey(type);
	}

	private void recordType(Class<?> javaClass, URI datatype) {
		if (!javaClasses.containsKey(datatype)) {
			javaClasses.putIfAbsent(datatype, javaClass);
		}
		if (rdfTypes.putIfAbsent(javaClass, datatype) == null) {
			Marshall<?> marshall = findMarshall(javaClass);
			marshall.setDatatype(datatype);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Marshall<T> findMarshall(Class<T> type) {
		String name = type.getName();
		if (marshalls.containsKey(name))
			return (Marshall<T>) marshalls.get(name);
		Marshall<T> marshall;
		try {
			marshall = new ValueOfMarshall<T>(lf, type);
		} catch (NoSuchMethodException e1) {
			try {
				marshall = new ObjectConstructorMarshall<T>(lf, type);
			} catch (NoSuchMethodException e2) {
				if (Serializable.class.isAssignableFrom(type)) {
					marshall = new ObjectSerializationMarshall<T>(lf, type);
				} else {
					throw new ObjectConversionException(e1);
				}
			}
		}
		Marshall<?> o = marshalls.putIfAbsent(name, marshall);
		if (o != null) {
			marshall = (Marshall<T>) o;
		}
		return marshall;
	}

	private Marshall<?> findMarshall(URI datatype) {
		Class<?> type;
		if (javaClasses.containsKey(datatype)) {
			type = javaClasses.get(datatype);
		} else if (datatype.getNamespace().equals(JAVA_NS)) {
			try {
				type = forName(datatype.getLocalName(), true, cl);
			} catch (ClassNotFoundException e) {
				throw new ObjectConversionException(e);
			}
		} else {
			throw new ObjectConversionException("Unknown datatype: " + datatype);
		}
		return findMarshall(type);
	}

	private Class<?> forName(String name, boolean init, ClassLoader cl)
			throws ClassNotFoundException {
		synchronized (cl) {
			return Class.forName(name, init, cl);
		}
	}

	private void loadDatatypes(ClassLoader cl, String properties) throws IOException,
			ClassNotFoundException {
		if (cl == null)
			return;
		Enumeration<URL> resources = cl.getResources(properties);
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			try {
				Properties p = new Properties();
				p.load(url.openStream());
				for (Map.Entry<?, ?> e : p.entrySet()) {
					String className = (String) e.getKey();
					String types = (String) e.getValue();
					Class<?> lc = forName(className, true, cl);
					boolean present = lc.isAnnotationPresent(Iri.class);
					for (String rdf : types.split("\\s+")) {
						if (rdf.length() == 0 && present) {
							rdf = lc.getAnnotation(Iri.class).value();
							recordType(lc, uf.createURI(rdf));
						} else if (rdf.length() == 0) {
							logger.warn("Unkown datatype mapping {}", className);
						} else {
							recordType(lc, uf.createURI(rdf));
						}
					}
				}
			} catch (IOException e) {
				String msg = e.getMessage() + " in: " + url;
				IOException ioe = new IOException(msg);
				ioe.initCause(e);
				throw ioe;
			}
		}
	}

	private void recordMarshall(Marshall<?> marshall) {
		recordMarshall(marshall.getJavaClassName(), marshall);
	}

}
