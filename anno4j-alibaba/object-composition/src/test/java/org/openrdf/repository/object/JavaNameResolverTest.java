package org.openrdf.repository.object;

import junit.framework.TestCase;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.compiler.JavaNameResolver;

public class JavaNameResolverTest extends TestCase {

	public void testPackageName() throws Exception {
		assertEquals("fred", packageName("fred"));
		assertEquals("_123abc", packageName("123abc"));
		assertEquals("f._3red", packageName("f.3red"));
		assertEquals("f._red", packageName("f._red"));
	}

	/**
	 * Test for http://www.openrdf.org/issues/browse/ALI-18
	 */
	public void testInvalidWindowsPackageNames() throws Exception {

		// Remember that old COPY CON FILE
		// .. well, those device names are still invalid on Windows
		// http://msdn.microsoft.com/en-us/library/aa561308.aspx
		//
		// Thus our JAR would not be unzippable on Windows

		assertEquals("_con", packageName("con"));
		assertEquals("_lpt1", packageName("lpt1"));



		// But not replaced if not surrounded by .
		assertEquals("conx", packageName("conx"));
		assertEquals("xcon", packageName("xcon"));

		// Case insensitive
		assertEquals("_CoM2", packageName("CoM2"));

		// Also if there's a prefix
		assertEquals("fred._con", packageName("fred.con"));
		assertEquals("fred._lpt1", packageName("fred.lpt1"));

		// or anywhere earlier
		assertEquals("_con.fred", packageName("con.fred"));
		assertEquals("_lpt1.soup", packageName("lpt1.soup"));
		assertEquals("fred._lpt1.soup", packageName("fred.lpt1.soup"));

		// This is silly, I know, but the $ could screw up the regex
		assertEquals("a._clock$.b", packageName("a.clock$.b"));
	}

	private String packageName(String packageName) {
		JavaNameResolver resolver = new JavaNameResolver();
		resolver.bindPackageToNamespace(packageName, "http://example.com/");
		return resolver.getPackageName(new URIImpl("http://example.com/MyClass"));
	}
}
