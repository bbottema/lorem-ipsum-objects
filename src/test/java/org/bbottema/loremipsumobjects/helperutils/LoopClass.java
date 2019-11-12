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

public class LoopClass {

	protected LoopClass loopObject;
	protected long id;

	/**
	 * Get the value of id
	 * 
	 * @return the value of id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the value of id
	 * 
	 * @param id new value of id
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * Get the value of loopObject
	 * 
	 * @return the value of loopObject
	 */
	public LoopClass getLoopObject() {
		return loopObject;
	}

	/**
	 * Set the value of loopObject
	 * 
	 * @param loopObject new value of loopObject
	 */
	public void setLoopObject(final LoopClass loopObject) {
		this.loopObject = loopObject;
	}
}
