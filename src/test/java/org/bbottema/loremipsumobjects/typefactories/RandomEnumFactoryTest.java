package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.javareflection.BeanUtils.Visibility;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.bbottema.loremipsumobjects.helperutils.SingleTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomEnumFactoryTest {

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
	public void testCreateLoremIpsumObject() {
		when(mock.getRandomInt(Visibility.values().length - 1))
				.thenReturn(Visibility.DEFAULT.ordinal())
				.thenReturn(Visibility.PRIVATE.ordinal())
				.thenReturn(Visibility.PROTECTED.ordinal())
				.thenReturn(Visibility.PUBLIC.ordinal());

		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject(null)).isEqualTo(Visibility.DEFAULT);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject(null)).isEqualTo(Visibility.PRIVATE);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject(null)).isEqualTo(Visibility.PROTECTED);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject(null)).isEqualTo(Visibility.PUBLIC);
	}

	@Test
	public void testCreateRandomDummy() {
		SingleTypeEnum singleTypeEnum = new RandomEnumFactory<>(SingleTypeEnum.class).createLoremIpsumObject(null);
		assertThat(singleTypeEnum).isSameAs(SingleTypeEnum.FOO);
	}
}