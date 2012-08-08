/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is dummyCreator. The Initial Developer of the Original
 * Software is Alexander Muthmann <amuthmann@dev-eth0.de>.
 */
package org.dummycreator;

/**
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
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
    public void set_int(int _int) {
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
    public void set_string(String _string) {
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
    public void set_float(float _float) {
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
    public void set_short(short _short) {
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
    public void set_double(double _double) {
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
    public void set_byte(byte _byte) {
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
    public void set_long(long _long) {
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
    public void setBool(boolean bool) {
	this._boolean = bool;
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
    public void set_char(char _char) {
	this._char = _char;
    }

    public EnumClass() {
    }

    public double getSecondDouble() {
	return _secondDouble;
    }

    public void setSecondDouble(double _secondDouble) {
	this._secondDouble = _secondDouble;
    }

    public EnumTester getEnumTester() {
	return enumTester;
    }

    public void setEnumTester(EnumTester enumTester) {
	this.enumTester = enumTester;
    }

    public internalEnum getInternalEnum() {
	return internalEnum;
    }

    public void setInternalEnum(internalEnum internalEnum) {
	this.internalEnum = internalEnum;
    }
}
