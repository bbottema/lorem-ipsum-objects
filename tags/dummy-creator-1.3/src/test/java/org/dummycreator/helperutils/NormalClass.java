package org.dummycreator.helperutils;

public class NormalClass {

	private int id;
	private PrimitiveClass primitiveClass;

	private Boolean someBoolean;

	/**
	 * Get the value of id
	 * 
	 * @return the value of id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the value of id
	 * 
	 * @param id new value of id
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Get the value of primitiveClass
	 * 
	 * @return the value of primitiveClass
	 */
	public PrimitiveClass getPrimitiveClass() {
		return primitiveClass;
	}

	/**
	 * Set the value of primitiveClass
	 * 
	 * @param primitiveClass new value of primitiveClass
	 */
	public void setPrimitiveClass(final PrimitiveClass primitiveClass) {
		this.primitiveClass = primitiveClass;
	}

	public Boolean getSomeBoolean() {
		return someBoolean;
	}

	public void setSomeBoolean(final Boolean someBoolean) {
		this.someBoolean = someBoolean;
	}
}