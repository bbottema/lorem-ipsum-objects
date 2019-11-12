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

public class InheritedPrimitiveClass extends PrimitiveClass {

	private String secondString;

	/**
	 * Get the value of secondString
	 * 
	 * @return the value of secondString
	 */
	public String getSecondString() {
		return secondString;
	}

	/**
	 * Set the value of secondString
	 * 
	 * @param secondString new value of secondString
	 */
	public void setSecondString(final String secondString) {
		this.secondString = secondString;
	}

}
