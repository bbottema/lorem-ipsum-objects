package org.bbottema.loremipsumobjects.typefactories;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FixedInstanceFactoryTest {
	@Test
	public void testCreateLoremIpsum() {
		assertThat(new FixedInstanceFactory<Integer>(123).createLoremIpsumObject(null)).isEqualTo(Integer.valueOf(123));
		assertThat(new FixedInstanceFactory<String>("haha").createLoremIpsumObject(null)).isEqualTo("haha");
		assertThat(new FixedInstanceFactory<String>(null).createLoremIpsumObject(null)).isNull();
	}

	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testIsValidForType() {
		assertThat(new FixedInstanceFactory<Integer>(123).isValidForType(Integer.class)).isTrue();
		assertThat(new FixedInstanceFactory(4.3).isValidForType(Number.class)).isTrue();

		try {
			assertThat(new FixedInstanceFactory(4.3).isValidForType(Integer.class)).isTrue();
			fail("4.3 is not assignable to an integer");
		} catch (final IllegalArgumentException e) {
			// ok
		}
	}
}