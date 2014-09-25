package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests for {@link FixedInstanceFactory}
 */
public class FixedInstanceFactoryTest {
	/**
	 * Test for {@link FixedInstanceFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}.
	 */
	@Test
	public void testCreateDummy() {
		assertEquals(Integer.valueOf(123), new FixedInstanceFactory<Integer>(123).createDummy(null));
		assertEquals("haha", new FixedInstanceFactory<String>("haha").createDummy(null));
		assertNull(new FixedInstanceFactory<String>(null).createDummy(null));
	}

	/**
	 * Test for {@link FixedInstanceFactory#isValidForType(Class)}.
	 */
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testIsValidForType() {
		assertTrue(new FixedInstanceFactory<Integer>(123).isValidForType(Integer.class));
		assertTrue(new FixedInstanceFactory(4.3).isValidForType(Number.class));

		try {
			assertTrue(new FixedInstanceFactory(4.3).isValidForType(Integer.class));
			fail("4.3 is not assignable to an integer");
		} catch (final IllegalArgumentException e) {
			// ok
		}
	};
}