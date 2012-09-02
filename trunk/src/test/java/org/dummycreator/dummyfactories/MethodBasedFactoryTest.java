package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.dummycreator.ClassBindings;
import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MethodBasedFactory}
 */
public class MethodBasedFactoryTest {

	private RandomCreator mock;

	@Before
	public void setup() {
		mock = EasyMock.createStrictMock(RandomCreator.class);
		RandomCreator.setInstance(mock);
	};

	@After
	public void cleanup() {
		RandomCreator.setInstance(new RandomCreator());
	};

	/**
	 * Test for {@link MethodBasedFactory#createDummy(java.util.Map, ClassBindings, java.util.List)}. Tests if
	 * <code>Integer.valueOf(12345)</code> is invoked correctly.
	 */
	@Test
	public void testCreateDummyInteger()
			throws SecurityException, NoSuchMethodException {
		final Method method = Integer.class.getMethod("valueOf", int.class);
		final MethodBasedFactory<Integer> factory = new MethodBasedFactory<Integer>(method);

		EasyMock.expect(mock.getRandomInt()).andReturn(12345);
		EasyMock.replay(mock);

		final Integer dummy = factory.createDummy(new ClassBindings());

		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(12345), dummy);

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link MethodBasedFactory#createDummy(java.util.Map, ClassBindings, java.util.List)}. Tests if
	 * <code>Integer.valueOf("98765")</code> is invoked correctly.
	 */
	@Test
	public void testCreateDummyString()
			throws SecurityException, NoSuchMethodException {
		final Method method = Integer.class.getMethod("valueOf", String.class);
		final MethodBasedFactory<Integer> factory = new MethodBasedFactory<Integer>(method);

		EasyMock.expect(mock.getRandomString()).andReturn("98765");
		EasyMock.replay(mock);

		final Integer dummy = factory.createDummy(new ClassBindings());

		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(98765), dummy);

		EasyMock.verify(mock);
	}
}