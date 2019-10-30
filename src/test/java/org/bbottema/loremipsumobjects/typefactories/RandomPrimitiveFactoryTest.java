package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomPrimitiveFactoryTest {

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
		when(mock.getRandomInt())
				.thenReturn(5)
				.thenReturn(14);
		when(mock.getRandomDouble())
				.thenReturn(5.5)
				.thenReturn(10.55);

		assertThat(new RandomPrimitiveFactory<>(int.class).createLoremIpsumObject(null)).isEqualTo(Integer.valueOf(5));
		assertThat(new RandomPrimitiveFactory<>(double.class).createLoremIpsumObject(null)).isEqualTo(Double.valueOf(5.5));
		assertThat(new RandomPrimitiveFactory<>(Integer.TYPE).createLoremIpsumObject(null)).isEqualTo(Integer.valueOf(14));
		assertThat(new RandomPrimitiveFactory<>(Double.TYPE).createLoremIpsumObject(null)).isEqualTo(Double.valueOf(10.55));
	}

	@Test
	public void testIsValidForType() {
		assertThat(new RandomPrimitiveFactory<>(int.class).isValidForType(int.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(long.class).isValidForType(long.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(float.class).isValidForType(float.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(boolean.class).isValidForType(boolean.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(char.class).isValidForType(char.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(byte.class).isValidForType(byte.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(short.class).isValidForType(short.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(double.class).isValidForType(double.class)).isTrue();
		assertThat(new RandomPrimitiveFactory<>(Object.class).isValidForType(Object.class)).isFalse();
		assertThat(new RandomPrimitiveFactory<>(String.class).isValidForType(String.class)).isFalse();
	}
}