package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link FixedInstanceFactory}
 */
public class RandomPrimitiveFactoryTest {

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
	 * Test for {@link RandomPrimitiveFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}.
	 */
	@Test
	public void testCreateDummy() {
		EasyMock.expect(mock.getRandomInt()).andReturn(5);
		EasyMock.expect(mock.getRandomDouble()).andReturn(5.5);
		EasyMock.expect(mock.getRandomInt()).andReturn(14);
		EasyMock.expect(mock.getRandomDouble()).andReturn(10.55);
		EasyMock.replay(mock);

		assertEquals(Integer.valueOf(5), new RandomPrimitiveFactory<Integer>(int.class).createDummy(null));
		assertEquals(Double.valueOf(5.5), new RandomPrimitiveFactory<Double>(double.class).createDummy(null));
		assertEquals(Integer.valueOf(14), new RandomPrimitiveFactory<Integer>(Integer.TYPE).createDummy(null));
		assertEquals(Double.valueOf(10.55), new RandomPrimitiveFactory<Double>(Double.TYPE).createDummy(null));

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link RandomPrimitiveFactory#isValidForType(Class)}.
	 */
	@Test
	public void testIsValidForType() {
		assertTrue(new RandomPrimitiveFactory<Integer>(int.class).isValidForType(int.class));
		assertTrue(new RandomPrimitiveFactory<Long>(long.class).isValidForType(long.class));
		assertTrue(new RandomPrimitiveFactory<Float>(float.class).isValidForType(float.class));
		assertTrue(new RandomPrimitiveFactory<Boolean>(boolean.class).isValidForType(boolean.class));
		assertTrue(new RandomPrimitiveFactory<Character>(char.class).isValidForType(char.class));
		assertTrue(new RandomPrimitiveFactory<Byte>(byte.class).isValidForType(byte.class));
		assertTrue(new RandomPrimitiveFactory<Short>(short.class).isValidForType(short.class));
		assertTrue(new RandomPrimitiveFactory<Double>(double.class).isValidForType(double.class));
		assertFalse(new RandomPrimitiveFactory<Object>(Object.class).isValidForType(Object.class));
		assertFalse(new RandomPrimitiveFactory<String>(String.class).isValidForType(String.class));
	}
}