package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomStringFactoryTest {

	@Test
	public void testCreateLoremIpsumObject() {
		final RandomStringFactory factory = new RandomStringFactory();

		final LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);
		when(mock.getRandomString()).thenReturn("1234");

		assertThat(factory.createLoremIpsumObject(null, null, LoremIpsumConfig.builder().build(), null)).isEqualTo("1234");

		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}
}