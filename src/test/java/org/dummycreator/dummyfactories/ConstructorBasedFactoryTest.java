package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;

import org.dummycreator.ClassBindings;
import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ConstructorBasedFactory}
 */
public class ConstructorBasedFactoryTest {

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
	 * Test for {@link ConstructorBasedFactory#createDummy(java.util.Map, ClassBindings, java.util.List)}. Tests if
	 * <code>new Integer(12345)</code> is invoked correctly.
	 */
	@Test
	public void testCreateDummyInteger()
			throws SecurityException, NoSuchMethodException {
		final Constructor<Integer> constructor = Integer.class.getConstructor(int.class);
		final ConstructorBasedFactory<Integer> factory = new ConstructorBasedFactory<Integer>(constructor);

		EasyMock.expect(mock.getRandomInt()).andReturn(12345);
		EasyMock.replay(mock);

		final Integer dummy = factory.createDummy(new ClassBindings());

		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(12345), dummy);

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link ConstructorBasedFactory#createDummy(java.util.Map, ClassBindings, java.util.List)}. Tests if
	 * <code>new Integer("98765")</code> is invoked correctly.
	 */
	@Test
	public void testCreateDummyString()
			throws SecurityException, NoSuchMethodException {
		final Constructor<Integer> constructor = Integer.class.getConstructor(String.class);
		final ConstructorBasedFactory<Integer> factory = new ConstructorBasedFactory<Integer>(constructor);

		EasyMock.expect(mock.getRandomString()).andReturn("98765");
		EasyMock.replay(mock);

		final Integer dummy = factory.createDummy(new ClassBindings());

		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(98765), dummy);

		EasyMock.verify(mock);
	}
}