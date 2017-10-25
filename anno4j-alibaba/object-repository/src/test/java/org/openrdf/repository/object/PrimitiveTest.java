package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class PrimitiveTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(PrimitiveTest.class);
	}

	@Iri("urn:PrimitiveBehaviour")
	public static interface PrimitiveBehaviour {
		public boolean isBoolean();

		public byte getByte();

		public char getChar();

		public double getDouble();

		public float getFloat();

		public int getInt();

		public short getShort();

		public String getString();

		public void setBoolean(boolean value);

		public void setByte(byte value);

		public void setChar(char value);

		public void setDouble(double value);

		public void setFloat(float value);

		public void setInt(int value);

		public void setShort(short value);

		public void setString(String value);
	}

	public static class PrimitiveBehaviourImpl1 implements PrimitiveBehaviour {

		private boolean booleanValue;

		private byte byteValue;

		private char charValue;

		private double doubleValue;

		private float floatValue;

		private int intValue;

		private short shortValue;

		private String stringValue;

		public boolean isBoolean() {
			if (count++ % 2 == 0) {
				return booleanValue;
			}
			return false;
		}

		public byte getByte() {
			if (count++ % 2 == 0) {
				return byteValue;
			}
			return 0;
		}

		public char getChar() {
			if (count++ % 2 == 0) {
				return charValue;
			}
			return 0;
		}

		public double getDouble() {
			if (count++ % 2 == 0) {
				return doubleValue;
			}
			return 0;
		}

		public float getFloat() {
			if (count++ % 2 == 0) {
				return floatValue;
			}
			return 0;
		}

		public int getInt() {
			if (count++ % 2 == 0) {
				return intValue;
			}
			return 0;
		}

		public short getShort() {
			if (count++ % 2 == 0) {
				return shortValue;
			}
			return 0;
		}

		public String getString() {
			if (count++ % 2 == 0) {
				return stringValue;
			}
			return null;
		}

		public void setBoolean(boolean value) {
			booleanValue = value;
		}

		public void setByte(byte value) {
			byteValue = value;
		}

		public void setChar(char value) {
			charValue = value;
		}

		public void setDouble(double value) {
			doubleValue = value;
		}

		public void setFloat(float value) {
			floatValue = value;
		}

		public void setInt(int value) {
			intValue = value;
		}

		public void setShort(short value) {
			shortValue = value;
		}

		public void setString(String value) {
			stringValue = value;
		}

	}

	public static class PrimitiveBehaviourImpl2 implements PrimitiveBehaviour {

		private boolean booleanValue;

		private byte byteValue;

		private char charValue;

		private double doubleValue;

		private float floatValue;

		private int intValue;

		private short shortValue;

		private String stringValue;

		public boolean isBoolean() {
			if (count++ % 2 == 0) {
				return booleanValue;
			}
			return false;
		}

		public byte getByte() {
			if (count++ % 2 == 0) {
				return byteValue;
			}
			return 0;
		}

		public char getChar() {
			if (count++ % 2 == 0) {
				return charValue;
			}
			return 0;
		}

		public double getDouble() {
			if (count++ % 2 == 0) {
				return doubleValue;
			}
			return 0;
		}

		public float getFloat() {
			if (count++ % 2 == 0) {
				return floatValue;
			}
			return 0;
		}

		public int getInt() {
			if (count++ % 2 == 0) {
				return intValue;
			}
			return 0;
		}

		public short getShort() {
			if (count++ % 2 == 0) {
				return shortValue;
			}
			return 0;
		}

		public String getString() {
			if (count++ % 2 == 0) {
				return stringValue;
			}
			return null;
		}

		public void setBoolean(boolean value) {
			booleanValue = value;
		}

		public void setByte(byte value) {
			byteValue = value;
		}

		public void setChar(char value) {
			charValue = value;
		}

		public void setDouble(double value) {
			doubleValue = value;
		}

		public void setFloat(float value) {
			floatValue = value;
		}

		public void setInt(int value) {
			intValue = value;
		}

		public void setShort(short value) {
			shortValue = value;
		}

		public void setString(String value) {
			stringValue = value;
		}

	}

	@Iri("urn:Primitive")
	public static interface PrimitiveConcept {
		@Iri("urn:boolean")
		public boolean isBoolean();

		@Iri("urn:byte")
		public byte getByte();

		@Iri("urn:char")
		public char getChar();

		@Iri("urn:double")
		public double getDouble();

		@Iri("urn:float")
		public float getFloat();

		@Iri("urn:int")
		public int getInt();

		@Iri("urn:short")
		public short getShort();

		@Iri("urn:string")
		public String getString();

		@Iri("urn:string")
		public LangString getLangString();

		@Iri("urn:boolean")
		public void setBoolean(boolean value);

		@Iri("urn:byte")
		public void setByte(byte value);

		@Iri("urn:char")
		public void setChar(char value);

		@Iri("urn:double")
		public void setDouble(double value);

		@Iri("urn:float")
		public void setFloat(float value);

		@Iri("urn:int")
		public void setInt(int value);

		@Iri("urn:short")
		public void setShort(short value);

		@Iri("urn:string")
		public void setString(String value);

		@Iri("urn:string")
		public void setLangString(LangString value);
	}

	@Iri("urn:PrimitiveClass")
	public static class PrimitiveConceptClass {
		@Iri("urn:boolean")
		private boolean bool;

		@Iri("urn:byte")
		private byte b;

		@Iri("urn:char")
		private char chr;

		@Iri("urn:double")
		private double doub;

		@Iri("urn:float")
		private float flo;

		@Iri("urn:int")
		private int in;

		@Iri("urn:short")
		private short shor;

		@Iri("urn:string")
		private String string;

		@Iri("urn:string")
		private LangString langString;

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

		public byte getB() {
			return b;
		}

		public void setB(byte b) {
			this.b = b;
		}

		public char getChr() {
			return chr;
		}

		public void setChr(char chr) {
			this.chr = chr;
		}

		public double getDoub() {
			return doub;
		}

		public void setDoub(double doub) {
			this.doub = doub;
		}

		public float getFlo() {
			return flo;
		}

		public void setFlo(float flo) {
			this.flo = flo;
		}

		public int getIn() {
			return in;
		}

		public void setIn(int in) {
			this.in = in;
		}

		public short getShor() {
			return shor;
		}

		public void setShor(short shor) {
			this.shor = shor;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public LangString getLangString() {
			return langString;
		}

		public void setLangString(LangString string) {
			this.langString = string;
		}
	}

	private static final boolean booleanValue = true;

	private static final byte byteValue = 1;

	private static final char charValue = '1';

	static int count;

	private static final double doubleValue = 1;

	private static final float floatValue = 1;

	private static final int intValue = 1;

	private static final short shortValue = 1;

	private static final String stringValue = "1";

	private PrimitiveConcept concept;

	private PrimitiveConceptClass conceptClass;

	private PrimitiveBehaviour behaviour;

	@Override
	protected void setUp() throws Exception {
		config.addConcept(PrimitiveConcept.class);
		config.addConcept(PrimitiveConceptClass.class);
		config.addConcept(PrimitiveBehaviour.class);
		config.addBehaviour(PrimitiveBehaviourImpl1.class);
		config.addBehaviour(PrimitiveBehaviourImpl2.class);
		super.setUp();
		conceptClass = con.addDesignation(con.getObjectFactory().createObject(), PrimitiveConceptClass.class);
		concept = con.addDesignation(con.getObjectFactory().createObject(), PrimitiveConcept.class);
		behaviour = con.addDesignation(con.getObjectFactory().createObject(), PrimitiveBehaviour.class);
	}

	public void testBoolean() {
		assertEquals(false, conceptClass.isBool());
		conceptClass.setBool(booleanValue);
		assertEquals(booleanValue, conceptClass.isBool());

		assertEquals(false, concept.isBoolean());
		concept.setBoolean(booleanValue);
		assertEquals(booleanValue, concept.isBoolean());

		assertEquals(false, behaviour.isBoolean());
		behaviour.setBoolean(booleanValue);
		assertEquals(booleanValue, behaviour.isBoolean());
	}

	public void testByte() {
		assertEquals(0, conceptClass.getB());
		conceptClass.setB(byteValue);
		assertEquals(byteValue, conceptClass.getB());

		assertEquals(0, concept.getByte());
		concept.setByte(byteValue);
		assertEquals(byteValue, concept.getByte());

		assertEquals(0, behaviour.getByte());
		behaviour.setByte(byteValue);
		assertEquals(byteValue, behaviour.getByte());
	}

	public void testChar() {
		assertEquals(0, conceptClass.getChr());
		conceptClass.setChr(charValue);
		assertEquals(charValue, conceptClass.getChr());

		assertEquals(0, concept.getChar());
		concept.setChar(charValue);
		assertEquals(charValue, concept.getChar());

		assertEquals(0, behaviour.getChar());
		behaviour.setChar(charValue);
		assertEquals(charValue, behaviour.getChar());
	}

	public void testDouble() {
		assertEquals(0.0, conceptClass.getDoub());
		conceptClass.setDoub(doubleValue);
		assertEquals(doubleValue, conceptClass.getDoub());

		assertEquals(0.0, concept.getDouble());
		concept.setDouble(doubleValue);
		assertEquals(doubleValue, concept.getDouble());

		assertEquals(0.0, behaviour.getDouble());
		behaviour.setDouble(doubleValue);
		assertEquals(doubleValue, behaviour.getDouble());
	}

	public void testFloat() {
		assertEquals(0.0f, conceptClass.getFlo());
		conceptClass.setFlo(floatValue);
		assertEquals(floatValue, conceptClass.getFlo());

		assertEquals(0.0f, concept.getFloat());
		concept.setFloat(floatValue);
		assertEquals(floatValue, concept.getFloat());

		assertEquals(0.0f, behaviour.getFloat());
		behaviour.setFloat(floatValue);
		assertEquals(floatValue, behaviour.getFloat());
	}

	public void testInt() {
		assertEquals(0, conceptClass.getIn());
		conceptClass.setIn(intValue);
		assertEquals(intValue, conceptClass.getIn());

		assertEquals(0, concept.getInt());
		concept.setInt(intValue);
		assertEquals(intValue, concept.getInt());

		assertEquals(0, behaviour.getInt());
		behaviour.setInt(intValue);
		assertEquals(intValue, behaviour.getInt());
	}

	public void testShort() {
		assertEquals(0, conceptClass.getShor());
		conceptClass.setShor(shortValue);
		assertEquals(shortValue, conceptClass.getShor());

		assertEquals(0, concept.getShort());
		concept.setShort(shortValue);
		assertEquals(shortValue, concept.getShort());

		assertEquals(0, behaviour.getShort());
		behaviour.setShort(shortValue);
		assertEquals(shortValue, behaviour.getShort());
	}

	public void testString() {
		assertEquals(null, conceptClass.getString());
		conceptClass.setString(stringValue);
		assertEquals(stringValue, conceptClass.getString());

		assertEquals(null, concept.getString());
		concept.setString(stringValue);
		assertEquals(stringValue, concept.getString());

		assertEquals(null, behaviour.getString());
		behaviour.setString(stringValue);
		assertEquals(stringValue, behaviour.getString());
	}

	public void testLangString() {
		assertEquals(null, conceptClass.getLangString());
		conceptClass.setLangString(new LangString(stringValue));
		assertEquals(new LangString(stringValue), conceptClass.getLangString());

		assertEquals(null, concept.getLangString());
		concept.setLangString(new LangString(stringValue));
		assertEquals(new LangString(stringValue), concept.getLangString());
	}

	public void testLangStringAsString() {
		conceptClass.setLangString(new LangString(stringValue));
		assertEquals(stringValue, conceptClass.getString());

		concept.setLangString(new LangString(stringValue));
		assertEquals(stringValue, concept.getString());
	}
}
