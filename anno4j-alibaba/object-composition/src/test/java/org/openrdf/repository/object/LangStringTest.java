package org.openrdf.repository.object;

import junit.framework.TestCase;

public class LangStringTest extends TestCase {

	public void testRange() throws Exception {
		String range = "de-*-DE";
		assertTrue(new LangString("", "de-DE").matchesLang(range));
		assertTrue(new LangString("", "de-de").matchesLang(range));
		assertTrue(new LangString("", "de-Latn-DE").matchesLang(range));
		assertTrue(new LangString("", "de-Latf-DE").matchesLang(range));
		assertTrue(new LangString("", "de-DE-x-goethe").matchesLang(range));
		assertTrue(new LangString("", "de-Latn-DE-1996").matchesLang(range));
		assertTrue(new LangString("", "de-Deva-DE").matchesLang(range));
		assertFalse(new LangString("", "de").matchesLang(range));
		assertFalse(new LangString("", "de-x-DE").matchesLang(range));
		assertFalse(new LangString("", "de-Deva").matchesLang(range));
	}

	public void testSynonym() throws Exception {
		String range = "de-DE";
		assertTrue(new LangString("", "de-DE").matchesLang(range));
		assertTrue(new LangString("", "de-de").matchesLang(range));
		assertTrue(new LangString("", "de-Latn-DE").matchesLang(range));
		assertTrue(new LangString("", "de-Latf-DE").matchesLang(range));
		assertTrue(new LangString("", "de-DE-x-goethe").matchesLang(range));
		assertTrue(new LangString("", "de-Latn-DE-1996").matchesLang(range));
		assertTrue(new LangString("", "de-Deva-DE").matchesLang(range));
		assertFalse(new LangString("", "de").matchesLang(range));
		assertFalse(new LangString("", "de-x-DE").matchesLang(range));
		assertFalse(new LangString("", "de-Deva").matchesLang(range));
	}

	public void testEquals() throws Exception {
		assertTrue(new LangString("", "de-DE").equals(new LangString("", "de-de")));
	}

	public void testConcat() throws Exception {
		LangString car = new LangString("car", "en-ca");
		LangString s = new LangString("s", "en");
		LangString cars = car.concat(s);
		assertEquals("cars", cars.toString());
		assertEquals("en", cars.getLang());
	}
}
