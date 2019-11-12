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

import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodBasedFactoryTest {

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
	 * Tests if <code>Integer.valueOf(12345)</code> is invoked correctly.
	 */
	@Test
	public void testCreateLoremIpsumInteger()
			throws SecurityException, NoSuchMethodException {
		final Method method = Integer.class.getMethod("valueOf", int.class);
		final MethodBasedFactory<Integer> factory = new MethodBasedFactory<>(method);

		when(mock.getRandomInt()).thenReturn(12345);

		final Integer dummy = factory.createLoremIpsumObject();

		assertThat(dummy.getClass()).isNotNull();
		assertThat(dummy.getClass()).isEqualTo(Integer.class);
		assertThat(dummy).isEqualTo(Integer.valueOf(12345));
	}

	/**
	 * Tests if <code>Integer.valueOf("98765")</code> is invoked correctly.
	 */
	@Test
	public void testCreateLoremIpsumString()
			throws SecurityException, NoSuchMethodException {
		final Method method = Integer.class.getMethod("valueOf", String.class);
		final MethodBasedFactory<Integer> factory = new MethodBasedFactory<>(method);

		when(mock.getRandomString()).thenReturn("98765");

		final Integer dummy = factory.createLoremIpsumObject();

		assertThat(dummy.getClass()).isNotNull();
		assertThat(dummy.getClass()).isEqualTo(Integer.class);
		assertThat(dummy).isEqualTo(Integer.valueOf(98765));
	}
}