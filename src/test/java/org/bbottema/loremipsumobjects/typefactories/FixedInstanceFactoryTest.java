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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FixedInstanceFactoryTest {
	@Test
	public void testCreateLoremIpsum() {
		assertThat(new FixedInstanceFactory<Integer>(123).createLoremIpsumObject()).isEqualTo(Integer.valueOf(123));
		assertThat(new FixedInstanceFactory<String>("haha").createLoremIpsumObject()).isEqualTo("haha");
		assertThat(new FixedInstanceFactory<String>(null).createLoremIpsumObject()).isNull();
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