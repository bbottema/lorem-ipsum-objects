/* Copyright (c) 2008 Sven Jacobs

   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
   THE SOFTWARE. 
 */

package de.svenjacobs.loremipsum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LoremIpsumTest {
	private LoremIpsum loremIpsum;

	@Before
	public void setUp() {
		loremIpsum = new LoremIpsum();
	}

	@Test
	public void testWords() {
		String words = loremIpsum.getWords(1);
		assertThat(words, is("Lorem"));

		words = loremIpsum.getWords(25);
		assertThat(
				words,
				is("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"));

		words = loremIpsum.getWords(50);
		assertThat(
				words,
				is("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."));

		words = loremIpsum.getWords(10, 2);
		assertThat(words, is("dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testWordsExceptionBelow() {
		loremIpsum.getWords(50, -1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testWordsExceptionAbove() {
		loremIpsum.getWords(50, 50);
	}

	@Test
	public void testParagraphs() {
		final String paragraphs = loremIpsum.getParagraphs(2);
		assertThat(
				paragraphs,
				is("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\nLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."));
	}
}
