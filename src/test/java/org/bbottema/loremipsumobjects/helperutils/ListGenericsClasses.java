package org.bbottema.loremipsumobjects.helperutils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListGenericsClasses {
	
	@Data
	public static class ArrayListIntegerHoldingClass {
		private ArrayList<Integer> integers;
	}
	
	@Data
	public static class ListIntegerHoldingClass {
		private List<Integer> integers;
	}
	
	@Data
	public static class ListMyClassHoldingClass {
		private List<MyClass> myClasses;
	}
	
	@Data
	public static class MyClass {
		private Set<Double> integers;
	}
}