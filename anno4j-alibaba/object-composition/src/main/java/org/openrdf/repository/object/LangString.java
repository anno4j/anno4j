/*
 * Copyright (c) 2012, 3 Round Stones Inc. Some rights reserved.
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
package org.openrdf.repository.object;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a string and language tag, and thus represents a plain RDF literal
 * with a language tag.
 * 
 * This class includes a number of similar methods to {@link java.lang.String},
 * that preserve the language tag. In addition it includes a method to compare
 * language tags {@link #matchesLang(String)}.
 * 
 * @author James Leigh
 * 
 */
public class LangString implements CharSequence, Serializable,
		Comparable<LangString> {
	private static final long serialVersionUID = 8175463447271413979L;

	/**
	 * Constructs a LangString using the default {@link Locale} for the
	 * language.
	 * 
	 * @param label
	 * @return a LangString with the given label and a default language
	 */
	public static LangString valueOf(String label) {
		return new LangString(label);
	}

	/**
	 * Constructs a LangString using the given label and language.
	 * 
	 * @param label
	 * @param language
	 * @return a LangString with the given label and language
	 */
	public static LangString valueOf(String label, String language) {
		return new LangString(label, language);
	}

	private static String toLang(Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		boolean l = language.length() != 0;
		boolean c = country.length() != 0;
		boolean v = variant.length() != 0;
		StringBuilder result = new StringBuilder(language);
		if (c || (l && v)) {
			result.append('-').append(country.toLowerCase());
		}
		if (v && (l || c)) {
			result.append('-').append(variant);
		}
		return result.toString();
	}

	private final String label;
	private final String lang;
	private Locale locale;

	/**
	 * Constructs a LangString using the default {@link Locale} for the
	 * language.
	 * 
	 * @param label
	 */
	public LangString(String label) {
		this(label, Locale.getDefault());
	}

	/**
	 * Constructs a LangString using the given label and language.
	 * 
	 * @param label
	 * @param lang
	 */
	public LangString(String label, String lang) {
		assert label != null;
		if (lang != null && lang.length() < 1)
			throw new IllegalArgumentException("language cannot be the empty string");
		this.label = label;
		this.lang = lang == null ? toLang(Locale.getDefault()) : lang;
	}

	/**
	 * Constructs a LangString using the given label and locale.
	 * 
	 * @param label
	 * @param locale
	 */
	public LangString(String label, Locale locale) {
		this(label, toLang(locale));
		this.locale = locale;
	}

	public String getLang() {
		return lang;
	}

	/**
	 * The {@link String} portion of this object
	 * 
	 * @return this string without a language
	 */
	@Override
	public String toString() {
		return label;
	}

	/**
	 * The language of the current LangString as a Locale.
	 * 
	 * @return this language as a Locale
	 */
	public synchronized Locale getLocale() {
		if (locale == null) {
			String[] split = getLang().split("-", 3);
			if (split.length == 1) {
				locale = new Locale(getLang());
			} else if (split.length == 2) {
				locale = new Locale(split[0], split[1]);
			} else {
				locale = new Locale(split[0], split[1], split[2]);
			}
		}
		return locale;
	}

	/**
	 * Returns a hash code for this string. The hash code for a
	 * <code>String</code> object is computed as <blockquote>
	 * 
	 * <pre>
	 * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
	 * </pre>
	 * 
	 * </blockquote> using <code>int</code> arithmetic, where <code>s[i]</code>
	 * is the <i>i</i>th character of the string, <code>n</code> is the length
	 * of the string, and <code>^</code> indicates exponentiation. (The hash
	 * value of the empty string is zero.)
	 * 
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Compares this string to the specified object. The result is {@code true}
	 * if and only if the argument is not {@code null} and is a {@code LangString}
	 * object that represents the same sequence of characters as this object.
	 * 
	 * @param o
	 *            The object to compare this {@code LangString} against
	 * 
	 * @return {@code true} if the given object represents a {@code LangString}
	 *         equivalent to this string, {@code false} otherwise
	 * 
	 * @see #compareTo(LangString)
	 * @see #equalsIgnoreCase(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof LangString) {
			LangString other = (LangString) o;
			if (!toString().equals(other.toString()))
				return false;
			if (!getLang().equalsIgnoreCase(other.getLang()))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Compares this {@code LangString} to another {@code LangString}, ignoring case
	 * considerations. Two strings are considered equal ignoring case if they
	 * are of the same length and corresponding characters in the two strings
	 * are equal ignoring case.
	 * 
	 * <p>
	 * Two characters {@code c1} and {@code c2} are considered the same ignoring
	 * case if at least one of the following is true:
	 * <ul>
	 * <li>The two characters are the same (as compared by the {@code ==}
	 * operator)
	 * <li>Applying the method {@link java.lang.Character#toUpperCase(char)} to
	 * each character produces the same result
	 * <li>Applying the method {@link java.lang.Character#toLowerCase(char)} to
	 * each character produces the same result
	 * </ul>
	 * 
	 * @param o
	 *            The {@code LangString} to compare this {@code LangString} against
	 * 
	 * @return {@code true} if the argument is not {@code null} and it
	 *         represents an equivalent {@code LangString} ignoring case;
	 *         {@code false} otherwise
	 * 
	 * @see #equals(Object)
	 */
	public boolean equalsIgnoreCase(Object o) {
		if (this == o)
			return true;
		if (o instanceof LangString) {
			LangString other = (LangString) o;
			if (!toString().equalsIgnoreCase(other.toString()))
				return false;
			if (!getLang().equalsIgnoreCase(other.getLang()))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Compares two strings lexicographically. The comparison is based on the
	 * Unicode value of each character in the strings. The character sequence
	 * represented by this <code>String</code> object is compared
	 * lexicographically to the character sequence represented by the argument
	 * string. The result is a negative integer if this <code>String</code>
	 * object lexicographically precedes the argument string. The result is a
	 * positive integer if this <code>String</code> object lexicographically
	 * follows the argument string. The result is zero if the strings are equal;
	 * <code>compareTo</code> returns <code>0</code> exactly when the
	 * {@link #equals(Object)} method would return <code>true</code>.
	 * <p>
	 * This is the definition of lexicographic ordering. If two strings are
	 * different, then either they have different characters at some index that
	 * is a valid index for both strings, or their lengths are different, or
	 * both. If they have different characters at one or more index positions,
	 * let <i>k</i> be the smallest such index; then the string whose character
	 * at position <i>k</i> has the smaller value, as determined by using the
	 * &lt; operator, lexicographically precedes the other string. In this case,
	 * <code>compareTo</code> returns the difference of the two character values
	 * at position <code>k</code> in the two string -- that is, the value:
	 * <blockquote>
	 * 
	 * <pre>
	 * this.charAt(k) - anotherString.charAt(k)
	 * </pre>
	 * 
	 * </blockquote> If there is no index position at which they differ, then
	 * the shorter string lexicographically precedes the longer string. In this
	 * case, <code>compareTo</code> returns the difference of the lengths of the
	 * strings -- that is, the value: <blockquote>
	 * 
	 * <pre>
	 * this.length() - anotherString.length()
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param o
	 *            the <code>String</code> to be compared.
	 * @return the value <code>0</code> if the argument string is equal to this
	 *         string; a value less than <code>0</code> if this string is
	 *         lexicographically less than the string argument; and a value
	 *         greater than <code>0</code> if this string is lexicographically
	 *         greater than the string argument.
	 */
	public int compareTo(LangString o) {
		if (this == o)
			return 0;
		if (o == null)
			return 1;
		int result = this.getLang().compareToIgnoreCase(o.getLang());
		if (result == 0)
			return this.toString().compareTo(o.toString());
		return result;
	}

	/**
	 * Compares two strings lexicographically, ignoring case differences. This
	 * method returns an integer whose sign is that of calling
	 * <code>compareTo</code> with normalized versions of the strings where case
	 * differences have been eliminated by calling
	 * <code>Character.toLowerCase(Character.toUpperCase(character))</code> on
	 * each character.
	 * <p>
	 * Note that this method does <em>not</em> take locale into account, and
	 * will result in an unsatisfactory ordering for certain locales. The
	 * java.text package provides <em>collators</em> to allow locale-sensitive
	 * ordering.
	 * 
	 * @param o
	 *            the <code>LangString</code> to be compared.
	 * @return a negative integer, zero, or a positive integer as the specified
	 *         String is greater than, equal to, or less than this String,
	 *         ignoring case considerations.
	 * @see java.text.Collator#compare(String, String)
	 */
	public int compareToIgnoreCase(LangString o) {
		if (this == o)
			return 0;
		if (o == null)
			return 1;
		int result = this.getLang().compareToIgnoreCase(o.getLang());
		if (result == 0)
			return this.toString().compareToIgnoreCase(o.toString());
		return result;
	}

	/**
	 * Extended filtering compares extended language ranges to language tags.
	 * Each extended language range in the language priority list is considered
	 * in turn, according to priority. A language range matches a particular
	 * language tag if each respective list of subtags matches.
	 * 
	 * Two subtags match if either they are the same when compared
	 * case-insensitively or the language range's subtag is the wildcard '*'.
	 * 
	 * See http://tools.ietf.org/html/rfc4647
	 * 
	 * @param range
	 *            In a language range, each subtag MUST either be a sequence of
	 *            ASCII alphanumeric characters or the single character '*'
	 *            (%x2A, ASTERISK). The character '*' is a "wildcard" that
	 *            matches any sequence of subtags. The meaning and uses of
	 *            wildcards vary according to the type of language range.
	 * @return true if this has a language tag that matches the extended
	 *         language range given; otherwise, false
	 */
	public boolean matchesLang(String range) {
		// 1. Split both the extended language range and the language tag being
		// compared into a list of subtags by dividing on the hyphen (%x2D)
		// character.
		String[] subtags = getLang().split("-");
		String[] subranges = range.split("-");

		// 2. Begin with the first subtag in each list. If the first subtag in
		// the range does not match the first subtag in the tag, the overall
		// match fails. Otherwise, move to the next subtag in both the
		// range and the tag.
		if (!subtags[0].equalsIgnoreCase(subranges[0]))
			return false;

		// 3. While there are more subtags left in the language range's list:
		int r = 1, t = 1;
		while (r < subranges.length) {

			// A. If the subtag currently being examined in the range is the
			// wildcard ('*'), move to the next subtag in the range and
			// continue with the loop.
			if ("*".equals(subranges[r])) {
				r++;
				continue;
			}

			// B. Else, if there are no more subtags in the language tag's
			// list, the match fails.
			if (t >= subtags.length)
				return false;

			// C. Else, if the current subtag in the range's list matches the
			// current subtag in the language tag's list, move to the next
			// subtag in both lists and continue with the loop.
			if (subranges[r].equalsIgnoreCase(subtags[t])) {
				r++;
				t++;
				continue;
			}

			// D. Else, if the language tag's subtag is a "singleton" (a single
			// letter or digit, which includes the private-use subtag 'x')
			// the match fails.
			if (subtags[t].length() == 1)
				return false;

			// E. Else, move to the next subtag in the language tag's list and
			// continue with the loop.
			t++;
			continue;
		}
		// 4. When the language range's list has no more subtags, the match
		// succeeds.
		return true;
	}

	/**
	 * Concatenates the specified string to the end of this string.
	 * <p>
	 * If the length of the argument string is <code>0</code>, then this
	 * <code>String</code> object is returned. Otherwise, a new
	 * <code>String</code> object is created, representing a character sequence
	 * that is the concatenation of the character sequence represented by this
	 * <code>String</code> object and the character sequence represented by the
	 * argument string.
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * "cares".concat("s") returns "caress"
	 * "to".concat("get").concat("her") returns "together"
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            the <code>String</code> that is concatenated to the end of
	 *            this <code>String</code>.
	 * @return a string that represents the concatenation of this object's
	 *         characters followed by the string argument's characters.
	 * @throws IllegalArgumentException
	 *             if the languages are different
	 */
	public LangString concat(LangString str) {
		String concat = toString().concat(str.toString());
		// check for same lang tag
		String l1 = getLang();
		String l2 = str.getLang();
		if (l1.equalsIgnoreCase(l2))
			return new LangString(concat, l1);
		// check for semantic subset
		if (str.matchesLang(l1))
			return new LangString(concat, l1);
		if (matchesLang(l2))
			return new LangString(concat, l2);
		// use common prefix
		String prefix = l1.length() < l2.length() ? l1 : l2;
		String other = l1.length() < l2.length() ? l2 : l1;
		String common = "";
		int i = prefix.indexOf('-');
		for (; i >= 0; i = prefix.indexOf('-', i + 1)) {
			String substring = prefix.substring(0, i + 1);
			if (!substring.equalsIgnoreCase(other.substring(0, i + 1)))
				break;
			common = substring;
		}
		if (common.length() < 1)
			throw new IllegalArgumentException("Different languages cannot be concatenated: " + l1 + " and " + l2);
		return new LangString(concat, common);
	}

	/**
	 * Concatenates the specified string to the end of this string.
	 * <p>
	 * If the length of the argument string is <code>0</code>, then this
	 * <code>String</code> object is returned. Otherwise, a new
	 * <code>String</code> object is created, representing a character sequence
	 * that is the concatenation of the character sequence represented by this
	 * <code>String</code> object and the character sequence represented by the
	 * argument string.
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * "cares".concat("s") returns "caress"
	 * "to".concat("get").concat("her") returns "together"
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param str
	 *            the <code>String</code> that is concatenated to the end of
	 *            this <code>String</code>.
	 * @return a string that represents the concatenation of this object's
	 *         characters followed by the string argument's characters.
	 */
	public LangString concat(String str) {
		return new LangString(toString().concat(str), getLang());
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 * 
	 * <p>
	 * An invocation of this method of the form
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * str.subSequence(begin, end)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * behaves in exactly the same way as the invocation
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * str.substring(begin, end)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * This method is defined so that the <tt>String</tt> class can implement
	 * the {@link CharSequence} interface.
	 * </p>
	 * 
	 * @param start
	 *            the begin index, inclusive.
	 * @param end
	 *            the end index, exclusive.
	 * @return the specified subsequence.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if <tt>beginIndex</tt> or <tt>endIndex</tt> are negative, if
	 *             <tt>endIndex</tt> is greater than <tt>length()</tt>, or if
	 *             <tt>beginIndex</tt> is greater than <tt>startIndex</tt>
	 */
	public LangString subSequence(int start, int end) {
		return new LangString(toString().substring(start, end), getLang());
	}

	/**
	 * Returns a new string that is a substring of this string. The substring
	 * begins with the character at the specified index and extends to the end
	 * of this string.
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * "unhappy".substring(2) returns "happy"
	 * "Harbison".substring(3) returns "bison"
	 * "emptiness".substring(9) returns "" (an empty string)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param beginIndex
	 *            the beginning index, inclusive.
	 * @return the specified substring.
	 * @exception IndexOutOfBoundsException
	 *                if <code>beginIndex</code> is negative or larger than the
	 *                length of this <code>String</code> object.
	 */
	public LangString substring(int beginIndex) {
		return new LangString(toString().substring(beginIndex), getLang());
	}

	/**
	 * Returns a new string that is a substring of this string. The substring
	 * begins at the specified <code>beginIndex</code> and extends to the
	 * character at index <code>endIndex - 1</code>. Thus the length of the
	 * substring is <code>endIndex-beginIndex</code>.
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * "hamburger".substring(4, 8) returns "urge"
	 * "smiles".substring(1, 5) returns "mile"
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param beginIndex
	 *            the beginning index, inclusive.
	 * @param endIndex
	 *            the ending index, exclusive.
	 * @return the specified substring.
	 * @exception IndexOutOfBoundsException
	 *                if the <code>beginIndex</code> is negative, or
	 *                <code>endIndex</code> is larger than the length of this
	 *                <code>String</code> object, or <code>beginIndex</code> is
	 *                larger than <code>endIndex</code>.
	 */
	public LangString substring(int beginIndex, int endIndex) {
		return new LangString(toString().substring(beginIndex, endIndex),
				getLang());
	}

	/**
	 * Returns a new string resulting from replacing all occurrences of
	 * <code>oldChar</code> in this string with <code>newChar</code>.
	 * <p>
	 * If the character <code>oldChar</code> does not occur in the character
	 * sequence represented by this <code>String</code> object, then a reference
	 * to this <code>String</code> object is returned. Otherwise, a new
	 * <code>String</code> object is created that represents a character
	 * sequence identical to the character sequence represented by this
	 * <code>String</code> object, except that every occurrence of
	 * <code>oldChar</code> is replaced by an occurrence of <code>newChar</code>.
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * "mesquite in your cellar".replace('e', 'o')
	 *         returns "mosquito in your collar"
	 * "the war of baronets".replace('r', 'y')
	 *         returns "the way of bayonets"
	 * "sparring with a purple porpoise".replace('p', 't')
	 *         returns "starring with a turtle tortoise"
	 * "JonL".replace('q', 'x') returns "JonL" (no change)
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param oldChar
	 *            the old character.
	 * @param newChar
	 *            the new character.
	 * @return a string derived from this string by replacing every occurrence
	 *         of <code>oldChar</code> with <code>newChar</code>.
	 */
	public LangString replace(char oldChar, char newChar) {
		return new LangString(toString().replace(oldChar, newChar), getLang());
	}

	/**
	 * Replaces the first substring of this string that matches the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a> with the
	 * given replacement.
	 * 
	 * <p>
	 * An invocation of this method of the form <i>str</i>
	 * <tt>.replaceFirst(</tt><i>regex</i><tt>,</tt> <i>repl</i><tt>)</tt>
	 * yields exactly the same result as the expression
	 * 
	 * <blockquote><tt>
	 * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#compile
	 * compile}(</tt><i>regex</i><tt>).{@link
	 * java.util.regex.Pattern#matcher(java.lang.CharSequence)
	 * matcher}(</tt><i>str</i><tt>).{@link java.util.regex.Matcher#replaceFirst
	 * replaceFirst}(</tt><i>repl</i><tt>)</tt></blockquote>
	 * 
	 * <p>
	 * Note that backslashes (<tt>\</tt>) and dollar signs (<tt>$</tt>) in the
	 * replacement string may cause the results to be different than if it were
	 * being treated as a literal replacement string; see
	 * {@link java.util.regex.Matcher#replaceFirst}. Use
	 * {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
	 * meaning of these characters, if desired.
	 * 
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @param replacement
	 *            the string to be substituted for the first match
	 * 
	 * @return The resulting <tt>String</tt>
	 * 
	 * @throws PatternSyntaxException
	 *             if the regular expression's syntax is invalid
	 * 
	 * @see java.util.regex.Pattern
	 */
	public LangString replaceFirst(String regex, String replacement) {
		return new LangString(toString().replaceFirst(regex, replacement),
				getLang());
	}

	/**
	 * Replaces each substring of this string that matches the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a> with the
	 * given replacement.
	 * 
	 * <p>
	 * An invocation of this method of the form <i>str</i><tt>.replaceAll(</tt>
	 * <i>regex</i><tt>,</tt> <i>repl</i><tt>)</tt> yields exactly the same
	 * result as the expression
	 * 
	 * <blockquote><tt>
	 * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#compile
	 * compile}(</tt><i>regex</i><tt>).{@link
	 * java.util.regex.Pattern#matcher(java.lang.CharSequence)
	 * matcher}(</tt><i>str</i><tt>).{@link java.util.regex.Matcher#replaceAll
	 * replaceAll}(</tt><i>repl</i><tt>)</tt></blockquote>
	 * 
	 * <p>
	 * Note that backslashes (<tt>\</tt>) and dollar signs (<tt>$</tt>) in the
	 * replacement string may cause the results to be different than if it were
	 * being treated as a literal replacement string; see
	 * {@link java.util.regex.Matcher#replaceAll Matcher.replaceAll}. Use
	 * {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
	 * meaning of these characters, if desired.
	 * 
	 * @param regex
	 *            the regular expression to which this string is to be matched
	 * @param replacement
	 *            the string to be substituted for each match
	 * 
	 * @return The resulting <tt>String</tt>
	 * 
	 * @throws PatternSyntaxException
	 *             if the regular expression's syntax is invalid
	 * 
	 * @see java.util.regex.Pattern
	 */
	public LangString replaceAll(String regex, String replacement) {
		return new LangString(toString().replaceAll(regex, replacement),
				getLang());
	}

	/**
	 * Replaces each substring of this string that matches the literal target
	 * sequence with the specified literal replacement sequence. The replacement
	 * proceeds from the beginning of the string to the end, for example,
	 * replacing "aa" with "b" in the string "aaa" will result in "ba" rather
	 * than "ab".
	 * 
	 * @param target
	 *            The sequence of char values to be replaced
	 * @param replacement
	 *            The replacement sequence of char values
	 * @return The resulting string
	 * @throws NullPointerException
	 *             if <code>target</code> or <code>replacement</code> is
	 *             <code>null</code>.
	 */
	public LangString replace(CharSequence target, CharSequence replacement) {
		return new LangString(toString().replace(target, replacement),
				getLang());
	}

	/**
	 * Splits this string around matches of the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a>.
	 * 
	 * <p>
	 * The array returned by this method contains each substring of this string
	 * that is terminated by another substring that matches the given expression
	 * or is terminated by the end of the string. The substrings in the array
	 * are in the order in which they occur in this string. If the expression
	 * does not match any part of the input then the resulting array has just
	 * one element, namely this string.
	 * 
	 * <p>
	 * The <tt>limit</tt> parameter controls the number of times the pattern is
	 * applied and therefore affects the length of the resulting array. If the
	 * limit <i>n</i> is greater than zero then the pattern will be applied at
	 * most <i>n</i>&nbsp;-&nbsp;1 times, the array's length will be no greater
	 * than <i>n</i>, and the array's last entry will contain all input beyond
	 * the last matched delimiter. If <i>n</i> is non-positive then the pattern
	 * will be applied as many times as possible and the array can have any
	 * length. If <i>n</i> is zero then the pattern will be applied as many
	 * times as possible, the array can have any length, and trailing empty
	 * strings will be discarded.
	 * 
	 * <p>
	 * The string <tt>"boo:and:foo"</tt>, for example, yields the following
	 * results with these parameters:
	 * 
	 * <blockquote>
	 * <table cellpadding=1 cellspacing=0 summary="Split example showing regex, limit, and result">
	 * <tr>
	 * <th>Regex</th>
	 * <th>Limit</th>
	 * <th>Result</th>
	 * </tr>
	 * <tr>
	 * <td align=center>:</td>
	 * <td align=center>2</td>
	 * <td><tt>{ "boo", "and:foo" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>:</td>
	 * <td align=center>5</td>
	 * <td><tt>{ "boo", "and", "foo" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>:</td>
	 * <td align=center>-2</td>
	 * <td><tt>{ "boo", "and", "foo" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>o</td>
	 * <td align=center>5</td>
	 * <td><tt>{ "b", "", ":and:f", "", "" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>o</td>
	 * <td align=center>-2</td>
	 * <td><tt>{ "b", "", ":and:f", "", "" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>o</td>
	 * <td align=center>0</td>
	 * <td><tt>{ "b", "", ":and:f" }</tt></td>
	 * </tr>
	 * </table>
	 * </blockquote>
	 * 
	 * <p>
	 * An invocation of this method of the form <i>str.</i><tt>split(</tt>
	 * <i>regex</i><tt>,</tt>&nbsp;<i>n</i><tt>)</tt> yields the same result as
	 * the expression
	 * 
	 * <blockquote> {@link java.util.regex.Pattern}.
	 * {@link java.util.regex.Pattern#compile compile}<tt>(</tt><i>regex</i>
	 * <tt>)</tt>.
	 * {@link java.util.regex.Pattern#split(java.lang.CharSequence,int) split}
	 * <tt>(</tt><i>str</i><tt>,</tt>&nbsp;<i>n</i><tt>)</tt> </blockquote>
	 * 
	 * 
	 * @param regex
	 *            the delimiting regular expression
	 * 
	 * @param limit
	 *            the result threshold, as described above
	 * 
	 * @return the array of strings computed by splitting this string around
	 *         matches of the given regular expression
	 * 
	 * @throws PatternSyntaxException
	 *             if the regular expression's syntax is invalid
	 * 
	 * @see java.util.regex.Pattern
	 */
	public LangString[] split(String regex, int limit) {
		String[] split = toString().split(regex, limit);
		LangString[] result = new LangString[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = new LangString(split[i], getLang());
		}
		return result;
	}

	/**
	 * Splits this string around matches of the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a>.
	 * 
	 * <p>
	 * This method works as if by invoking the two-argument
	 * {@link #split(String, int) split} method with the given expression and a
	 * limit argument of zero. Trailing empty strings are therefore not included
	 * in the resulting array.
	 * 
	 * <p>
	 * The string <tt>"boo:and:foo"</tt>, for example, yields the following
	 * results with these expressions:
	 * 
	 * <blockquote>
	 * <table cellpadding=1 cellspacing=0 summary="Split examples showing regex and result">
	 * <tr>
	 * <th>Regex</th>
	 * <th>Result</th>
	 * </tr>
	 * <tr>
	 * <td align=center>:</td>
	 * <td><tt>{ "boo", "and", "foo" }</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>o</td>
	 * <td><tt>{ "b", "", ":and:f" }</tt></td>
	 * </tr>
	 * </table>
	 * </blockquote>
	 * 
	 * 
	 * @param regex
	 *            the delimiting regular expression
	 * 
	 * @return the array of strings computed by splitting this string around
	 *         matches of the given regular expression
	 * 
	 * @throws PatternSyntaxException
	 *             if the regular expression's syntax is invalid
	 * 
	 * @see java.util.regex.Pattern
	 */
	public LangString[] split(String regex) {
		return split(regex, 0);
	}

	/**
	 * Converts all of the characters in this <code>String</code> to lower case
	 * using the rules of the given <code>Locale</code>. Case mapping is based
	 * on the Unicode Standard version specified by the
	 * {@link java.lang.Character Character} class. Since case mappings are not
	 * always 1:1 char mappings, the resulting <code>String</code> may be a
	 * different length than the original <code>String</code>.
	 * <p>
	 * Examples of lowercase mappings are in the following table:
	 * <table border="1" summary="Lowercase mapping examples showing language code of locale, upper case, lower case, and description">
	 * <tr>
	 * <th>Language Code of Locale</th>
	 * <th>Upper Case</th>
	 * <th>Lower Case</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0130</td>
	 * <td>&#92;u0069</td>
	 * <td>capital letter I with dot above -&gt; small letter i</td>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0049</td>
	 * <td>&#92;u0131</td>
	 * <td>capital letter I -&gt; small letter dotless i</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>French Fries</td>
	 * <td>french fries</td>
	 * <td>lowercased all chars in String</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td><img src="doc-files/capiota.gif" alt="capiota"><img
	 * src="doc-files/capchi.gif" alt="capchi"> <img
	 * src="doc-files/captheta.gif" alt="captheta"><img
	 * src="doc-files/capupsil.gif" alt="capupsil"> <img
	 * src="doc-files/capsigma.gif" alt="capsigma"></td>
	 * <td><img src="doc-files/iota.gif" alt="iota"><img src="doc-files/chi.gif"
	 * alt="chi"> <img src="doc-files/theta.gif" alt="theta"><img
	 * src="doc-files/upsilon.gif" alt="upsilon"> <img
	 * src="doc-files/sigma1.gif" alt="sigma"></td>
	 * <td>lowercased all chars in String</td>
	 * </tr>
	 * </table>
	 * 
	 * @return the <code>String</code>, converted to lowercase.
	 * @see java.lang.String#toLowerCase()
	 * @see java.lang.String#toUpperCase()
	 * @see java.lang.String#toUpperCase(Locale)
	 */
	public LangString toLowerCase() {
		return new LangString(toString().toLowerCase(getLocale()), getLang());
	}

	/**
	 * Converts all of the characters in this <code>String</code> to upper case
	 * using the rules of the given <code>Locale</code>. Case mapping is based
	 * on the Unicode Standard version specified by the
	 * {@link java.lang.Character Character} class. Since case mappings are not
	 * always 1:1 char mappings, the resulting <code>String</code> may be a
	 * different length than the original <code>String</code>.
	 * <p>
	 * Examples of locale-sensitive and 1:M case mappings are in the following
	 * table.
	 * <p>
	 * <table border="1" summary="Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.">
	 * <tr>
	 * <th>Language Code of Locale</th>
	 * <th>Lower Case</th>
	 * <th>Upper Case</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0069</td>
	 * <td>&#92;u0130</td>
	 * <td>small letter i -&gt; capital letter I with dot above</td>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0131</td>
	 * <td>&#92;u0049</td>
	 * <td>small letter dotless i -&gt; capital letter I</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>&#92;u00df</td>
	 * <td>&#92;u0053 &#92;u0053</td>
	 * <td>small letter sharp s -&gt; two letters: SS</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>Fahrvergn&uuml;gen</td>
	 * <td>FAHRVERGN&Uuml;GEN</td>
	 * <td></td>
	 * </tr>
	 * </table>
	 * 
	 * @return the <code>String</code>, converted to uppercase.
	 * @see java.lang.String#toUpperCase()
	 * @see java.lang.String#toLowerCase()
	 * @see java.lang.String#toLowerCase(Locale)
	 */
	public LangString toUpperCase() {
		return new LangString(toString().toUpperCase(getLocale()), getLang());
	}

	/**
	 * Returns a copy of the string, with leading and trailing whitespace
	 * omitted.
	 * <p>
	 * If this <code>String</code> object represents an empty character
	 * sequence, or the first and last characters of character sequence
	 * represented by this <code>String</code> object both have codes greater
	 * than <code>'&#92;u0020'</code> (the space character), then a reference to
	 * this <code>String</code> object is returned.
	 * <p>
	 * Otherwise, if there is no character with a code greater than
	 * <code>'&#92;u0020'</code> in the string, then a new <code>String</code>
	 * object representing an empty string is created and returned.
	 * <p>
	 * Otherwise, let <i>k</i> be the index of the first character in the string
	 * whose code is greater than <code>'&#92;u0020'</code>, and let <i>m</i> be
	 * the index of the last character in the string whose code is greater than
	 * <code>'&#92;u0020'</code>. A new <code>String</code> object is created,
	 * representing the substring of this string that begins with the character
	 * at index <i>k</i> and ends with the character at index <i>m</i>-that is,
	 * the result of <code>this.substring(<i>k</i>,&nbsp;<i>m</i>+1)</code>.
	 * <p>
	 * This method may be used to trim whitespace (as defined above) from the
	 * beginning and end of a string.
	 * 
	 * @return A copy of this string with leading and trailing white space
	 *         removed, or this string if it has no leading or trailing white
	 *         space.
	 */
	public LangString trim() {
		return new LangString(toString().trim(), getLang());
	}

	//////////////////////////////// Delegate toString() ////////////////////////////////

    /**
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return  the length of the sequence of characters represented by this
     *          object.
     */
	public int length() {
		return toString().length();
	}

    /**
     * Returns the <code>char</code> value at the
     * specified index. An index ranges from <code>0</code> to
     * <code>length() - 1</code>. The first <code>char</code> value of the sequence
     * is at index <code>0</code>, the next at index <code>1</code>,
     * and so on, as for array indexing.
     *
     * <p>If the <code>char</code> value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the <code>char</code> value.
     * @return     the <code>char</code> value at the specified index of this string.
     *             The first <code>char</code> value is at index <code>0</code>.
     * @exception  IndexOutOfBoundsException  if the <code>index</code>
     *             argument is negative or not less than the length of this
     *             string.
     */
	public char charAt(int index) {
		return toString().charAt(index);
	}

	/**
     * Returns <tt>true</tt> if, and only if, {@link #length()} is <tt>0</tt>.
     *
     * @return <tt>true</tt> if {@link #length()} is <tt>0</tt>, otherwise
     * <tt>false</tt>
     */
	public final boolean isEmpty() {
		return toString().isEmpty();
	}

    /**
     * Compares this string to the specified {@code CharSequence}.  The result
     * is {@code true} if and only if this {@code LangString} represents the same
     * sequence of char values as the specified sequence.
     *
     * @param  cs
     *         The sequence to compare this {@code LangString} against
     *
     * @return  {@code true} if this {@code LangString} represents the same
     *          sequence of char values as the specified sequence, {@code
     *          false} otherwise
     */
	public final boolean contentEquals(CharSequence cs) {
		return toString().contentEquals(cs);
	}

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this <tt>String</tt> object is compared to a substring
     * of the argument other. The result is true if these substrings
     * represent identical character sequences. The substring of this
     * <tt>String</tt> object to be compared begins at index <tt>toffset</tt>
     * and has length <tt>len</tt>. The substring of other to be compared
     * begins at index <tt>ooffset</tt> and has length <tt>len</tt>. The
     * result is <tt>false</tt> if and only if at least one of the following
     * is true:
     * <ul><li><tt>toffset</tt> is negative.
     * <li><tt>ooffset</tt> is negative.
     * <li><tt>toffset+len</tt> is greater than the length of this
     * <tt>String</tt> object.
     * <li><tt>ooffset+len</tt> is greater than the length of the other
     * argument.
     * <li>There is some nonnegative integer <i>k</i> less than <tt>len</tt>
     * such that:
     * <tt>this.charAt(toffset+<i>k</i>)&nbsp;!=&nbsp;other.charAt(ooffset+<i>k</i>)</tt>
     * </ul>
     *
     * @param   toffset   the starting offset of the subregion in this string.
     * @param   other     the string argument.
     * @param   ooffset   the starting offset of the subregion in the string
     *                    argument.
     * @param   len       the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          exactly matches the specified subregion of the string argument;
     *          <code>false</code> otherwise.
     */
	public final boolean regionMatches(int toffset, CharSequence other, int ooffset, int len) {
		return toString().regionMatches(toffset, other.toString(), ooffset, len);
	}

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this <tt>String</tt> object is compared to a substring
     * of the argument <tt>other</tt>. The result is <tt>true</tt> if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if <tt>ignoreCase</tt> is true. The substring of
     * this <tt>String</tt> object to be compared begins at index
     * <tt>toffset</tt> and has length <tt>len</tt>. The substring of
     * <tt>other</tt> to be compared begins at index <tt>ooffset</tt> and
     * has length <tt>len</tt>. The result is <tt>false</tt> if and only if
     * at least one of the following is true:
     * <ul><li><tt>toffset</tt> is negative.
     * <li><tt>ooffset</tt> is negative.
     * <li><tt>toffset+len</tt> is greater than the length of this
     * <tt>String</tt> object.
     * <li><tt>ooffset+len</tt> is greater than the length of the other
     * argument.
     * <li><tt>ignoreCase</tt> is <tt>false</tt> and there is some nonnegative
     * integer <i>k</i> less than <tt>len</tt> such that:
     * <blockquote><pre>
     * this.charAt(toffset+k) != other.charAt(ooffset+k)
     * </pre></blockquote>
     * <li><tt>ignoreCase</tt> is <tt>true</tt> and there is some nonnegative
     * integer <i>k</i> less than <tt>len</tt> such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(toffset+k)) !=
               Character.toLowerCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(toffset+k)) !=
     *         Character.toUpperCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param   ignoreCase   if <code>true</code>, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  <code>true</code> if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          <code>false</code> otherwise. Whether the matching is exact
     *          or case insensitive depends on the <code>ignoreCase</code>
     *          argument.
     */
	public final boolean regionMatches(boolean ignoreCase, int toffset, CharSequence other,
			int ooffset, int len) {
		return toString().regionMatches(ignoreCase, toffset, other.toString(), ooffset, len);
	}

    /**
     * Tests if the substring of this string beginning at the
     * specified index starts with the specified prefix.
     *
     * @param   prefix    the prefix.
     * @param   toffset   where to begin looking in this string.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a prefix of the substring of this object starting
     *          at index <code>toffset</code>; <code>false</code> otherwise.
     *          The result is <code>false</code> if <code>toffset</code> is
     *          negative or greater than the length of this
     *          <code>String</code> object; otherwise the result is the same
     *          as the result of the expression
     *          <pre>
     *          this.substring(toffset).startsWith(prefix)
     *          </pre>
     */
	public final boolean startsWith(CharSequence prefix, int toffset) {
		return toString().startsWith(prefix.toString(), toffset);
	}

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param   prefix   the prefix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a prefix of the character sequence represented by
     *          this string; <code>false</code> otherwise.
     *          Note also that <code>true</code> will be returned if the
     *          argument is an empty string or is equal to this
     *          <code>String</code> object as determined by the
     *          {@link #equals(Object)} method.
     */
	public final boolean startsWith(CharSequence prefix) {
		return toString().startsWith(prefix.toString());
	}

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param   suffix   the suffix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a suffix of the character sequence represented by
     *          this object; <code>false</code> otherwise. Note that the
     *          result will be <code>true</code> if the argument is the
     *          empty string or is equal to this <code>String</code> object
     *          as determined by the {@link #equals(Object)} method.
     */
	public final boolean endsWith(CharSequence suffix) {
		return toString().endsWith(suffix.toString());
	}

    /**
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * <code>ch</code> occurs in the character sequence represented by
     * this <code>String</code> object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of <code>ch</code> in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of <code>ch</code>, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then <code>-1</code> is returned.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     */
	public final int indexOf(int ch) {
		return toString().indexOf(ch);
	}

    /**
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * <code>ch</code> occurs in the character sequence represented by
     * this <code>String</code> object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of <code>ch</code> in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of <code>ch</code>, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then <code>-1</code> is returned.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     */
	public final int indexOf(int ch, int fromIndex) {
		return toString().indexOf(ch, fromIndex);
	}

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character. For values of <code>ch</code> in the
     * range from 0 to 0xFFFF (inclusive), the index (in Unicode code
     * units) returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of <code>ch</code>, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true.  In either case, if no such character occurs in this
     * string, then <code>-1</code> is returned.  The
     * <code>String</code> is searched backwards starting at the last
     * character.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object, or
     *          <code>-1</code> if the character does not occur.
     */
	public final int lastIndexOf(int ch) {
		return toString().lastIndexOf(ch);
	}

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character, searching backward starting at the
     * specified index. For values of <code>ch</code> in the range
     * from 0 to 0xFFFF (inclusive), the index returned is the largest
     * value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) && (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of <code>ch</code>, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) && (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or before position <code>fromIndex</code>, then
     * <code>-1</code> is returned.
     *
     * <p>All indices are specified in <code>char</code> values
     * (Unicode code units).
     *
     * @param   ch          a character (Unicode code point).
     * @param   fromIndex   the index to start the search from. There is no
     *          restriction on the value of <code>fromIndex</code>. If it is
     *          greater than or equal to the length of this string, it has
     *          the same effect as if it were equal to one less than the
     *          length of this string: this entire string may be searched.
     *          If it is negative, it has the same effect as if it were -1:
     *          -1 is returned.
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object that is less
     *          than or equal to <code>fromIndex</code>, or <code>-1</code>
     *          if the character does not occur before that point.
     */
	public final int lastIndexOf(int ch, int fromIndex) {
		return toString().lastIndexOf(ch, fromIndex);
	}

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring. The integer returned is the smallest value
     * <i>k</i> such that:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * is <code>true</code>.
     *
     * @param   str   any string.
     * @return  if the string argument occurs as a substring within this
     *          object, then the index of the first character of the first
     *          such substring is returned; if it does not occur as a
     *          substring, <code>-1</code> is returned.
     */
	public final int indexOf(CharSequence str) {
		return toString().indexOf(str.toString());
	}

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.  The integer
     * returned is the smallest value <tt>k</tt> for which:
     * <blockquote><pre>
     *     k &gt;= Math.min(fromIndex, this.length()) && this.startsWith(str, k)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param   str         the substring for which to search.
     * @param   fromIndex   the index from which to start the search.
     * @return  the index within this string of the first occurrence of the
     *          specified substring, starting at the specified index.
     */
	public final int indexOf(CharSequence str, int fromIndex) {
		return toString().indexOf(str.toString(), fromIndex);
	}

    /**
     * Returns the index within this string of the rightmost occurrence
     * of the specified substring.  The rightmost empty string "" is
     * considered to occur at the index value <code>this.length()</code>.
     * The returned index is the largest value <i>k</i> such that
     * <blockquote><pre>
     * this.startsWith(str, k)
     * </pre></blockquote>
     * is true.
     *
     * @param   str   the substring to search for.
     * @return  if the string argument occurs one or more times as a substring
     *          within this object, then the index of the first character of
     *          the last such substring is returned. If it does not occur as
     *          a substring, <code>-1</code> is returned.
     */
	public final int lastIndexOf(CharSequence str) {
		return toString().lastIndexOf(str.toString());
	}

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     * The integer returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     *     k &lt;= Math.min(fromIndex, this.length()) && this.startsWith(str, k)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then -1 is returned.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index within this string of the last occurrence of the
     *          specified substring.
     */
	public final int lastIndexOf(CharSequence str, int fromIndex) {
		return toString().lastIndexOf(str.toString(), fromIndex);
	}

    /**
     * Tells whether or not this string matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> An invocation of this method of the form
     * <i>str</i><tt>.matches(</tt><i>regex</i><tt>)</tt> yields exactly the
     * same result as the expression
     *
     * <blockquote><tt> {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#matches(String,CharSequence)
     * matches}(</tt><i>regex</i><tt>,</tt> <i>str</i><tt>)</tt></blockquote>
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     *
     * @return  <tt>true</tt> if, and only if, this string matches the
     *          given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     */
	public final boolean matches(String regex) {
		return toString().matches(regex);
	}

    /**
     * Returns true if and only if this string contains the specified
     * sequence of char values.
     *
     * @param s the sequence to search for
     * @return true if this string contains <code>s</code>, false otherwise
     * @throws NullPointerException if <code>s</code> is <code>null</code>
     */
	public final boolean contains(CharSequence s) {
		return toString().contains(s);
	}
}
