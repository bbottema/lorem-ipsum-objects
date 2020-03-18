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