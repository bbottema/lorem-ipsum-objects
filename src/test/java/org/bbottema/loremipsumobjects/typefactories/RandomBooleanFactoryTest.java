package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomBooleanFactoryTest {

	private LoremIpsumGenerator mock;

	@Before
	public void setup() {
		mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);
	}

	@After
	public void cleanup() {
		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}

	@Test
	public void test() {
		when(mock.getRandomBoolean())
				.thenReturn(true)
				.thenReturn(true)
				.thenReturn(false)
				.thenReturn(false);

		assertThat(new RandomBooleanFactory().createLoremIpsumObject()).isTrue();
		assertThat(new RandomBooleanFactory().createLoremIpsumObject()).isTrue();
		assertThat(new RandomBooleanFactory().createLoremIpsumObject()).isFalse();
		assertThat(new RandomBooleanFactory().createLoremIpsumObject()).isFalse();
	}
}