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

import org.bbottema.javareflection.BeanUtils.Visibility;
import org.bbottema.loremipsumobjects.helperutils.SingleTypeEnum;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
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

		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject()).isEqualTo(Visibility.DEFAULT);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject()).isEqualTo(Visibility.PRIVATE);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject()).isEqualTo(Visibility.PROTECTED);
		assertThat(new RandomEnumFactory<>(Visibility.class).createLoremIpsumObject()).isEqualTo(Visibility.PUBLIC);
	}

	@Test
	public void testCreateRandomDummy() {
		SingleTypeEnum singleTypeEnum = new RandomEnumFactory<>(SingleTypeEnum.class).createLoremIpsumObject();
		assertThat(singleTypeEnum).isSameAs(SingleTypeEnum.FOO);
	}
}