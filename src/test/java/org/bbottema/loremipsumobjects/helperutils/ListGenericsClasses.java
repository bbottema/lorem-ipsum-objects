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
	public static class ListMyValueClassesHoldingClass {
		private List<MyValueClass> myClasses;
		private List<Integer> ints;
		private List<String> strings;
	}
	
	@Value
	public static class MyValueClass {
		private double value;
	}
}