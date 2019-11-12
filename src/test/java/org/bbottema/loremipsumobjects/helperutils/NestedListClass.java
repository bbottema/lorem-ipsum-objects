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