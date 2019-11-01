package org.bbottema.loremipsumobjects.helperutils;

import lombok.Getter;

@Getter
public class TimeoutSimulator {
	private final int constructorValue;
	private Integer method1Value = null;
	private Float method2Value = null;
	
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