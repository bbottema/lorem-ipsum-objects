package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConstructorBasedFactoryTest {

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

	/**
	 * Tests if <code>new Integer(12345)</code> is invoked correctly.
	 */
	@Test
	public void testCreateLoremIpsumInteger()
			throws SecurityException, NoSuchMethodException {
		final Constructor<Integer> constructor = Integer.class.getConstructor(int.class);
		final ConstructorBasedFactory<Integer> factory = new ConstructorBasedFactory<Integer>(constructor);

		when(mock.getRandomInt()).thenReturn(12345);

		final Integer dummy = factory.createLoremIpsumObject(new ClassBindings());

		assertThat(dummy.getClass()).isNotNull();
		assertThat(dummy.getClass()).isEqualTo(Integer.class);
		assertThat(dummy).isEqualTo(Integer.valueOf(12345));
	}

	/**
	 * Tests if <code>new Integer("98765")</code> is invoked correctly.
	 */
	@Test
	public void testCreateLoremIpsumString()
			throws SecurityException, NoSuchMethodException {
		final Constructor<Integer> constructor = Integer.class.getConstructor(String.class);
		final ConstructorBasedFactory<Integer> factory = new ConstructorBasedFactory<Integer>(constructor);

		when(mock.getRandomString()).thenReturn("98765");

		final Integer dummy = factory.createLoremIpsumObject(new ClassBindings());

		assertThat(dummy.getClass()).isNotNull();
		assertThat(dummy.getClass()).isEqualTo(Integer.class);
		assertThat(dummy).isEqualTo(Integer.valueOf(98765));
	}
}