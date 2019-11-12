/*
 * Copyright (C) 2019 Benny Bottema (benny@bennybottema.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bbottema.loremipsumobjects.helperutils;

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