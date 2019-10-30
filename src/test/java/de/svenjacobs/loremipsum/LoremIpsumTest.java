package de.svenjacobs.loremipsum;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoremIpsumTest {
	private LoremIpsum loremIpsum;

	@Before
	public void setUp() {
		loremIpsum = new LoremIpsum();
	}

	@Test
	public void testWords() {
		String words = loremIpsum.getWords(1);
		assertThat(words).isEqualTo("Lorem");

		words = loremIpsum.getWords(25);
		assertThat(words).isEqualTo("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At");

		words = loremIpsum.getWords(50);
		assertThat(words).isEqualTo("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

		words = loremIpsum.getWords(10, 2);
		assertThat(words).isEqualTo("dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod");
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
		assertThat(paragraphs).isEqualTo("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\nLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
	}
}
