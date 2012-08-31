package org.dummycreator.helperutils;

import java.util.Map;

public class NestedMapClass {

	public static class NestedSingleMapClass {
		public Map<Double, LoopClass> numbers;

		public Map<Double, LoopClass> getNumbers() {
			return numbers;
		}

		public void setNumbers(Map<Double, LoopClass> numbers) {
			this.numbers = numbers;
		}

	}

	public static class NestedDoubleMapClass {
		public Map<Map<Integer, NestedDoubleMapClass>, Map<Double, LoopClass>> MapsOfNumbers;

		public Map<Map<Integer, NestedDoubleMapClass>, Map<Double, LoopClass>> getMapsOfNumbers() {
			return MapsOfNumbers;
		}

		public void setMapsOfNumbers(Map<Map<Integer, NestedDoubleMapClass>, Map<Double, LoopClass>> mapsOfNumbers) {
			MapsOfNumbers = mapsOfNumbers;
		}
	}
}