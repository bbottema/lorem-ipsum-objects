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

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class TimeoutSimulator {
	private final int constructorValue;
	@Nullable private Integer method1Value = null;
	@Nullable private Float method2Value = null;
	
	public static boolean contructorTimeoutTriggered;
	public static boolean methodTimeoutTriggered;
	
	public TimeoutSimulator(Integer value) throws InterruptedException {
		System.out.println("constructor 1...");
		waitForConstructorTimeoutUnlessAlreadyTriggered();
		this.constructorValue = value;
		System.out.println("\tconstructorValue set to " + constructorValue);
	}
	
	public TimeoutSimulator(int value) throws InterruptedException {
		System.out.println("constructor 2...");
		waitForConstructorTimeoutUnlessAlreadyTriggered();
		this.constructorValue = value;
		System.out.println("\tconstructorValue set to " + constructorValue);
	}
	
	public void setMethod1Value(Integer value) throws InterruptedException {
		System.out.println("setMethod1Value...");
		waitForMethodTimeoutUnlessAlreadyTriggered();
		this.method1Value = value;
		System.out.println("\tmethod1Value set to " + method1Value);
	}
	
	public void setMethod2Value(Float value) throws InterruptedException {
		System.out.println("setMethod2Value...");
		waitForMethodTimeoutUnlessAlreadyTriggered();
		this.method2Value = value;
		System.out.println("\tmethod2Value set to " + method2Value);
	}
	
	private void waitForConstructorTimeoutUnlessAlreadyTriggered() throws InterruptedException {
		if (!contructorTimeoutTriggered) {
			contructorTimeoutTriggered = true;
			Thread.sleep(Integer.MAX_VALUE);
		}
	}
	
	private void waitForMethodTimeoutUnlessAlreadyTriggered() throws InterruptedException {
		if (!methodTimeoutTriggered) {
			methodTimeoutTriggered = true;
			Thread.sleep(Integer.MAX_VALUE);
		}
	}
}