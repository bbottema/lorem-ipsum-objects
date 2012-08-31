package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class FixedInstanceFactoryTest {

	@Test
	public void testCreateDummy() {
		assertEquals(Integer.valueOf(123), new FixedInstanceFactory<Integer>(123).createDummy(null));
		assertEquals("haha", new FixedInstanceFactory<String>("haha").createDummy(null));
		assertNull(new FixedInstanceFactory<String>(null).createDummy(null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsValidForType() {
		assertTrue(new FixedInstanceFactory<Integer>(123).isValidForType(Integer.class));
		assertTrue(new FixedInstanceFactory(4.3).isValidForType(Number.class));

		try {
			assertTrue(new FixedInstanceFactory(4.3).isValidForType(Integer.class));
			fail("4.3 is not assignable to an integer");
		} catch (IllegalArgumentException e) {
			// ok
		}
	};
}