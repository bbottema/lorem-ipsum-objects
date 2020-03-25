package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomUuidFactoryTest {

	@Test
	public void testCreateLoremIpsumObject() {
		final RandomUuidFactory factory = new RandomUuidFactory();

		final LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);
		UUID uuid = UUID.randomUUID();
		when(mock.getRandomUuid()).thenReturn(uuid);

		assertThat(factory.createLoremIpsumObject(null, null, LoremIpsumConfig.builder().build(), null)).isEqualTo(uuid);

		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}
}