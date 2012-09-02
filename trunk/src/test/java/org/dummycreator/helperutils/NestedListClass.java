package org.dummycreator.helperutils;

import java.util.List;

public class NestedListClass {

	public static class NestedSingleListClass {
		public List<Double> numbers;

		public List<Double> getNumbers() {
			return numbers;
		}

		public void setNumbers(final List<Double> numbers) {
			this.numbers = numbers;
		}
	}

	public static class NestedDoubleListClass {
		public List<List<Double>> listsOfNumbers;

		public List<List<Double>> getListsOfNumbers() {
			return listsOfNumbers;
		}

		public void setListsOfNumbers(final List<List<Double>> listsOfNumbers) {
			this.listsOfNumbers = listsOfNumbers;
		}
	}

	public static class NestedTripleListClass {
		public List<List<List<Double>>> listsOflistsOfNumbers;

		public List<List<List<Double>>> getListsOflistsOfNumbers() {
			return listsOflistsOfNumbers;
		}

		public void setListsOflistsOfNumbers(final List<List<List<Double>>> listsOflistsOfNumbers) {
			this.listsOflistsOfNumbers = listsOflistsOfNumbers;
		}
	}

	public static class NestedQuadrupleListClass {
		public List<List<List<List<LoopClass>>>> listsOflistsOflistsOfNumbers;

		public List<List<List<List<LoopClass>>>> getListsOfListsOflistsOfNumbers() {
			return listsOflistsOflistsOfNumbers;
		}

		public void setListsOflistsOflistsOfNumbers(final List<List<List<List<LoopClass>>>> listsOflistsOflistsOfNumbers) {
			this.listsOflistsOflistsOfNumbers = listsOflistsOflistsOfNumbers;
		}
	}
}