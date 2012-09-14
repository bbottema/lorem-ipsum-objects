package org.dummycreator.helperutils;

public class EnumClass {

	public enum internalEnum {

		priv1, priv2, priv3
	}

	private char _char;
	private boolean _boolean;
	private long _long;
	private double _double;
	private double _secondDouble;
	private String _string;
	private short _short;
	private float _float;
	private byte _byte;
	private int _int;
	private EnumTester enumTester;
	private internalEnum internalEnum;

	/**
	 * Get the value of _int
	 * 
	 * @return the value of _int
	 */
	public int get_int() {
		return _int;
	}

	/**
	 * Set the value of _int
	 * 
	 * @param _int new value of _int
	 */
	public void set_int(final int _int) {
		this._int = _int;
	}

	/**
	 * Get the value of _string
	 * 
	 * @return the value of _string
	 */
	public String get_string() {
		return _string;
	}

	/**
	 * Set the value of _string
	 * 
	 * @param _string new value of _string
	 */
	public void set_string(final String _string) {
		this._string = _string;
	}

	/**
	 * Get the value of _float
	 * 
	 * @return the value of _float
	 */
	public float get_float() {
		return _float;
	}

	/**
	 * Set the value of _float
	 * 
	 * @param _float new value of _float
	 */
	public void set_float(final float _float) {
		this._float = _float;
	}

	/**
	 * Get the value of _short
	 * 
	 * @return the value of _short
	 */
	public short get_short() {
		return _short;
	}

	/**
	 * Set the value of _short
	 * 
	 * @param _short new value of _short
	 */
	public void set_short(final short _short) {
		this._short = _short;
	}

	/**
	 * Get the value of _double
	 * 
	 * @return the value of _double
	 */
	public double get_double() {
		return _double;
	}

	/**
	 * Set the value of _double
	 * 
	 * @param _double new value of _double
	 */
	public void set_double(final double _double) {
		this._double = _double;
	}

	/**
	 * Get the value of _byte
	 * 
	 * @return the value of _byte
	 */
	public byte get_byte() {
		return _byte;
	}

	/**
	 * Set the value of _byte
	 * 
	 * @param _byte new value of _byte
	 */
	public void set_byte(final byte _byte) {
		this._byte = _byte;
	}

	/**
	 * Get the value of _long
	 * 
	 * @return the value of _long
	 */
	public long get_long() {
		return _long;
	}

	/**
	 * Set the value of _long
	 * 
	 * @param _long new value of _long
	 */
	public void set_long(final long _long) {
		this._long = _long;
	}

	/**
	 * Get the value of bool
	 * 
	 * @return the value of bool
	 */
	public boolean isBool() {
		return _boolean;
	}

	/**
	 * Set the value of bool
	 * 
	 * @param bool new value of bool
	 */
	public void setBool(final boolean bool) {
		_boolean = bool;
	}

	/**
	 * Get the value of _char
	 * 
	 * @return the value of _char
	 */
	public char get_char() {
		return _char;
	}

	/**
	 * Set the value of _char
	 * 
	 * @param _char new value of _char
	 */
	public void set_char(final char _char) {
		this._char = _char;
	}

	public EnumClass() {
	}

	public double getSecondDouble() {
		return _secondDouble;
	}

	public void setSecondDouble(final double _secondDouble) {
		this._secondDouble = _secondDouble;
	}

	public EnumTester getEnumTester() {
		return enumTester;
	}

	public void setEnumTester(final EnumTester enumTester) {
		this.enumTester = enumTester;
	}

	public internalEnum getInternalEnum() {
		return internalEnum;
	}

	public void setInternalEnum(final internalEnum internalEnum) {
		this.internalEnum = internalEnum;
	}
}
