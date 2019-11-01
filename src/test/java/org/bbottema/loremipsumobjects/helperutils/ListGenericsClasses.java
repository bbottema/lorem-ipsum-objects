package org.bbottema.loremipsumobjects.helperutils;

import lombok.Data;
import lombok.Value;

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
	public static class ListMyDataClassHoldingClass {
		private List<MyDataClass> myClasses;
	}
	
	@Data
	public static class MyDataClass {
		private Set<Double> integers;
	}
	
	@Value
	public static class ListMyValueClassHoldingClass {
		private List<MyValueClass> myClasses;
	}
	@Value
	public static class MyValueClass {
		private double value;
	}
}